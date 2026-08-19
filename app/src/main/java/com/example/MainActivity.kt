package com.example

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.KeyboardPreferences
import com.example.ui.components.TeluguSmartLogo
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SetupWizardScreen
import com.example.ui.screens.ThemePickerScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class MainTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    LAYOUTS("Layouts", Icons.Default.GridView, "nav_tab_layouts"),
    THEMES("Themes", Icons.Default.Palette, "nav_tab_themes"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_tab_settings")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TeluguSmartApplication.instance.repository

        setContent {
            var isDarkMode by remember { mutableStateOf(false) } // Default is Light Mode as requested
            var showSetupWizard by remember { mutableStateOf(false) }
            var currentTab by remember { mutableStateOf(MainTab.LAYOUTS) }
            var showMenu by remember { mutableStateOf(false) }

            val preferences: KeyboardPreferences by repository.preferences.collectAsState()
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            // Check if IME is already default on device
            val checkImeActive = {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                val enabledList = imm?.enabledInputMethodList ?: emptyList()
                val isEnabled = enabledList.any { it.packageName == context.packageName }
                val currentIme = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD
                ) ?: ""
                val isSelected = currentIme.contains(context.packageName)
                if (!isEnabled || !isSelected) {
                    showSetupWizard = true
                }
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        checkImeActive()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            LaunchedEffect(Unit) {
                checkImeActive()
            }

            MyApplicationTheme(darkTheme = isDarkMode) {
                if (showSetupWizard) {
                    SetupWizardScreen(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { isDarkMode = !isDarkMode },
                        onComplete = {
                            showSetupWizard = false
                        }
                    )
                } else {
                    BackHandler(enabled = currentTab != MainTab.LAYOUTS) {
                        currentTab = MainTab.LAYOUTS
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        TeluguSmartLogo(
                                            size = 36.dp,
                                            showText = false,
                                            modifier = Modifier.testTag("app_bar_icon")
                                        )
                                        Text(
                                            text = "TeluguSmart Keyboard",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                actions = {
                                    // Dark Mode Toggle on EVERY page as requested!
                                    IconButton(
                                        onClick = { isDarkMode = !isDarkMode },
                                        modifier = Modifier.testTag("top_bar_dark_mode_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = "Toggle Light/Dark Theme",
                                            tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFF0F172A)
                                        )
                                    }

                                    IconButton(
                                        onClick = { showSetupWizard = true },
                                        modifier = Modifier.testTag("top_bar_help_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HelpOutline,
                                            contentDescription = "Setup Guide",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Box {
                                        IconButton(onClick = { showMenu = true }) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "More Options",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Re-run Setup Wizard") },
                                                onClick = {
                                                    showMenu = false
                                                    showSetupWizard = true
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Share Keyboard") },
                                                onClick = {
                                                    showMenu = false
                                                }
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 6.dp,
                                modifier = Modifier.height(68.dp)
                            ) {
                                MainTab.entries.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF047857),
                                            selectedTextColor = Color(0xFF047857),
                                            indicatorColor = if (isDarkMode) Color(0xFF064E3B) else Color(0xFFD1FAE5)
                                        ),
                                        modifier = Modifier.testTag(tab.testTag)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            when (currentTab) {
                                MainTab.LAYOUTS -> {
                                    HomeScreen(
                                        preferences = preferences,
                                        onUpdatePreferences = { updated ->
                                            scope.launch { repository.updatePreferences(updated) }
                                        },
                                        isDarkMode = isDarkMode
                                    )
                                }
                                MainTab.THEMES -> {
                                    ThemePickerScreen(
                                        preferences = preferences,
                                        onUpdatePreferences = { updated ->
                                            scope.launch { repository.updatePreferences(updated) }
                                        },
                                        isDarkMode = isDarkMode
                                    )
                                }
                                MainTab.SETTINGS -> {
                                    SettingsScreen(
                                        preferences = preferences,
                                        onUpdatePreferences = { updated ->
                                            scope.launch { repository.updatePreferences(updated) }
                                        },
                                        isDarkMode = isDarkMode,
                                        onNavigateToThemes = {
                                            currentTab = MainTab.THEMES
                                        }
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
