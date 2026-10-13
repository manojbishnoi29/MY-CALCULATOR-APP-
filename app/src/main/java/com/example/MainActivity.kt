package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.components.AutoMediaBanner
import com.example.ui.components.MediaSettingsDialog
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.FinanceToolsScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SecondaryText
import com.example.util.MediaSettingsManager
import com.example.util.NetworkMonitor
import com.example.viewmodel.CalculatorViewModel

enum class MainAppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    CALCULATOR("Calculator", Icons.Filled.Calculate, "nav_tab_calculator"),
    CONVERTER("Converter", Icons.Filled.Straighten, "nav_tab_converter"),
    FINANCE("Finance", Icons.Filled.ReceiptLong, "nav_tab_finance")
}

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: CalculatorViewModel) {
    val context = LocalContext.current
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = MainAppTab.values()

    // Internet connectivity and media configuration
    val networkMonitor = remember { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = networkMonitor.checkCurrentConnectivity())
    val mediaSettingsManager = remember { MediaSettingsManager(context) }
    val mediaConfig by mediaSettingsManager.config.collectAsState()

    var isMediaSettingsOpen by remember { mutableStateOf(false) }
    val mediaSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Auto-sync latest media from GitHub whenever internet is active
    androidx.compose.runtime.LaunchedEffect(isOnline) {
        if (isOnline && mediaConfig.githubRepo.isNotBlank() && mediaConfig.autoSyncGitHub) {
            mediaSettingsManager.syncFromGitHub()
        }
    }

    // Pressing back on sub-screens returns to the main calculator screen
    BackHandler(enabled = selectedTabIndex != 0) {
        selectedTabIndex = 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                Column {
                    // Auto-Playing Media Area: Placed directly ABOVE the Calculator, Convert, Finance buttons
                    AutoMediaBanner(
                        config = mediaConfig,
                        isOnline = isOnline,
                        onOpenSettings = { isMediaSettingsOpen = true },
                        onClose = { mediaSettingsManager.toggleEnabled(false) }
                    )

                    NavigationBar(
                        containerColor = Color.White,
                        contentColor = com.example.ui.theme.BrutalistBorder,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier
                            .testTag("main_bottom_nav")
                            .border(BorderStroke(2.dp, com.example.ui.theme.BrutalistBorder))
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = selectedTabIndex == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTabIndex = index },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = com.example.ui.theme.BrutalistBorder,
                                    selectedTextColor = com.example.ui.theme.BrutalistBorder,
                                    indicatorColor = com.example.ui.theme.BrutalistYellow,
                                    unselectedIconColor = Color(0xFF6B7280),
                                    unselectedTextColor = Color(0xFF6B7280)
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = selectedTabIndex,
                label = "tab_crossfade",
                modifier = Modifier.padding(innerPadding)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> CalculatorScreen(
                        viewModel = viewModel,
                        onOpenMediaSettings = { isMediaSettingsOpen = true }
                    )
                    1 -> UnitConverterScreen(viewModel = viewModel)
                    2 -> FinanceToolsScreen(viewModel = viewModel)
                }
            }
        }

        // Media Settings Dialog
        if (isMediaSettingsOpen) {
            MediaSettingsDialog(
                config = mediaConfig,
                isOnline = isOnline,
                manager = mediaSettingsManager,
                sheetState = mediaSheetState,
                onDismiss = { isMediaSettingsOpen = false }
            )
        }
    }
}
