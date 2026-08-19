package com.example.ui.keyboard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppItem(
    val appName: String,
    val packageName: String,
    val initials: String
)

@Composable
fun AppSearchSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var installedApps by remember { mutableStateOf<List<InstalledAppItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveList: List<ResolveInfo> = try {
                pm.queryIntentActivities(mainIntent, 0)
            } catch (_: Exception) {
                emptyList()
            }

            val apps = resolveList.mapNotNull { resolveInfo ->
                try {
                    val name = resolveInfo.loadLabel(pm).toString()
                    val pkg = resolveInfo.activityInfo.packageName
                    val initials = name.trim().split(" ")
                        .filter { it.isNotEmpty() }
                        .take(2)
                        .map { it.first().uppercaseChar() }
                        .joinToString("")
                        .ifEmpty { "A" }
                    InstalledAppItem(appName = name, packageName = pkg, initials = initials)
                } catch (_: Exception) {
                    null
                }
            }.distinctBy { it.packageName }.sortedBy { it.appName }

            // Fallback list of common daily apps if queryIntentActivities returns few
            val finalApps = if (apps.size >= 5) apps else (apps + listOf(
                InstalledAppItem("WhatsApp", "com.whatsapp", "WA"),
                InstalledAppItem("YouTube", "com.google.android.youtube", "YT"),
                InstalledAppItem("Chrome", "com.android.chrome", "CH"),
                InstalledAppItem("Maps", "com.google.android.apps.maps", "GM"),
                InstalledAppItem("Gmail", "com.google.android.gm", "GM"),
                InstalledAppItem("Settings", "com.android.settings", "ST"),
                InstalledAppItem("Camera", "com.android.camera", "CA"),
                InstalledAppItem("Gallery / Photos", "com.google.android.apps.photos", "PH")
            )).distinctBy { it.packageName }

            withContext(Dispatchers.Main) {
                installedApps = finalApps
                isLoading = false
            }
        }
    }

    val filteredApps = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter {
                it.appName.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(palette.surface)
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "App Search & Quick Launch (యాప్ సెర్చ్)",
                        color = palette.keyText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Instantly search and launch any app directly from keyboard",
                        color = palette.keySecondaryText,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_app_search_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close App Search",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search installed apps (e.g. WhatsApp, YouTube)...", fontSize = 11.sp, color = palette.keySecondaryText) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = palette.keySecondaryText, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(vertical = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.accent,
                unfocusedBorderColor = palette.border,
                focusedTextColor = palette.keyText,
                unfocusedTextColor = palette.keyText,
                focusedContainerColor = palette.background,
                unfocusedContainerColor = palette.background
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("Loading apps...", color = palette.keySecondaryText, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppItemRow(
                        app = app,
                        palette = palette,
                        onLaunch = {
                            try {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                }
                            } catch (_: Exception) {}
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItemRow(
    app: InstalledAppItem,
    palette: KeyboardPalette,
    onLaunch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.keyBackground)
            .clickable(onClick = onLaunch)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("app_item_${app.packageName}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Icon Placeholder / Badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(palette.accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.initials,
                color = palette.accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.appName,
                color = palette.keyText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = app.packageName,
                color = palette.keySecondaryText,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "Open App",
            tint = palette.accent,
            modifier = Modifier.size(16.dp)
        )
    }
}
