package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Company::class,
        Designer::class,
        ThemeEntity::class,
        ThemeVersion::class,
        ThemePreview::class,
        Comment::class,
        AdminSetting::class,
        SubAdmin::class,
        ThemeBattle::class,
        CollectionEntity::class,
        CollectionTheme::class,
        NotificationItem::class,
        FavoriteRecord::class,
        BattleVoteRecord::class,
        ThemeRating::class,
        DesignerFollowRecord::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun designerDao(): DesignerDao
    abstract fun themeDao(): ThemeDao
    abstract fun themeVersionDao(): ThemeVersionDao
    abstract fun themePreviewDao(): ThemePreviewDao
    abstract fun commentDao(): CommentDao
    abstract fun adminSettingDao(): AdminSettingDao
    abstract fun subAdminDao(): SubAdminDao
    abstract fun themeBattleDao(): ThemeBattleDao
    abstract fun collectionDao(): CollectionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun battleVoteDao(): BattleVoteDao
    abstract fun themeRatingDao(): ThemeRatingDao
    abstract fun designerFollowDao(): DesignerFollowDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "s18_theme_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDatabase(database)
                    }
                }
            }
        }

        suspend fun seedDatabase(database: AppDatabase) {
            val companyDao = database.companyDao()
            val designerDao = database.designerDao()
            val themeDao = database.themeDao()
            val versionDao = database.themeVersionDao()
            val previewDao = database.themePreviewDao()
            val commentDao = database.commentDao()
            val settingDao = database.adminSettingDao()

            // 1. Initial Companies
            val honorId = companyDao.insertCompany(
                Company(
                    name = "HONOR",
                    slug = "honor",
                    logoUrl = "https://images.unsplash.com/photo-1616469829941-c7200edec809?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&q=80",
                    description = "Dynamic MagicOS themes with fluid glass and curved elegance"
                )
            )
            val huaweiId = companyDao.insertCompany(
                Company(
                    name = "HUAWEI",
                    slug = "huawei",
                    logoUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1200&q=80",
                    description = "EMUI & HarmonyOS premium icon packs and dark layouts"
                )
            )
            val infinixId = companyDao.insertCompany(
                Company(
                    name = "INFINIX",
                    slug = "infinix",
                    logoUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1200&q=80",
                    description = "XOS high-energy neon and dynamic glass styles"
                )
            )
            val tecnoId = companyDao.insertCompany(
                Company(
                    name = "TECNO",
                    slug = "tecno",
                    logoUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=1200&q=80",
                    description = "HiOS artistic themes and customized widgets"
                )
            )
            val xiaomiId = companyDao.insertCompany(
                Company(
                    name = "Xiaomi",
                    slug = "xiaomi",
                    logoUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff025a5?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&q=80",
                    description = "HyperOS & MIUI top-rated community and minimalist themes"
                )
            )
            val redmiId = companyDao.insertCompany(
                Company(
                    name = "Redmi",
                    slug = "redmi",
                    logoUrl = "https://images.unsplash.com/photo-1580927752452-89d86da3fa0a?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1519638399535-1b036603ac77?w=1200&q=80",
                    description = "Redmi HyperOS tailored AMOLED dark themes and live clocks"
                )
            )
            val pocoId = companyDao.insertCompany(
                Company(
                    name = "POCO",
                    slug = "poco",
                    logoUrl = "https://images.unsplash.com/photo-1534972195531-a756b1126f24?w=400&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1200&q=80",
                    description = "High-octane gaming and futuristic cyber aesthetics for POCO"
                )
            )

            // 2. Initial Designers
            val s18DesignerId = designerDao.insertDesigner(
                Designer(
                    name = "S18_STUDIO",
                    slug = "s18-studio",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&q=80",
                    bio = "Official S18 Design Team crafting premium fluid & dark luxury mobile themes.",
                    telegramUrl = "https://t.me/s18theme",
                    tiktokUrl = "https://tiktok.com/@s18theme",
                    websiteUrl = "https://s18theme.dev"
                )
            )
            val liquidDesignerId = designerDao.insertDesigner(
                Designer(
                    name = "LiquidCraft",
                    slug = "liquid-craft",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&q=80",
                    bio = "Pioneering glassmorphism and liquid physics interfaces for modern devices.",
                    telegramUrl = "https://t.me/liquidcraft",
                    tiktokUrl = "https://tiktok.com/@liquidcraft"
                )
            )
            val neoDesignerId = designerDao.insertDesigner(
                Designer(
                    name = "NeoMatrix",
                    slug = "neo-matrix",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&q=80",
                    bio = "Dark cyberpunk aesthetics, pure OLED blacks, and high-contrast glowing accents.",
                    telegramUrl = "https://t.me/neomatrix",
                    tiktokUrl = "https://tiktok.com/@neomatrix"
                )
            )

            val now = System.currentTimeMillis()

            // 3. Themes: App starts with NO pre-seeded random themes per user request ("حذف كل الثيمات العشوائية/الافتراضية الموجودة حاليًا، بحيث يبدأ التطبيق بدون أي ثيمات")
            // The admin can add new themes via the Admin Dashboard.

            // 4. Default Admin Settings
            settingDao.setSetting(AdminSetting("site_name", "S18_THEME"))
            settingDao.setSetting(AdminSetting("site_desc", "Beautiful Themes. One Place."))
            settingDao.setSetting(AdminSetting("default_lang", "AR"))
            settingDao.setSetting(AdminSetting("new_badge_days", "7"))
            settingDao.setSetting(AdminSetting("show_public_counters", "true"))
            settingDao.setSetting(AdminSetting("telegram_url", "https://t.me/s18theme"))
            settingDao.setSetting(AdminSetting("tiktok_url", "https://tiktok.com/@s18theme"))

            // 5. Default Sub-Admins
            val subAdminDao = database.subAdminDao()
            subAdminDao.insertSubAdmin(
                SubAdmin(
                    email = "editor@s18.cyber",
                    displayName = "محرر المحتوى الرقمي",
                    role = "مدير الثيمات (Theme Manager)",
                    pin = "2026",
                    canManageThemes = true,
                    canManageCompanies = false,
                    canModerateComments = true,
                    canManageDesigners = false,
                    isActive = true
                )
            )
            subAdminDao.insertSubAdmin(
                SubAdmin(
                    email = "mod.cyber@s18.local",
                    displayName = "مشرف المجتمع والتعليقات",
                    role = "مشرف التعليقات (Moderator)",
                    pin = "8899",
                    canManageThemes = false,
                    canManageCompanies = false,
                    canModerateComments = true,
                    canManageDesigners = false,
                    isActive = true
                )
            )

            // 6. Theme Battles and Collections will be populated as admin creates themes
            val notificationDao = database.notificationDao()
            notificationDao.insertNotification(
                NotificationItem(
                    title = "Welcome to S18_THEME v5.0",
                    titleAr = "أهلاً بك في منصة S18_THEME الإصدار 5.0",
                    body = "Themes & Wallpapers platform ready. Add your themes via Admin Panel!",
                    bodyAr = "منصة S18_THEME جاهزة بدون ثيمات افتراضية. يمكنك البدء بإضافة ثيماتك وخلفياتك من لوحة الإدارة.",
                    type = "system"
                )
            )
        }
    }
}
