package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.ThemeCard

/**
 * Standard reusable screen top header
 */
@Composable
fun StandardTopBar(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 1. Favorites Screen
 */
@Composable
fun FavoritesScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteThemeIds.collectAsStateWithLifecycle()

    val favThemes = fullThemes.filter { favoriteIds.contains(it.theme.id) }

    Column(modifier = modifier.fillMaxSize().testTag("favorites_screen")) {
        StandardTopBar(
            title = S18Strings.get("favorites", language),
            onBack = { viewModel.navigateTo(Screen.Home) }
        )

        if (favThemes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (language == AppLanguage.AR) "قائمة المفضلة فارغة" else "No Favorite Themes Yet",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (language == AppLanguage.AR) "اضغط على أيقونة القلب في أي ثيم لحفظه هنا والوصول إليه بسرعة" else "Tap the heart icon on any theme to save it here for fast access.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(S18Strings.get("home", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favThemes.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                ThemeCard(
                                    item = item,
                                    language = language,
                                    onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) },
                                    onQuickPreviewClick = { viewModel.showQuickPreview(item) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. Categories Screen
 */
@Composable
fun CategoriesScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val companies by viewModel.publishedCompanies.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()

    val categories = listOf(
        "Cyberpunk" to "Future & Neon vibes",
        "Minimal" to "Clean & distraction-free",
        "Dark Mode" to "OLED-friendly blacks",
        "Anime" to "Japanese animation art",
        "Nature" to "Landscapes & forests",
        "Superheroes" to "Marvel & DC worlds",
        "Sports" to "Cars & racing styles",
        "Abstract" to "3D geometric visuals"
    )

    Column(modifier = modifier.fillMaxSize().testTag("categories_screen")) {
        StandardTopBar(
            title = S18Strings.get("categories", language),
            onBack = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = S18Strings.get("companies", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00E5FF)
                )
            }

            items(companies) { company ->
                val count = fullThemes.count { it.theme.companyId == company.id && it.theme.published }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.CompanyDetails(company.slug)) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = company.name,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = company.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "$count",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == AppLanguage.AR) "الأنماط والتصنيفات الشائعة" else "Popular Styles & Tags",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00E5FF)
                )
            }

            items(categories) { (tag, desc) ->
                val count = fullThemes.count { it.theme.tags.contains(tag, ignoreCase = true) && it.theme.published }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.Search) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = tag,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "$count themes",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. Support & Contact Us Screen
 */
@Composable
fun SupportScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val allMessages by viewModel.allSupportMessages.collectAsStateWithLifecycle()

    var supportTab by remember { mutableIntStateOf(0) } // 0: New Ticket, 1: My Tickets & Replies
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("عام") }
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val isAr = language == AppLanguage.AR
    val categories = if (isAr) listOf("عام", "مشكلة تحميل", "طلب ثيم", "اقتراح", "أخرى")
    else listOf("General", "Download Issue", "Theme Request", "Suggestion", "Other")

    Column(modifier = modifier.fillMaxSize().testTag("support_screen")) {
        StandardTopBar(
            title = S18Strings.get("support", language),
            onBack = { viewModel.navigateTo(Screen.Home) }
        )

        // Sub-tabs: Send Ticket vs View Tickets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { supportTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (supportTab == 0) Color(0xFF00E5FF) else Color.Transparent,
                    contentColor = if (supportTab == 0) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isAr) "إرسال تذكرة" else "New Ticket", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { supportTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (supportTab == 1) Color(0xFF00E5FF) else Color.Transparent,
                    contentColor = if (supportTab == 1) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isAr) "تذاكري (${allMessages.size})" else "My Tickets (${allMessages.size})",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (supportTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isAr) "مركز الدعم والتواصل المباشر" else "S18_THEME Support Center",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF00E5FF)
                            )
                            Text(
                                text = if (isAr) "نسعد بمساعدتك والإجابة على أي استفسار أو مشكلة في تثبيت الثيمات والخطوط، وسيقوم فريق الإدارة بالرد عليك مباشرة." else "We are here to assist you. Submit a ticket and our administrative team will reply directly.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Category Chips
                item {
                    Text(
                        text = if (isAr) "تصنيف الطلب" else "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isAr) "الاسم أو المعرف" else "Your Name") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (isAr) "البريد الإلكتروني أو تليجرام" else "Email / Telegram handle") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text(if (isAr) "موضوع التذكرة" else "Subject") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text(if (isAr) "تفاصيل الرسالة أو المشكلة" else "Message / Details") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (name.isNotBlank() && message.isNotBlank()) {
                                isSending = true
                                viewModel.sendSupportMessage(
                                    senderName = name.trim(),
                                    senderEmail = email.trim(),
                                    category = selectedCategory,
                                    subject = subject.trim().ifBlank { if (isAr) "استفسار دعم" else "Support Ticket" },
                                    message = message.trim(),
                                    onComplete = { success ->
                                        isSending = false
                                        if (success) {
                                            Toast.makeText(context, if (isAr) "تم إرسال تذكرتك بنجاح! يمكنك متابعتها في تبويب 'تذاكري'" else "Ticket submitted successfully!", Toast.LENGTH_LONG).show()
                                            name = ""
                                            email = ""
                                            subject = ""
                                            message = ""
                                            supportTab = 1
                                        }
                                    }
                                )
                            } else {
                                Toast.makeText(context, if (isAr) "يرجى كتابة الاسم والرسالة" else "Please fill out your name and message", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSending,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024)),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isAr) "إرسال التذكرة الآن" else "Submit Ticket", fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isAr) "قنوات التواصل المباشرة" else "Direct Channels",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/s18theme"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Telegram", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:support@s18theme.com"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Email", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Tab 1: My Tickets & Replies
            if (allMessages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(48.dp))
                        Text(if (isAr) "لا توجد تذاكر دعم مسجلة حتى الآن" else "No support tickets yet", fontWeight = FontWeight.Bold)
                        Text(if (isAr) "يمكنك إرسال استفسار أو مشكلة في تبويب 'إرسال تذكرة'" else "Submit an inquiry in the 'New Ticket' tab.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allMessages) { ticket ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ticket.subject,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val isReplied = ticket.status == "REPLIED"
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isReplied) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (isReplied) (if (isAr) "تم الرد" else "Replied") else (if (isAr) "قيد المراجعة" else "Pending"),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isReplied) Color(0xFF10B981) else Color(0xFFF59E0B)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = ticket.senderName, fontSize = 12.sp, color = Color(0xFF00E5FF))
                                    Text(text = "•", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                    Text(text = ticket.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Text(
                                    text = ticket.message,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    lineHeight = 20.sp
                                )

                                // Admin Reply Box if present
                                if (!ticket.adminReply.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF00E5FF).copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = if (isAr) "رد فريق إدارة S18_THEME" else "S18_THEME Admin Reply",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF00E5FF)
                                                )
                                            }
                                            Text(
                                                text = ticket.adminReply,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 19.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. About Us Screen
 */
@Composable
fun AboutUsScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().testTag("about_us_screen")) {
        StandardTopBar(
            title = S18Strings.get("about_us", language),
            onBack = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFF00E5FF), Color(0xFF0284C7))
                                    ),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "S18",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }

                        Text(
                            text = "S18_THEME",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Version 2.0.0 (Pro Edition)",
                            fontSize = 12.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "حول المنصة" else "About The Platform",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                        Text(
                            text = if (language == AppLanguage.AR)
                                "منصة S18_THEME هي المركز المتقدم والرائد لتصميم وتخصيص هواتف الأندرويد، حيث تجمع أفضل الثيمات الاحترافية، والخلفيات عالية الدقة 4K، والأيقونات المميزة المتوافقة مع مختلف أجهزة الهواتف (Xiaomi, Samsung, Oppo, Realme, وغيرها)."
                            else
                                "S18_THEME is the next-generation customization hub for mobile operating systems, offering premium themes, 4K wallpapers, and icon packs engineered for maximum aesthetic refinement and device performance.",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "المميزات الرئيسية" else "Core Capabilities",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                        Text("• " + if (language == AppLanguage.AR) "تحميل مباشر وسريع عبر روابط سحابية موثوقة" else "High-speed real downloads with system integration", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        Text("• " + if (language == AppLanguage.AR) "معاينة واقعية للشاشة الرئيسية وشاشة القفل قبل التثبيت" else "Interactive live lockscreen and homescreen previews", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        Text("• " + if (language == AppLanguage.AR) "نظام إدارة متطور للأدمن لنشر وتحديث المحتوى فوريًا" else "Full Admin and Sub-admin portal for rapid content publishing", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * 5. Privacy Policy & Terms Screen
 */
@Composable
fun PrivacyPolicyScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().testTag("privacy_policy_screen")) {
        StandardTopBar(
            title = S18Strings.get("privacy_policy", language),
            onBack = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E5FF))
                            Text(
                                text = if (language == AppLanguage.AR) "سياسة الخصوصية وأمان البيانات" else "Privacy & Data Security",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = if (language == AppLanguage.AR)
                                "نحن في تطبيق S18_THEME نولي أمان وخصوصية مستخدمينا أقصى درجات الاهتمام. جميع بيانات التفضيلات والإعدادات يتم حفظها بشكل مشفر محليًا على جهازك فقط (Local Database) ولا نشارك أي بيانات شخصية مع أي جهات خارجية إطلاقًا."
                            else
                                "At S18_THEME, user privacy is paramount. User preferences, favorites, and customizations are stored securely and locally on your device. We do not sell or transmit your personal information.",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "الشروط والأحكام" else "Terms and Conditions",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )

                        Text(
                            text = if (language == AppLanguage.AR)
                                "جميع الثيمات والخلفيات المنشورة في التطبيق مخصصة للاستخدام الشخصي غير التجاري. يمنع إعادة توزيع المحتوى أو بيعه بدون إذن مسبق من المصمم أو إدارة التطبيق."
                            else
                                "All downloadable themes and wallpapers are intended strictly for personal, non-commercial modification of your personal devices. Redistribution without prior permission is prohibited.",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
