package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun getAllCompaniesFlow(): Flow<List<Company>>

    @Query("SELECT * FROM companies WHERE published = 1 ORDER BY name ASC")
    fun getPublishedCompaniesFlow(): Flow<List<Company>>

    @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
    suspend fun getCompanyById(id: Long): Company?

    @Query("SELECT * FROM companies WHERE slug = :slug LIMIT 1")
    suspend fun getCompanyBySlug(slug: String): Company?

    @Query("SELECT * FROM companies LIMIT 1")
    suspend fun getFirstCompany(): Company?

    @Query("SELECT * FROM companies WHERE slug = :slug LIMIT 1")
    fun getCompanyBySlugFlow(slug: String): Flow<Company?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: Company): Long

    @Update
    suspend fun updateCompany(company: Company)

    @Delete
    suspend fun deleteCompany(company: Company)
}

@Dao
interface DesignerDao {
    @Query("SELECT * FROM designers ORDER BY name ASC")
    fun getAllDesignersFlow(): Flow<List<Designer>>

    @Query("SELECT * FROM designers WHERE id = :id LIMIT 1")
    suspend fun getDesignerById(id: Long): Designer?

    @Query("SELECT * FROM designers WHERE slug = :slug LIMIT 1")
    suspend fun getDesignerBySlug(slug: String): Designer?

    @Query("SELECT * FROM designers LIMIT 1")
    suspend fun getFirstDesigner(): Designer?

    @Query("SELECT * FROM designers WHERE slug = :slug LIMIT 1")
    fun getDesignerBySlugFlow(slug: String): Flow<Designer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesigner(designer: Designer): Long

    @Update
    suspend fun updateDesigner(designer: Designer)

    @Delete
    suspend fun deleteDesigner(designer: Designer)
}

@Dao
interface ThemeDao {
    @Query("SELECT * FROM themes ORDER BY createdAt DESC")
    fun getAllThemesFlow(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE published = 1 ORDER BY createdAt DESC")
    fun getPublishedThemesFlow(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE published = 1 AND featured = 1 ORDER BY updatedAt DESC")
    fun getFeaturedThemesFlow(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE published = 1 ORDER BY (views * 2 + downloads * 5) DESC LIMIT 10")
    fun getTrendingThemesFlow(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE published = 1 ORDER BY updatedAt DESC")
    fun getRecentlyUpdatedThemesFlow(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE companyId = :companyId AND published = 1 ORDER BY createdAt DESC")
    fun getThemesByCompanyFlow(companyId: Long): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE designerId = :designerId AND published = 1 ORDER BY createdAt DESC")
    fun getThemesByDesignerFlow(designerId: Long): Flow<List<ThemeEntity>>

    @Query("SELECT COUNT(*) FROM themes WHERE companyId = :companyId")
    suspend fun countThemesForCompany(companyId: Long): Int

    @Query("SELECT COUNT(*) FROM themes WHERE designerId = :designerId")
    suspend fun countThemesForDesigner(designerId: Long): Int

    @Query("SELECT * FROM themes WHERE id = :id LIMIT 1")
    suspend fun getThemeById(id: Long): ThemeEntity?

    @Query("SELECT * FROM themes WHERE slug = :slug LIMIT 1")
    suspend fun getThemeBySlug(slug: String): ThemeEntity?

    @Query("SELECT * FROM themes WHERE slug = :slug LIMIT 1")
    fun getThemeBySlugFlow(slug: String): Flow<ThemeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: ThemeEntity): Long

    @Update
    suspend fun updateTheme(theme: ThemeEntity)

    @Delete
    suspend fun deleteTheme(theme: ThemeEntity)

    @Query("UPDATE themes SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("UPDATE themes SET downloads = downloads + 1 WHERE id = :id")
    suspend fun incrementDownloads(id: Long)
}

@Dao
interface ThemeVersionDao {
    @Query("SELECT * FROM theme_versions WHERE themeId = :themeId ORDER BY createdAt DESC")
    fun getVersionsForThemeFlow(themeId: Long): Flow<List<ThemeVersion>>

    @Query("SELECT * FROM theme_versions WHERE themeId = :themeId AND published = 1 ORDER BY createdAt DESC")
    fun getPublishedVersionsForThemeFlow(themeId: Long): Flow<List<ThemeVersion>>

    @Query("SELECT * FROM theme_versions WHERE themeId = :themeId AND published = 1 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestPublishedVersion(themeId: Long): ThemeVersion?

    @Query("SELECT * FROM theme_versions ORDER BY updatedAt DESC LIMIT 20")
    fun getRecentlyUpdatedVersionsFlow(): Flow<List<ThemeVersion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: ThemeVersion): Long

    @Update
    suspend fun updateVersion(version: ThemeVersion)

    @Delete
    suspend fun deleteVersion(version: ThemeVersion)
}

@Dao
interface ThemePreviewDao {
    @Query("SELECT * FROM theme_previews WHERE themeId = :themeId ORDER BY sortOrder ASC")
    fun getPreviewsForThemeFlow(themeId: Long): Flow<List<ThemePreview>>

    @Query("SELECT * FROM theme_previews WHERE themeId = :themeId ORDER BY sortOrder ASC")
    suspend fun getPreviewsForTheme(themeId: Long): List<ThemePreview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreview(preview: ThemePreview): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreviews(previews: List<ThemePreview>)

    @Update
    suspend fun updatePreview(preview: ThemePreview)

    @Delete
    suspend fun deletePreview(preview: ThemePreview)

    @Query("DELETE FROM theme_previews WHERE themeId = :themeId")
    suspend fun deletePreviewsForTheme(themeId: Long)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE themeId = :themeId AND approved = 1 ORDER BY createdAt DESC")
    fun getApprovedCommentsForThemeFlow(themeId: Long): Flow<List<Comment>>

    @Query("SELECT * FROM comments ORDER BY createdAt DESC")
    fun getAllCommentsFlow(): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Update
    suspend fun updateComment(comment: Comment)

    @Delete
    suspend fun deleteComment(comment: Comment)
}

@Dao
interface AdminSettingDao {
    @Query("SELECT * FROM admin_settings")
    fun getAllSettingsFlow(): Flow<List<AdminSetting>>

    @Query("SELECT value FROM admin_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AdminSetting)
}

@Dao
interface SubAdminDao {
    @Query("SELECT * FROM sub_admins ORDER BY createdAt DESC")
    fun getAllSubAdminsFlow(): Flow<List<SubAdmin>>

    @Query("SELECT * FROM sub_admins WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getSubAdminByEmail(email: String): SubAdmin?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubAdmin(subAdmin: SubAdmin): Long

    @Update
    suspend fun updateSubAdmin(subAdmin: SubAdmin)

    @Delete
    suspend fun deleteSubAdmin(subAdmin: SubAdmin)

    @Query("UPDATE sub_admins SET isActive = :isActive WHERE id = :id")
    suspend fun toggleActive(id: Long, isActive: Boolean)
}

@Dao
interface ThemeBattleDao {
    @Query("SELECT * FROM theme_battles ORDER BY createdAt DESC")
    fun getAllBattlesFlow(): Flow<List<ThemeBattle>>

    @Query("SELECT * FROM theme_battles WHERE active = 1 ORDER BY createdAt DESC")
    fun getActiveBattlesFlow(): Flow<List<ThemeBattle>>

    @Query("SELECT * FROM theme_battles WHERE id = :id LIMIT 1")
    suspend fun getBattleById(id: Long): ThemeBattle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBattle(battle: ThemeBattle): Long

    @Update
    suspend fun updateBattle(battle: ThemeBattle)

    @Delete
    suspend fun deleteBattle(battle: ThemeBattle)

    @Query("UPDATE theme_battles SET themeAVotes = themeAVotes + 1 WHERE id = :battleId")
    suspend fun voteForThemeA(battleId: Long)

    @Query("UPDATE theme_battles SET themeBVotes = themeBVotes + 1 WHERE id = :battleId")
    suspend fun voteForThemeB(battleId: Long)

    @Query("UPDATE theme_battles SET active = :active WHERE id = :id")
    suspend fun setBattleActive(id: Long, active: Boolean)
}

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun getAllCollectionsFlow(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun getPublicCollectionsFlow(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE slug = :slug LIMIT 1")
    fun getCollectionBySlugFlow(slug: String): Flow<CollectionEntity?>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getCollectionById(id: Long): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Query("SELECT themeId FROM collection_themes WHERE collectionId = :collectionId ORDER BY sortOrder ASC")
    fun getThemeIdsForCollectionFlow(collectionId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionTheme(collectionTheme: CollectionTheme)

    @Query("DELETE FROM collection_themes WHERE collectionId = :collectionId AND themeId = :themeId")
    suspend fun removeThemeFromCollection(collectionId: Long, themeId: Long)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC LIMIT 50")
    fun getAllNotificationsFlow(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE `read` = 0")
    fun getUnreadCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Query("UPDATE notifications SET `read` = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET `read` = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}

@Dao
interface FavoriteDao {
    @Query("SELECT themeId FROM favorites")
    fun getFavoriteThemeIdsFlow(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE themeId = :themeId)")
    fun isFavoriteFlow(themeId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(record: FavoriteRecord)

    @Query("DELETE FROM favorites WHERE themeId = :themeId")
    suspend fun removeFavorite(themeId: Long)
}

@Dao
interface BattleVoteDao {
    @Query("SELECT votedThemeId FROM battle_votes WHERE battleId = :battleId LIMIT 1")
    suspend fun getVotedThemeId(battleId: Long): Long?

    @Query("SELECT * FROM battle_votes")
    fun getAllVotesFlow(): Flow<List<BattleVoteRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordVote(vote: BattleVoteRecord)
}

@Dao
interface ThemeRatingDao {
    @Query("SELECT * FROM theme_ratings WHERE themeId = :themeId ORDER BY createdAt DESC")
    fun getRatingsForThemeFlow(themeId: Long): Flow<List<ThemeRating>>

    @Query("SELECT AVG(stars) FROM theme_ratings WHERE themeId = :themeId")
    fun getAverageRatingFlow(themeId: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM theme_ratings WHERE themeId = :themeId")
    fun getRatingsCountFlow(themeId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: ThemeRating): Long

    @Delete
    suspend fun deleteRating(rating: ThemeRating)
}

@Dao
interface DesignerFollowDao {
    @Query("SELECT EXISTS(SELECT 1 FROM designer_follows WHERE designerId = :designerId)")
    fun isFollowingFlow(designerId: Long): Flow<Boolean>

    @Query("SELECT designerId FROM designer_follows")
    fun getFollowedDesignerIdsFlow(): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM designer_follows WHERE designerId = :designerId")
    fun getFollowerCountFlow(designerId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followDesigner(record: DesignerFollowRecord)

    @Query("DELETE FROM designer_follows WHERE designerId = :designerId")
    suspend fun unfollowDesigner(designerId: Long)
}

