package com.example.util

import android.os.Build
import androidx.compose.ui.graphics.Color
import com.example.ui.AppLanguage

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val detectedOs: String
)

data class CompatibilityResult(
    val isCompatible: Boolean,
    val scorePercentage: Int,
    val title: String,
    val description: String,
    val badgeColor: Color,
    val targetBrand: String
)

data class InstallationStep(
    val stepNumber: Int,
    val title: String,
    val description: String
)

data class InstallationGuide(
    val brandTitle: String,
    val brandSlug: String,
    val fileExtension: String,
    val targetFolder: String,
    val steps: List<InstallationStep>,
    val tips: List<String>
)

object DeviceCompatibility {

    fun getDeviceInfo(): DeviceInfo {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val release = Build.VERSION.RELEASE
        val sdk = Build.VERSION.SDK_INT

        val osName = when {
            manufacturer.contains("Xiaomi", ignoreCase = true) || manufacturer.contains("Redmi", ignoreCase = true) || manufacturer.contains("Poco", ignoreCase = true) -> {
                if (sdk >= 34) "HyperOS" else "MIUI"
            }
            manufacturer.contains("Honor", ignoreCase = true) -> "MagicOS"
            manufacturer.contains("Huawei", ignoreCase = true) -> if (sdk >= 31) "HarmonyOS / EMUI" else "EMUI"
            manufacturer.contains("Infinix", ignoreCase = true) -> "XOS"
            manufacturer.contains("Tecno", ignoreCase = true) -> "HiOS"
            manufacturer.contains("Samsung", ignoreCase = true) -> "One UI"
            manufacturer.contains("Realme", ignoreCase = true) -> "realme UI"
            manufacturer.contains("Oppo", ignoreCase = true) -> "ColorOS"
            manufacturer.contains("Vivo", ignoreCase = true) -> "OriginOS / Funtouch"
            else -> "Android $release"
        }

        return DeviceInfo(
            manufacturer = manufacturer,
            model = model,
            androidVersion = release,
            sdkInt = sdk,
            detectedOs = osName
        )
    }

    fun checkThemeCompatibility(
        companySlug: String,
        companyName: String,
        themeTags: String,
        language: AppLanguage
    ): CompatibilityResult {
        val device = getDeviceInfo()
        val devManuf = device.manufacturer.lowercase()
        val comp = companySlug.lowercase()

        val isDirectMatch = when {
            comp.contains("honor") && (devManuf.contains("honor") || devManuf.contains("huawei")) -> true
            comp.contains("huawei") && (devManuf.contains("huawei") || devManuf.contains("honor")) -> true
            (comp.contains("xiaomi") || comp.contains("poco") || comp.contains("redmi")) &&
                    (devManuf.contains("xiaomi") || devManuf.contains("poco") || devManuf.contains("redmi")) -> true
            comp.contains("infinix") && devManuf.contains("infinix") -> true
            comp.contains("tecno") && devManuf.contains("tecno") -> true
            else -> false
        }

        val tagMatch = themeTags.contains(devManuf, ignoreCase = true) ||
                themeTags.contains(device.detectedOs, ignoreCase = true)

        val isCompatible = isDirectMatch || tagMatch
        val score = when {
            isDirectMatch -> 100
            tagMatch -> 90
            else -> 60
        }

        val title = if (language == AppLanguage.AR) {
            if (isDirectMatch) "متوافق 100% مع جهازك (${device.detectedOs})"
            else if (tagMatch) "توافق عالي مع واجهة جهازك (${device.detectedOs})"
            else "مصمم خصيصاً لأجهزة $companyName (قد يحتاج لأداة تحويل)"
        } else {
            if (isDirectMatch) "100% Compatible with your ${device.detectedOs}"
            else if (tagMatch) "High Compatibility with your ${device.detectedOs}"
            else "Designed for $companyName (Conversion or Theme App required)"
        }

        val description = if (language == AppLanguage.AR) {
            if (isDirectMatch) "تم التحقق: هذا الثيم مخصص لنظام ${device.detectedOs} على هاتف ${device.model}. يعمل بسلاسة تامة مع مركز التحكم وشاشة القفل."
            else "هاتفك الحالي هو ${device.manufacturer} ${device.model} بنظام ${device.detectedOs}. يمكنك تطبيق الثيم عبر دليل التثبيت الإرشادي."
        } else {
            if (isDirectMatch) "Verified: Custom-tuned for ${device.detectedOs} on your ${device.model}. Lock screen, control center, and icons fit perfectly."
            else "Your current device is ${device.manufacturer} ${device.model} (${device.detectedOs}). Follow our step-by-step guide to install."
        }

        val badgeColor = when {
            score >= 95 -> Color(0xFF00E676) // Emerald Green
            score >= 80 -> Color(0xFF00E5FF) // Cyan
            else -> Color(0xFFFFB300) // Amber
        }

        return CompatibilityResult(
            isCompatible = isCompatible,
            scorePercentage = score,
            title = title,
            description = description,
            badgeColor = badgeColor,
            targetBrand = companyName
        )
    }

    fun getInstallationGuide(companySlug: String, language: AppLanguage): InstallationGuide {
        val slug = companySlug.lowercase()
        return when {
            slug.contains("honor") -> {
                if (language == AppLanguage.AR) {
                    InstallationGuide(
                        brandTitle = "دليل تثبيت ثيمات HONOR (MagicOS)",
                        brandSlug = "honor",
                        fileExtension = ".hwt",
                        targetFolder = "Honor/Themes أو Huawei/Themes",
                        steps = listOf(
                            InstallationStep(1, "تنزيل ملف الثيم", "اضغط على زر التنزيل واحفظ ملف الثيم بامتداد .hwt في وحدة التخزين."),
                            InstallationStep(2, "نقل الملف إلى مجلد السمات", "باستخدام تطبيق مدير الملفات، انسخ ملف .hwt إلى المسار: وحدة التخزين الداخلية > Honor > Themes (أو مجلد Themes الرئيسي)."),
                            InstallationStep(3, "تطبيق الثيم", "افتح تطبيق 'السمات' (Themes) الرسمي في جهازك، وانتقل إلى تبويب 'أنا' (Me) > 'السمات الخاصة بي'."),
                            InstallationStep(4, "إعادة تشغيل الجهاز", "اختر الثيم واضغط 'تطبيق'. يُفضل إعادة تشغيل الهاتف لضمان ظهور كافة الأيقونات وويدجات شاشة القفل.")
                        ),
                        tips = listOf(
                            "إذا لم يظهر مجلد Honor/Themes، قم بإنشاء مجلد باسم 'Themes' في الذاكرة الداخلية يدوياً.",
                            "تأكد من إعطاء تطبيق السمات إذن الوصول لملفات التخزين."
                        )
                    )
                } else {
                    InstallationGuide(
                        brandTitle = "HONOR (MagicOS) Installation Guide",
                        brandSlug = "honor",
                        fileExtension = ".hwt",
                        targetFolder = "Honor/Themes",
                        steps = listOf(
                            InstallationStep(1, "Download the Theme", "Click download and save the .hwt theme file to your device storage."),
                            InstallationStep(2, "Move to Themes Folder", "Using your file manager, move or copy the .hwt file into: Internal Storage > Honor > Themes."),
                            InstallationStep(3, "Apply in Themes App", "Open the official 'Themes' app on your HONOR phone, go to 'Me' > 'My Themes'."),
                            InstallationStep(4, "Restart Phone", "Select the theme and tap 'Apply'. Rebooting ensures lock screen and control center elements apply cleanly.")
                        ),
                        tips = listOf(
                            "If the folder does not exist, create a folder named 'Themes' in your internal storage root.",
                            "Grant file access permissions to the Themes app if prompted."
                        )
                    )
                }
            }
            slug.contains("xiaomi") || slug.contains("poco") || slug.contains("redmi") -> {
                if (language == AppLanguage.AR) {
                    InstallationGuide(
                        brandTitle = "دليل تثبيت ثيمات Xiaomi / POCO / Redmi (HyperOS & MIUI)",
                        brandSlug = "xiaomi",
                        fileExtension = ".mtz",
                        targetFolder = "MIUI/theme أو عبر تطبيق السمات",
                        steps = listOf(
                            InstallationStep(1, "تحميل ملف الثيم MTZ", "قم بتحميل ملف الثيم بامتداد .mtz من صفحة الثيم."),
                            InstallationStep(2, "استيراد الثيم من التطبيق", "افتح تطبيق 'السمات' الرسمي > حسابك > السمات > اضغط 'استيراد' (Import) بالأسفل واختر الملف."),
                            InstallationStep(3, "استخدام أداة Theme Tester (للأجهزة العالمية)", "إذا ظهرت رسالة 'الثيمات من مصادر خارجية غير مدعومة'، استخدم تطبيق MIUI Theme Editor أو MTZ Tester لتثبيته بنقرة واحدة بدون روت."),
                            InstallationStep(4, "تطبيق وتفعيل الثيم", "اضغط على الثيم ثم تطبيق، واعد تشغيل الهاتف لتحديث شريط الحالة ولوحة التحكم الجديدة.")
                        ),
                        tips = listOf(
                            "ملفات .mtz توفر تخصيصاً كاملاً لمركز التحكم ومؤشر البطارية وشريط الإشعارات.",
                            "في واجهة HyperOS الجديدة، تأكد من توافق خط الثيم مع نمط الساعة المتعمق (Depth Clock)."
                        )
                    )
                } else {
                    InstallationGuide(
                        brandTitle = "Xiaomi / POCO (HyperOS & MIUI) Installation Guide",
                        brandSlug = "xiaomi",
                        fileExtension = ".mtz",
                        targetFolder = "Themes App / MTZ",
                        steps = listOf(
                            InstallationStep(1, "Download .mtz File", "Download the .mtz package from the theme details page."),
                            InstallationStep(2, "Import via Themes App", "Open Official Themes app > Profile > Themes > scroll down and tap 'Import'."),
                            InstallationStep(3, "Third-Party Installer (If Required)", "If third-party themes are restricted on your global ROM, use MIUI Theme Editor or MTZ Tester app."),
                            InstallationStep(4, "Apply & Reboot", "Select the imported theme, tap Apply, and restart your device for full Control Center theming.")
                        ),
                        tips = listOf(
                            "MTZ files provide deep customization including dynamic status bars and volume sliders.",
                            "For HyperOS, depth lock screen clocks integrate seamlessly with AMOLED wallpapers."
                        )
                    )
                }
            }
            slug.contains("huawei") -> {
                if (language == AppLanguage.AR) {
                    InstallationGuide(
                        brandTitle = "دليل تثبيت ثيمات HUAWEI (EMUI & HarmonyOS)",
                        brandSlug = "huawei",
                        fileExtension = ".hwt",
                        targetFolder = "Huawei/Themes",
                        steps = listOf(
                            InstallationStep(1, "تحميل ملف الثيم", "قم بتحميل ملف الثيم (.hwt) على جهازك."),
                            InstallationStep(2, "نقل الملف", "انقل الملف إلى مسار: وحدة التخزين الداخلية > Huawei > Themes."),
                            InstallationStep(3, "فتح تطبيق السمات", "افتح تطبيق 'السمات' > 'صفحتي' > 'السمات'، وستجد الثيم بانتظارك."),
                            InstallationStep(4, "التطبيق", "اضغط على الثيم ثم اضغط تطبيق (Apply).")
                        ),
                        tips = listOf(
                            "متوافق تماماً مع إصدارات EMUI 12 و 13 و 14 و HarmonyOS 2/3/4."
                        )
                    )
                } else {
                    InstallationGuide(
                        brandTitle = "HUAWEI (EMUI & HarmonyOS) Installation Guide",
                        brandSlug = "huawei",
                        fileExtension = ".hwt",
                        targetFolder = "Huawei/Themes",
                        steps = listOf(
                            InstallationStep(1, "Download Theme", "Download the .hwt package to your storage."),
                            InstallationStep(2, "Move File", "Copy to: Internal Storage > Huawei > Themes folder."),
                            InstallationStep(3, "Open Themes App", "Launch official Huawei Themes app > Me > Themes."),
                            InstallationStep(4, "Apply", "Tap the theme preview and press Apply.")
                        ),
                        tips = listOf(
                            "Fully compatible with EMUI 12, 13, 14 and HarmonyOS versions."
                        )
                    )
                }
            }
            else -> {
                if (language == AppLanguage.AR) {
                    InstallationGuide(
                        brandTitle = "دليل التثبيت السريع",
                        brandSlug = "generic",
                        fileExtension = ".zip / .hwt / .mtz",
                        targetFolder = "Download / Themes",
                        steps = listOf(
                            InstallationStep(1, "تنزيل الملف", "قم بتنزيل حزمة الثيم أو ملفات الخلفيات والأيقونات."),
                            InstallationStep(2, "تطبيق حزمة الأيقونات", "استخدم مشغل تطبيقات يدعم حزم الأيقونات أو تطبيق سمات جهازك الرسمي."),
                            InstallationStep(3, "تعيين الخلفية وشاشة القفل", "اضغط على زر تعيين الخلفية أو حدد الصورة من المعرض.")
                        ),
                        tips = listOf(
                            "يمكنك دائماً حفظ خلفيات الـ 4K وتطبيقها مباشرة من التطبيق."
                        )
                    )
                } else {
                    InstallationGuide(
                        brandTitle = "Quick Installation Guide",
                        brandSlug = "generic",
                        fileExtension = ".zip / .hwt / .mtz",
                        targetFolder = "Downloads",
                        steps = listOf(
                            InstallationStep(1, "Download Pack", "Download the theme or wallpaper package."),
                            InstallationStep(2, "Apply Icon Pack", "Use your device's built-in Theme store or a custom launcher."),
                            InstallationStep(3, "Set Wallpaper", "Apply lock screen and home screen wallpapers directly.")
                        ),
                        tips = listOf(
                            "You can also use the One-Tap Wallpaper action to set 4K backgrounds instantly."
                        )
                    )
                }
            }
        }
    }
}
