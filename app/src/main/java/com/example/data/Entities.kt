package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "companies",
    indices = [Index(value = ["slug"], unique = true)]
)
data class Company(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val slug: String,
    val logoUrl: String = "",
    val bannerUrl: String = "",
    val description: String = "",
    val published: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "designers",
    indices = [Index(value = ["slug"], unique = true)]
)
data class Designer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val slug: String,
    val avatarUrl: String = "",
    val bio: String = "",
    val isVerified: Boolean = true,
    val badgeTitle: String = "Verified Creator",
    val telegramUrl: String = "",
    val tiktokUrl: String = "",
    val websiteUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "themes",
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Designer::class,
            parentColumns = ["id"],
            childColumns = ["designerId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [
        Index(value = ["slug"], unique = true),
        Index(value = ["companyId"]),
        Index(value = ["designerId"]),
        Index(value = ["published"]),
        Index(value = ["featured"]),
        Index(value = ["createdAt"]),
        Index(value = ["updatedAt"])
    ]
)
data class ThemeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyId: Long = 1,
    val designerId: Long = 1,
    val name: String,
    val slug: String,
    val description: String = "",
    val coverImageUrl: String = "",
    val tags: String = "", // Comma-separated tags
    val featured: Boolean = false,
    val published: Boolean = true,
    val publishAt: Long = 0L,
    val views: Int = 0,
    val downloads: Int = 0,
    val accentColor: String = "#00E5FF",
    val comingSoon: Boolean = false,
    val launchDate: Long = 0L,
    val installationGuide: String = "",
    val beforeImageUrl: String = "",
    val afterImageUrl: String = "",
    val likes: Int = 0,
    val favorites: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "theme_versions",
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["themeId"]),
        Index(value = ["published"])
    ]
)
data class ThemeVersion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val themeId: Long,
    val version: String,
    val downloadUrl: String,
    val changelog: String = "",
    val releaseDate: String = "",
    val published: Boolean = true,
    val publishAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "theme_previews",
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["themeId"]),
        Index(value = ["sortOrder"])
    ]
)
data class ThemePreview(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val themeId: Long,
    val themeVersionId: Long? = null,
    val imageUrl: String,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["themeId"]),
        Index(value = ["approved"])
    ]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val themeId: Long,
    val nickname: String,
    val comment: String,
    val approved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_settings")
data class AdminSetting(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(
    tableName = "sub_admins",
    indices = [Index(value = ["email"], unique = true)]
)
data class SubAdmin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String, // Can be fictitious/mock like editor@s18.cyber, mod1@s18.mock
    val displayName: String,
    val role: String = "Moderator", // "Theme Manager", "Moderator", "Content Editor"
    val pin: String = "1234",
    val canManageThemes: Boolean = true,
    val canManageCompanies: Boolean = false,
    val canModerateComments: Boolean = true,
    val canManageDesigners: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Composite UI model combining Theme with its Company, Designer, latest Version, and Previews
 */
data class ThemeFullItem(
    val theme: ThemeEntity,
    val company: Company,
    val designer: Designer,
    val latestVersion: ThemeVersion?,
    val previews: List<ThemePreview>
)

@Entity(
    tableName = "theme_battles",
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeAId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeBId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["themeAId"]), Index(value = ["themeBId"])]
)
data class ThemeBattle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val titleAr: String = "",
    val themeAId: Long,
    val themeBId: Long,
    val themeAVotes: Int = 0,
    val themeBVotes: Int = 0,
    val active: Boolean = true,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + 7 * 86400000L,
    val createdAt: Long = System.currentTimeMillis()
)

data class BattleFullItem(
    val battle: ThemeBattle,
    val themeA: ThemeFullItem?,
    val themeB: ThemeFullItem?
)

@Entity(
    tableName = "collections",
    indices = [Index(value = ["slug"], unique = true)]
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nameAr: String = "",
    val slug: String,
    val description: String = "",
    val coverImageUrl: String = "",
    val isPublic: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "collection_themes",
    primaryKeys = ["collectionId", "themeId"],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["collectionId"]), Index(value = ["themeId"])]
)
data class CollectionTheme(
    val collectionId: Long,
    val themeId: Long,
    val sortOrder: Int = 0
)

data class CollectionFullItem(
    val collection: CollectionEntity,
    val themes: List<ThemeFullItem>
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val titleAr: String = "",
    val body: String,
    val bodyAr: String = "",
    val type: String = "info", // "update", "battle", "release", "system"
    val relatedThemeSlug: String = "",
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteRecord(
    @PrimaryKey val themeId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "battle_votes")
data class BattleVoteRecord(
    @PrimaryKey val battleId: Long,
    val votedThemeId: Long,
    val votedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "theme_ratings",
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = ["id"],
            childColumns = ["themeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["themeId"])]
)
data class ThemeRating(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val themeId: Long,
    val stars: Int, // 1 to 5
    val nickname: String,
    val review: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "designer_follows")
data class DesignerFollowRecord(
    @PrimaryKey val designerId: Long,
    val followedAt: Long = System.currentTimeMillis()
)

