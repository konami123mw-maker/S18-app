package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Company
import com.example.data.Designer
import com.example.data.ThemeEntity
import com.example.data.ThemeRepository
import com.example.data.ThemeVersion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThemeDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: ThemeRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ThemeRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testCompanyAndThemeCreation() = runBlocking {
        val companyId = repository.saveCompany(
            Company(name = "HONOR", slug = "honor", description = "HONOR MagicOS")
        )
        val designerId = repository.saveDesigner(
            Designer(name = "Ahmed S18", slug = "ahmed-s18", telegramUrl = "https://t.me/s18theme")
        )

        val themeId = repository.saveTheme(
            theme = ThemeEntity(
                companyId = companyId,
                designerId = designerId,
                name = "Cyberpunk Neon",
                slug = "cyberpunk-neon",
                coverImageUrl = "theme_preview_liquid",
                published = true
            ),
            version = ThemeVersion(
                themeId = 0L,
                version = "1.0",
                downloadUrl = "https://t.me/s18theme/download",
                changelog = "Initial release"
            )
        )

        val themes = repository.fullThemesFlow.first()
        assertEquals(1, themes.size)
        assertEquals("Cyberpunk Neon", themes[0].theme.name)
        assertEquals("HONOR", themes[0].company.name)
        assertEquals("Ahmed S18", themes[0].designer.name)
        assertEquals("1.0", themes[0].latestVersion?.version)
    }
}
