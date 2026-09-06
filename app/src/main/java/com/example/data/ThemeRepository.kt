package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ThemeRepository(
    private val database: AppDatabase
) {
    private val companyDao = database.companyDao()
    private val designerDao = database.designerDao()
    private val themeDao = database.themeDao()
    private val versionDao = database.themeVersionDao()
    private val previewDao = database.themePreviewDao()
    private val commentDao = database.commentDao()
    private val settingDao = database.adminSettingDao()
    private val subAdminDao = database.subAdminDao()
    private val battleDao = database.themeBattleDao()
    private val collectionDao = database.collectionDao()
    private val notificationDao = database.notificationDao()
    private val favoriteDao = database.favoriteDao()
    private val battleVoteDao = database.battleVoteDao()
    private val themeRatingDao = database.themeRatingDao()
    private val designerFollowDao = database.designerFollowDao()

    val publishedCompanies: Flow<List<Company>> = companyDao.getPublishedCompaniesFlow()
    val allCompanies: Flow<List<Company>> = companyDao.getAllCompaniesFlow()

    val allDesigners: Flow<List<Designer>> = designerDao.getAllDesignersFlow()

    val allThemes: Flow<List<ThemeEntity>> = themeDao.getAllThemesFlow()
    val publishedThemes: Flow<List<ThemeEntity>> = themeDao.getPublishedThemesFlow()
    val featuredThemes: Flow<List<ThemeEntity>> = themeDao.getFeaturedThemesFlow()
    val trendingThemes: Flow<List<ThemeEntity>> = themeDao.getTrendingThemesFlow()
    val recentlyUpdatedThemes: Flow<List<ThemeEntity>> = themeDao.getRecentlyUpdatedThemesFlow()

    val allComments: Flow<List<Comment>> = commentDao.getAllCommentsFlow()
    val allSettings: Flow<List<AdminSetting>> = settingDao.getAllSettingsFlow()
    val allSubAdmins: Flow<List<SubAdmin>> = subAdminDao.getAllSubAdminsFlow()

    val allBattles: Flow<List<ThemeBattle>> = battleDao.getAllBattlesFlow()
    val activeBattles: Flow<List<ThemeBattle>> = battleDao.getActiveBattlesFlow()
    val allCollections: Flow<List<CollectionEntity>> = collectionDao.getAllCollectionsFlow()
    val publicCollections: Flow<List<CollectionEntity>> = collectionDao.getPublicCollectionsFlow()
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotificationsFlow()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCountFlow()
    val favoriteThemeIds: Flow<List<Long>> = favoriteDao.getFavoriteThemeIdsFlow()

    /**
     * Combines all published themes with company, designer, and latest version
     */
    val fullThemesFlow: Flow<List<ThemeFullItem>> = combine(
        themeDao.getAllThemesFlow(),
        companyDao.getAllCompaniesFlow(),
        designerDao.getAllDesignersFlow()
    ) { themes, companies, designers ->
        val companyMap = companies.associateBy { it.id }
        val designerMap = designers.associateBy { it.id }

        themes.map { theme ->
            val comp = companyMap[theme.companyId] ?: Company(id = theme.companyId, name = "Generic", slug = "generic")
            val des = designerMap[theme.designerId] ?: Designer(id = theme.designerId, name = "S18_STUDIO", slug = "s18-studio")
            val latestVer = versionDao.getLatestPublishedVersion(theme.id)
            val previews = previewDao.getPreviewsForTheme(theme.id)
            ThemeFullItem(
                theme = theme,
                company = comp,
                designer = des,
                latestVersion = latestVer,
                previews = previews
            )
        }
    }

    suspend fun getThemeBySlug(slug: String): ThemeFullItem? = withContext(Dispatchers.IO) {
        val theme = themeDao.getThemeBySlug(slug) ?: return@withContext null
        val comp = companyDao.getCompanyById(theme.companyId) ?: Company(id = theme.companyId, name = "Brand", slug = "brand")
        val des = designerDao.getDesignerById(theme.designerId) ?: Designer(id = theme.designerId, name = "Designer", slug = "designer")
        val latestVer = versionDao.getLatestPublishedVersion(theme.id)
        val previews = previewDao.getPreviewsForTheme(theme.id)
        ThemeFullItem(theme, comp, des, latestVer, previews)
    }

    suspend fun getCompanyBySlug(slug: String): Company? = withContext(Dispatchers.IO) {
        companyDao.getCompanyBySlug(slug)
    }

    suspend fun getDesignerBySlug(slug: String): Designer? = withContext(Dispatchers.IO) {
        designerDao.getDesignerBySlug(slug)
    }

    fun getVersionsForTheme(themeId: Long): Flow<List<ThemeVersion>> {
        return versionDao.getVersionsForThemeFlow(themeId)
    }

    fun getPreviewsForTheme(themeId: Long): Flow<List<ThemePreview>> {
        return previewDao.getPreviewsForThemeFlow(themeId)
    }

    fun getApprovedCommentsForTheme(themeId: Long): Flow<List<Comment>> {
        return commentDao.getApprovedCommentsForThemeFlow(themeId)
    }

    suspend fun incrementViews(themeId: Long) = withContext(Dispatchers.IO) {
        themeDao.incrementViews(themeId)
    }

    suspend fun recordDownload(themeId: Long) = withContext(Dispatchers.IO) {
        themeDao.incrementDownloads(themeId)
    }

    suspend fun postComment(themeId: Long, nickname: String, commentText: String): Boolean = withContext(Dispatchers.IO) {
        val trimmedNick = nickname.trim().take(50)
        val trimmedComment = commentText.trim().take(500)
        if (trimmedNick.isEmpty() || trimmedComment.isEmpty()) return@withContext false

        val comment = Comment(
            themeId = themeId,
            nickname = trimmedNick,
            comment = trimmedComment,
            approved = false // Specs: New comments must default to approved = false
        )
        commentDao.insertComment(comment)
        true
    }

    // --- Admin Operations ---

    suspend fun saveTheme(
        theme: ThemeEntity,
        version: ThemeVersion? = null,
        previews: List<ThemePreview>? = null
    ): Long = withContext(Dispatchers.IO) {
        // 1. Resolve safe and guaranteed valid companyId
        val validCompanyId = if (theme.companyId > 0 && companyDao.getCompanyById(theme.companyId) != null) {
            theme.companyId
        } else {
            companyDao.getFirstCompany()?.id ?: companyDao.insertCompany(
                Company(
                    name = "S18 Studio",
                    slug = "s18-studio",
                    description = "Official S18 Theme Studio"
                )
            )
        }

        // 2. Resolve safe and guaranteed valid designerId
        val validDesignerId = if (theme.designerId > 0 && designerDao.getDesignerById(theme.designerId) != null) {
            theme.designerId
        } else {
            designerDao.getFirstDesigner()?.id ?: designerDao.insertDesigner(
                Designer(
                    name = "S18 Creator",
                    slug = "s18-creator",
                    bio = "Official S18 Theme Designer"
                )
            )
        }

        // 3. Resolve safe unique slug
        var safeSlug = theme.slug.trim().ifBlank {
            theme.name.trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "theme" }
        }
        val existingThemeWithSlug = themeDao.getThemeBySlug(safeSlug)
        if (existingThemeWithSlug != null && existingThemeWithSlug.id != theme.id) {
            safeSlug = "$safeSlug-${System.currentTimeMillis() % 10000}"
        }

        val sanitizedTheme = theme.copy(
            companyId = validCompanyId,
            designerId = validDesignerId,
            slug = safeSlug
        )

        val themeId = if (sanitizedTheme.id == 0L) {
            themeDao.insertTheme(sanitizedTheme.copy(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
        } else {
            themeDao.updateTheme(sanitizedTheme.copy(updatedAt = System.currentTimeMillis()))
            sanitizedTheme.id
        }

        if (version != null) {
            if (version.id == 0L) {
                versionDao.insertVersion(version.copy(themeId = themeId, createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            } else {
                versionDao.updateVersion(version.copy(themeId = themeId, updatedAt = System.currentTimeMillis()))
            }
        }

        if (previews != null) {
            previewDao.deletePreviewsForTheme(themeId)
            previewDao.insertPreviews(previews.mapIndexed { idx, p ->
                p.copy(themeId = themeId, sortOrder = idx + 1)
            })
        }

        themeId
    }

    suspend fun deleteTheme(theme: ThemeEntity) = withContext(Dispatchers.IO) {
        themeDao.deleteTheme(theme)
    }

    suspend fun duplicateTheme(sourceThemeId: Long): Long = withContext(Dispatchers.IO) {
        val source = themeDao.getThemeById(sourceThemeId) ?: return@withContext 0L
        val uniqueSlug = "${source.slug}-copy-${System.currentTimeMillis() % 10000}"
        val newTheme = source.copy(
            id = 0,
            name = "${source.name} (Copy)",
            slug = uniqueSlug,
            published = false, // Defaults to Draft as required
            views = 0,
            downloads = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val newId = themeDao.insertTheme(newTheme)

        // Copy previews
        val previews = previewDao.getPreviewsForTheme(sourceThemeId)
        previewDao.insertPreviews(previews.map { it.copy(id = 0, themeId = newId) })

        // Copy latest version as draft
        val latestVer = versionDao.getLatestPublishedVersion(sourceThemeId)
        if (latestVer != null) {
            versionDao.insertVersion(latestVer.copy(id = 0, themeId = newId, published = false))
        }

        newId
    }

    suspend fun saveCompany(company: Company): Long = withContext(Dispatchers.IO) {
        if (company.id == 0L) {
            companyDao.insertCompany(company.copy(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
        } else {
            companyDao.updateCompany(company.copy(updatedAt = System.currentTimeMillis()))
            company.id
        }
    }

    suspend fun deleteCompany(company: Company): Result<Unit> = withContext(Dispatchers.IO) {
        val count = themeDao.countThemesForCompany(company.id)
        if (count > 0) {
            Result.failure(IllegalStateException("Company has $count themes"))
        } else {
            companyDao.deleteCompany(company)
            Result.success(Unit)
        }
    }

    suspend fun saveDesigner(designer: Designer): Long = withContext(Dispatchers.IO) {
        if (designer.id == 0L) {
            designerDao.insertDesigner(designer.copy(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
        } else {
            designerDao.updateDesigner(designer.copy(updatedAt = System.currentTimeMillis()))
            designer.id
        }
    }

    suspend fun deleteDesigner(designer: Designer) = withContext(Dispatchers.IO) {
        designerDao.deleteDesigner(designer)
    }

    suspend fun saveVersion(version: ThemeVersion): Long = withContext(Dispatchers.IO) {
        val id = if (version.id == 0L) {
            versionDao.insertVersion(version.copy(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
        } else {
            versionDao.updateVersion(version.copy(updatedAt = System.currentTimeMillis()))
            version.id
        }
        // Update parent theme's updatedAt to trigger "Recently Updated"
        val theme = themeDao.getThemeById(version.themeId)
        if (theme != null) {
            themeDao.updateTheme(theme.copy(updatedAt = System.currentTimeMillis()))
        }
        id
    }

    suspend fun deleteVersion(version: ThemeVersion) = withContext(Dispatchers.IO) {
        versionDao.deleteVersion(version)
    }

    suspend fun approveComment(comment: Comment) = withContext(Dispatchers.IO) {
        commentDao.updateComment(comment.copy(approved = true, updatedAt = System.currentTimeMillis()))
    }

    suspend fun hideComment(comment: Comment) = withContext(Dispatchers.IO) {
        commentDao.updateComment(comment.copy(approved = false, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        commentDao.deleteComment(comment)
    }

    suspend fun saveSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        settingDao.setSetting(AdminSetting(key, value))
    }

    suspend fun getSetting(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        settingDao.getSettingValue(key) ?: defaultValue
    }

    /**
     * Real JSON Database Backup Export
     */
    suspend fun saveSubAdmin(subAdmin: SubAdmin): Long = withContext(Dispatchers.IO) {
        val cleanEmail = subAdmin.email.trim()
        val existing = subAdminDao.getSubAdminByEmail(cleanEmail)
        if (existing != null && subAdmin.id == 0L) {
            val updated = subAdmin.copy(id = existing.id, email = cleanEmail)
            subAdminDao.updateSubAdmin(updated)
            existing.id
        } else if (subAdmin.id == 0L) {
            subAdminDao.insertSubAdmin(subAdmin.copy(email = cleanEmail))
        } else {
            subAdminDao.updateSubAdmin(subAdmin.copy(email = cleanEmail))
            subAdmin.id
        }
    }

    suspend fun deleteSubAdmin(subAdmin: SubAdmin) = withContext(Dispatchers.IO) {
        subAdminDao.deleteSubAdmin(subAdmin)
    }

    suspend fun toggleSubAdminActive(id: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
        subAdminDao.toggleActive(id, isActive)
    }

    suspend fun getSubAdminByEmail(email: String): SubAdmin? = withContext(Dispatchers.IO) {
        subAdminDao.getSubAdminByEmail(email.trim())
    }

    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", "1.0")
        root.put("exported_at", System.currentTimeMillis())

        val themes = themeDao.getAllThemesFlow().firstOrNull() ?: emptyList()
        val themeArray = JSONArray()
        for (t in themes) {
            val tObj = JSONObject().apply {
                put("id", t.id)
                put("companyId", t.companyId)
                put("designerId", t.designerId)
                put("name", t.name)
                put("slug", t.slug)
                put("description", t.description)
                put("coverImageUrl", t.coverImageUrl)
                put("tags", t.tags)
                put("featured", t.featured)
                put("published", t.published)
                put("views", t.views)
                put("downloads", t.downloads)
            }
            themeArray.put(tObj)
        }
        root.put("themes", themeArray)

        val companies = companyDao.getAllCompaniesFlow().firstOrNull() ?: emptyList()
        val compArray = JSONArray()
        for (c in companies) {
            val cObj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("slug", c.slug)
                put("logoUrl", c.logoUrl)
                put("description", c.description)
                put("published", c.published)
            }
            compArray.put(cObj)
        }
        root.put("companies", compArray)

        root.toString(2)
    }

    // --- Battles ---
    suspend fun saveBattle(battle: ThemeBattle): Long = withContext(Dispatchers.IO) {
        if (battle.id == 0L) battleDao.insertBattle(battle)
        else {
            battleDao.updateBattle(battle)
            battle.id
        }
    }

    suspend fun deleteBattle(battle: ThemeBattle) = withContext(Dispatchers.IO) {
        battleDao.deleteBattle(battle)
    }

    suspend fun voteInBattle(battleId: Long, themeId: Long) = withContext(Dispatchers.IO) {
        val existing = battleVoteDao.getVotedThemeId(battleId)
        if (existing == null) {
            val battle = battleDao.getBattleById(battleId) ?: return@withContext
            if (themeId == battle.themeAId) {
                battleDao.voteForThemeA(battleId)
            } else if (themeId == battle.themeBId) {
                battleDao.voteForThemeB(battleId)
            }
            battleVoteDao.recordVote(BattleVoteRecord(battleId, themeId))
        }
    }

    suspend fun getVotedThemeForBattle(battleId: Long): Long? = withContext(Dispatchers.IO) {
        battleVoteDao.getVotedThemeId(battleId)
    }

    // --- Collections ---
    suspend fun saveCollection(collection: CollectionEntity): Long = withContext(Dispatchers.IO) {
        if (collection.id == 0L) collectionDao.insertCollection(collection)
        else {
            collectionDao.updateCollection(collection)
            collection.id
        }
    }

    suspend fun deleteCollection(collection: CollectionEntity) = withContext(Dispatchers.IO) {
        collectionDao.deleteCollection(collection)
    }

    fun getThemeIdsForCollection(collectionId: Long): Flow<List<Long>> =
        collectionDao.getThemeIdsForCollectionFlow(collectionId)

    suspend fun addThemeToCollection(collectionId: Long, themeId: Long) = withContext(Dispatchers.IO) {
        collectionDao.insertCollectionTheme(CollectionTheme(collectionId, themeId))
    }

    suspend fun removeThemeFromCollection(collectionId: Long, themeId: Long) = withContext(Dispatchers.IO) {
        collectionDao.removeThemeFromCollection(collectionId, themeId)
    }

    // --- Notifications ---
    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun createNotification(notification: NotificationItem): Long = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(notification)
    }

    // --- Favorites ---
    fun isThemeFavorite(themeId: Long): Flow<Boolean> = favoriteDao.isFavoriteFlow(themeId)

    suspend fun toggleFavorite(themeId: Long, isFav: Boolean) = withContext(Dispatchers.IO) {
        if (isFav) {
            favoriteDao.removeFavorite(themeId)
        } else {
            favoriteDao.addFavorite(FavoriteRecord(themeId))
        }
    }

    // --- Theme Ratings & Reviews ---
    fun getRatingsForTheme(themeId: Long): Flow<List<ThemeRating>> =
        themeRatingDao.getRatingsForThemeFlow(themeId)

    fun getAverageRatingForTheme(themeId: Long): Flow<Double?> =
        themeRatingDao.getAverageRatingFlow(themeId)

    fun getRatingsCountForTheme(themeId: Long): Flow<Int> =
        themeRatingDao.getRatingsCountFlow(themeId)

    suspend fun submitRating(rating: ThemeRating): Long = withContext(Dispatchers.IO) {
        themeRatingDao.insertRating(rating)
    }

    suspend fun deleteRating(rating: ThemeRating) = withContext(Dispatchers.IO) {
        themeRatingDao.deleteRating(rating)
    }

    // --- Designer Follows ---
    fun isFollowingDesigner(designerId: Long): Flow<Boolean> =
        designerFollowDao.isFollowingFlow(designerId)

    fun getFollowedDesignerIds(): Flow<List<Long>> =
        designerFollowDao.getFollowedDesignerIdsFlow()

    fun getFollowerCountForDesigner(designerId: Long): Flow<Int> =
        designerFollowDao.getFollowerCountFlow(designerId)

    suspend fun toggleFollowDesigner(designerId: Long, isCurrentlyFollowing: Boolean) = withContext(Dispatchers.IO) {
        if (isCurrentlyFollowing) {
            designerFollowDao.unfollowDesigner(designerId)
        } else {
            designerFollowDao.followDesigner(DesignerFollowRecord(designerId))
        }
    }
}
