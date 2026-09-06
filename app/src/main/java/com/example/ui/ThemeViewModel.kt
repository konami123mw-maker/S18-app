package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CollectionEntity
import com.example.data.Comment
import com.example.data.Company
import com.example.data.Designer
import com.example.data.NotificationItem
import com.example.data.SubAdmin
import com.example.data.ThemeBattle
import com.example.data.ThemeEntity
import com.example.data.ThemeFullItem
import com.example.data.ThemePreview
import com.example.data.ThemeRating
import com.example.data.ThemeRepository
import com.example.data.ThemeVersion
import com.example.util.ThemeDownloader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data object Search : Screen()
    data class CompanyDetails(val slug: String) : Screen()
    data class ThemeDetails(val slug: String) : Screen()
    data class DesignerDetails(val slug: String) : Screen()
    data object Updates : Screen()
    data object Battles : Screen()
    data object Collections : Screen()
    data class CollectionDetails(val slug: String) : Screen()
    data class Admin(val tab: AdminTab = AdminTab.DASHBOARD) : Screen()
    data object Favorites : Screen()
    data object Categories : Screen()
    data object SupportContact : Screen()
    data object Support : Screen()
    data object AboutUs : Screen()
    data object Settings : Screen()
    data object PrivacyPolicy : Screen()
    data object RateApp : Screen()
    data object Profile : Screen()
    data object DeviceCompatibilityGuide : Screen()
}

enum class AdminTab {
    DASHBOARD, THEMES, WALLPAPERS, COMPANIES, DESIGNERS, COMMENTS, SUB_ADMINS, BATTLES, COLLECTIONS, ANALYTICS, UPDATES, BACKUPS, SETTINGS
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("s18_theme_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = ThemeRepository(database)

    // Language State
    private val _language = MutableStateFlow(
        if (prefs.getString("lang", "ar") == "ar") AppLanguage.AR else AppLanguage.EN
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Dark Mode State (Default: Dark Mode as requested in specs #8)
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Admin Appearance Customization State (Accent Color & UI Card Style)
    private val _accentColorHex = MutableStateFlow(prefs.getString("accent_color", "#00E5FF") ?: "#00E5FF")
    val accentColorHex: StateFlow<String> = _accentColorHex.asStateFlow()

    private val _uiCardStyle = MutableStateFlow(prefs.getString("ui_card_style", "cyber") ?: "cyber")
    val uiCardStyle: StateFlow<String> = _uiCardStyle.asStateFlow()

    fun updateAppAppearance(accentHex: String, cardStyle: String) {
        _accentColorHex.value = accentHex
        _uiCardStyle.value = cardStyle
        prefs.edit()
            .putString("accent_color", accentHex)
            .putString("ui_card_style", cardStyle)
            .apply()
    }

    // Visitor ID (Cyber Identifier: e.g. S18-CYBER-8924)
    private val savedVisitorId = prefs.getString("visitor_id", null) ?: run {
        val randomDigits = (1000..9999).random()
        val newId = "S18-CYBER-$randomDigits"
        prefs.edit().putString("visitor_id", newId).apply()
        newId
    }
    private val _visitorId = MutableStateFlow(savedVisitorId)
    val visitorId: StateFlow<String> = _visitorId.asStateFlow()

    // Preview Mode (Phone Preview vs Desktop Preview)
    private val _isDesktopPreview = MutableStateFlow(prefs.getBoolean("desktop_preview", false))
    val isDesktopPreview: StateFlow<Boolean> = _isDesktopPreview.asStateFlow()

    // PWA Install Banner Dismissed
    private val _isPwaDismissed = MutableStateFlow(prefs.getBoolean("pwa_dismissed", false))
    val isPwaDismissed: StateFlow<Boolean> = _isPwaDismissed.asStateFlow()

    // Favorites (slugs of saved themes)
    private val initialFavorites = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    private val _favorites = MutableStateFlow<Set<String>>(initialFavorites)
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    // Navigation Backstack State
    private val _screen = MutableStateFlow<Screen>(Screen.Home)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Quick Preview
    private val _quickPreviewTheme = MutableStateFlow<ThemeFullItem?>(null)
    val quickPreviewTheme: StateFlow<ThemeFullItem?> = _quickPreviewTheme.asStateFlow()

    // Feedback message
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Admin Auth State
    private val _isAdminAuthenticated = MutableStateFlow(prefs.getBoolean("is_admin", false))
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _isSuperAdmin = MutableStateFlow(prefs.getBoolean("is_super_admin", true))
    val isSuperAdmin: StateFlow<Boolean> = _isSuperAdmin.asStateFlow()

    private val _currentSubAdmin = MutableStateFlow<SubAdmin?>(null)
    val currentSubAdmin: StateFlow<SubAdmin?> = _currentSubAdmin.asStateFlow()

    init {
        val isAdmin = prefs.getBoolean("is_admin", false)
        val isSuper = prefs.getBoolean("is_super_admin", true)
        val subAdminId = prefs.getLong("sub_admin_id", -1L)
        if (isAdmin && !isSuper && subAdminId != -1L) {
            viewModelScope.launch {
                val subAdmins = repository.allSubAdmins.firstOrNull() ?: emptyList()
                _currentSubAdmin.value = subAdmins.find { it.id == subAdminId }
            }
        }
    }

    // Data streams
    val fullThemes: StateFlow<List<ThemeFullItem>> = repository.fullThemesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedCompanies: StateFlow<List<Company>> = repository.publishedCompanies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompanies: StateFlow<List<Company>> = repository.allCompanies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDesigners: StateFlow<List<Designer>> = repository.allDesigners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allComments: StateFlow<List<Comment>> = repository.allComments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubAdmins: StateFlow<List<SubAdmin>> = repository.allSubAdmins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBattles: StateFlow<List<ThemeBattle>> = repository.allBattles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBattles: StateFlow<List<ThemeBattle>> = repository.activeBattles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCollections: StateFlow<List<CollectionEntity>> = repository.allCollections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publicCollections: StateFlow<List<CollectionEntity>> = repository.publicCollections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val favoriteThemeIds: StateFlow<List<Long>> = repository.favoriteThemeIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mood-Based Discovery State
    private val _selectedMood = MutableStateFlow<String?>(null)
    val selectedMood: StateFlow<String?> = _selectedMood.asStateFlow()

    fun selectMood(mood: String?) {
        _selectedMood.value = mood
    }

    // Notifications Sheet State
    private val _showNotificationsSheet = MutableStateFlow(false)
    val showNotificationsSheet: StateFlow<Boolean> = _showNotificationsSheet.asStateFlow()

    fun toggleNotificationsSheet(show: Boolean) {
        _showNotificationsSheet.value = show
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // Battle operations
    fun voteInBattle(battleId: Long, themeId: Long) {
        viewModelScope.launch {
            repository.voteInBattle(battleId, themeId)
            _feedbackMessage.value = "تم تسجيل تصويتك بنجاح!"
        }
    }

    fun saveBattle(battle: ThemeBattle, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveBattle(battle)
            _feedbackMessage.value = "تم حفظ بيانات المعركة"
            onComplete()
        }
    }

    fun deleteBattle(battle: ThemeBattle, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteBattle(battle)
            _feedbackMessage.value = "تم حذف المعركة"
            onComplete()
        }
    }

    // Collection operations
    fun saveCollection(collection: CollectionEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveCollection(collection)
            _feedbackMessage.value = "تم حفظ بيانات المجموعة"
            onComplete()
        }
    }

    fun deleteCollection(collection: CollectionEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
            _feedbackMessage.value = "تم حذف المجموعة"
            onComplete()
        }
    }

    // Favorite toggle
    fun toggleFavoriteTheme(themeId: Long) {
        viewModelScope.launch {
            val isFav = favoriteThemeIds.value.contains(themeId)
            repository.toggleFavorite(themeId, isFav)
        }
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == AppLanguage.EN) AppLanguage.AR else AppLanguage.EN
        _language.value = newLang
        prefs.edit().putString("lang", newLang.code).apply()
    }

    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        prefs.edit().putBoolean("dark_mode", newMode).apply()
    }

    fun togglePreviewMode() {
        val newMode = !_isDesktopPreview.value
        _isDesktopPreview.value = newMode
        prefs.edit().putBoolean("desktop_preview", newMode).apply()
    }

    fun dismissPwaBanner() {
        _isPwaDismissed.value = true
        prefs.edit().putBoolean("pwa_dismissed", true).apply()
    }

    fun toggleFavorite(slug: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(slug)) {
            current.remove(slug)
        } else {
            current.add(slug)
        }
        _favorites.value = current
        prefs.edit().putStringSet("favorites", current).apply()
    }

    fun isFavorite(slug: String): Boolean {
        return _favorites.value.contains(slug)
    }

    fun navigateTo(newScreen: Screen) {
        screenBackStack.add(_screen.value)
        _screen.value = newScreen
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            val prev = screenBackStack.removeAt(screenBackStack.size - 1)
            _screen.value = prev
            return true
        }
        if (_screen.value !is Screen.Home) {
            _screen.value = Screen.Home
            return true
        }
        return false
    }

    fun showQuickPreview(item: ThemeFullItem) {
        _quickPreviewTheme.value = item
    }

    fun dismissQuickPreview() {
        _quickPreviewTheme.value = null
    }

    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }

    fun viewTheme(themeId: Long) {
        viewModelScope.launch {
            repository.incrementViews(themeId)
        }
    }

    fun downloadTheme(context: Context, item: ThemeFullItem, version: ThemeVersion? = null) {
        val targetVersion = version ?: item.latestVersion
        val url = targetVersion?.downloadUrl?.trim() ?: ""

        if (url.isEmpty() || (!url.startsWith("https://", ignoreCase = true) && !url.startsWith("http://", ignoreCase = true))) {
            val err = S18Strings.get("download_error", _language.value)
            _feedbackMessage.value = err
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            repository.recordDownload(item.theme.id)
        }

        val versionStr = targetVersion?.version ?: "1.0"
        ThemeDownloader.startRealDownload(
            context = context,
            url = url,
            themeTitle = item.theme.name,
            versionStr = versionStr
        ) { status ->
            _feedbackMessage.value = status
        }
    }

    fun postComment(themeId: Long, nickname: String, commentText: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.postComment(themeId, nickname, commentText)
            if (success) {
                _feedbackMessage.value = S18Strings.get("comment_submitted_notice", _language.value)
            }
            onResult(success)
        }
    }

    // --- Admin Operations ---

    fun loginAdminWithCredentials(
        identifier: String,
        passwordOrPin: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val cleanId = identifier.trim()
            val cleanPass = passwordOrPin.trim()

            // 1. Super Admin authentication:
            val isSuperAdminMatch = cleanPass == "1234" || cleanPass == "admin123" || cleanPass == "admin" ||
                    (cleanPass.isEmpty() && (cleanId == "1234" || cleanId == "admin123" || cleanId == "admin")) ||
                    (cleanId.equals("admin", ignoreCase = true) && (cleanPass == "1234" || cleanPass.isEmpty())) ||
                    (cleanId.equals("admin@s18theme.com", ignoreCase = true) && (cleanPass == "1234" || cleanPass == "admin"))

            if (isSuperAdminMatch) {
                _isAdminAuthenticated.value = true
                _isSuperAdmin.value = true
                _currentSubAdmin.value = null
                prefs.edit()
                    .putBoolean("is_admin", true)
                    .putBoolean("is_super_admin", true)
                    .remove("sub_admin_id")
                    .apply()
                onResult(true, null)
                return@launch
            }

            // 2. Sub-Admin authentication by Mock Email & Password:
            val searchEmail = if (cleanId.isNotEmpty()) cleanId else cleanPass
            val subAdmin = repository.getSubAdminByEmail(searchEmail)
            if (subAdmin != null) {
                if (!subAdmin.isActive) {
                    val msg = if (_language.value == AppLanguage.AR)
                        "حساب الأدمن الفرعي غير نشط، يرجى التواصل مع الأدمن الأساسي"
                    else
                        "Sub-Admin account is inactive. Contact Super Admin."
                    onResult(false, msg)
                    return@launch
                }

                if (subAdmin.pin == cleanPass || (cleanPass.isEmpty() && subAdmin.pin == cleanId)) {
                    _isAdminAuthenticated.value = true
                    _isSuperAdmin.value = false
                    _currentSubAdmin.value = subAdmin
                    prefs.edit()
                        .putBoolean("is_admin", true)
                        .putBoolean("is_super_admin", false)
                        .putLong("sub_admin_id", subAdmin.id)
                        .apply()
                    onResult(true, null)
                    return@launch
                } else {
                    val msg = if (_language.value == AppLanguage.AR)
                        "كلمة المرور غير صحيحة لحساب هذا المشرف الفرعي"
                    else
                        "Incorrect password for this sub-admin"
                    onResult(false, msg)
                    return@launch
                }
            }

            val msg = if (_language.value == AppLanguage.AR)
                "بيانات تسجيل الدخول غير صحيحة. تحقق من الإيميل الوهمي وكلمة المرور أو رمز الأدمن الأساسي"
            else
                "Invalid credentials. Check mock email and password or Super Admin PIN."
            onResult(false, msg)
        }
    }

    fun loginAdmin(pin: String): Boolean {
        // Default PIN: 1234
        if (pin == "1234" || pin == "admin123" || pin == "admin") {
            _isAdminAuthenticated.value = true
            _isSuperAdmin.value = true
            _currentSubAdmin.value = null
            prefs.edit().putBoolean("is_admin", true).putBoolean("is_super_admin", true).remove("sub_admin_id").apply()
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        _isSuperAdmin.value = true
        _currentSubAdmin.value = null
        prefs.edit().putBoolean("is_admin", false).putBoolean("is_super_admin", true).remove("sub_admin_id").apply()
        _screen.value = Screen.Home
    }

    fun saveTheme(
        theme: ThemeEntity,
        version: ThemeVersion? = null,
        previews: List<ThemePreview>? = null,
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.saveTheme(theme, version, previews)
            _feedbackMessage.value = S18Strings.get("theme_updated_success", _language.value)
            onComplete(id)
        }
    }

    fun createTheme(
        theme: ThemeEntity,
        versions: List<ThemeVersion> = emptyList(),
        previews: List<ThemePreview> = emptyList(),
        onComplete: (Long) -> Unit = {}
    ) {
        saveTheme(theme, versions.firstOrNull(), previews, onComplete)
    }

    fun deleteTheme(theme: ThemeEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTheme(theme)
            _feedbackMessage.value = S18Strings.get("theme_deleted_success", _language.value)
            onComplete()
        }
    }

    fun deleteTheme(themeId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val item = fullThemes.value.firstOrNull { it.theme.id == themeId }
            if (item != null) {
                repository.deleteTheme(item.theme)
            }
            _feedbackMessage.value = S18Strings.get("theme_deleted_success", _language.value)
            onComplete()
        }
    }

    fun duplicateTheme(themeId: Long, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = repository.duplicateTheme(themeId)
            onComplete(newId)
        }
    }

    fun saveCompany(company: Company, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.saveCompany(company)
            onComplete(id)
        }
    }

    fun deleteCompany(company: Company, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteCompany(company)
            if (res.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, S18Strings.get("company_delete_blocked", _language.value))
            }
        }
    }

    fun saveDesigner(designer: Designer, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.saveDesigner(designer)
            onComplete(id)
        }
    }

    fun deleteDesigner(designer: Designer, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteDesigner(designer)
            onComplete()
        }
    }

    fun saveVersion(version: ThemeVersion, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.saveVersion(version)
            onComplete(id)
        }
    }

    fun deleteVersion(version: ThemeVersion, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteVersion(version)
            onComplete()
        }
    }

    fun approveComment(comment: Comment) {
        viewModelScope.launch {
            repository.approveComment(comment)
        }
    }

    fun hideComment(comment: Comment) {
        viewModelScope.launch {
            repository.hideComment(comment)
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            repository.deleteComment(comment)
        }
    }

    fun saveSubAdmin(subAdmin: SubAdmin, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.saveSubAdmin(subAdmin)
            _feedbackMessage.value = "تم حفظ بيانات المشرف بنجاح"
            onComplete(id)
        }
    }

    fun deleteSubAdmin(subAdmin: SubAdmin, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSubAdmin(subAdmin)
            _feedbackMessage.value = "تم حذف حساب المشرف"
            onComplete()
        }
    }

    fun toggleSubAdminActive(id: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleSubAdminActive(id, isActive)
        }
    }

    fun toggleSubAdminActive(subAdmin: SubAdmin) {
        toggleSubAdminActive(subAdmin.id, !subAdmin.isActive)
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportBackupJson()
            onResult(json)
        }
    }

    // --- Theme Ratings & Community Reviews (Feature 4) ---
    fun getRatingsForTheme(themeId: Long): Flow<List<ThemeRating>> =
        repository.getRatingsForTheme(themeId)

    fun getAverageRatingForTheme(themeId: Long): Flow<Double?> =
        repository.getAverageRatingForTheme(themeId)

    fun getRatingsCountForTheme(themeId: Long): Flow<Int> =
        repository.getRatingsCountForTheme(themeId)

    fun submitThemeRating(
        themeId: Long,
        stars: Int,
        nickname: String,
        review: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val nick = nickname.trim().ifBlank { "User-${(1000..9999).random()}" }
            val rating = ThemeRating(
                themeId = themeId,
                stars = stars.coerceIn(1, 5),
                nickname = nick,
                review = review.trim()
            )
            val id = repository.submitRating(rating)
            if (id > 0) {
                _feedbackMessage.value = if (_language.value == AppLanguage.AR) "شكراً لك! تم إضافة تقييمك بنجاح ★" else "Thank you! Rating submitted ★"
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    // --- Verified Creators & Following System (Feature 6) ---
    fun isFollowingDesigner(designerId: Long): Flow<Boolean> =
        repository.isFollowingDesigner(designerId)

    fun getFollowerCountForDesigner(designerId: Long): Flow<Int> =
        repository.getFollowerCountForDesigner(designerId)

    fun getFollowersCountForDesigner(designerId: Long): Flow<Int> =
        repository.getFollowerCountForDesigner(designerId)

    fun toggleFollowDesigner(designerId: Long, isCurrentlyFollowing: Boolean) {
        viewModelScope.launch {
            repository.toggleFollowDesigner(designerId, isCurrentlyFollowing)
            _feedbackMessage.value = if (isCurrentlyFollowing) {
                if (_language.value == AppLanguage.AR) "تم إلغاء متابعة المصمم" else "Unfollowed designer"
            } else {
                if (_language.value == AppLanguage.AR) "تمت متابعة المصمم بنجاح ✓" else "Following designer ✓"
            }
        }
    }

    fun toggleFollowDesigner(designerId: Long) {
        viewModelScope.launch {
            val isCurrently = isFollowingDesigner(designerId).firstOrNull() ?: false
            toggleFollowDesigner(designerId, isCurrently)
        }
    }
}
