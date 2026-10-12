package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AccountBillingScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AiGeneratorScreen
import com.example.ui.screens.Board3DViewerScreen
import com.example.ui.screens.ComponentCatalogScreen
import com.example.ui.screens.DrcReportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningCenterScreen
import com.example.ui.screens.ManufacturingExportScreen
import com.example.ui.screens.PcbEditorScreen
import com.example.ui.screens.SchematicEditorScreen
import com.example.ui.screens.SimulationScreen
import com.example.ui.theme.CopperGold
import com.example.ui.theme.EasyPcbTheme
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceContainer
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PcbGreenDark
import com.example.ui.theme.PcbGreenPrimary
import com.example.ui.theme.SiliconTraceBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.EasyPcbViewModel

enum class AppScreen(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    PIPELINE("Pipeline", Icons.Default.AutoAwesome),
    SCHEMATIC("Schematic", Icons.Default.Memory),
    PCB_2D("PCB 2D", Icons.Default.Layers),
    BOARD_3D("3D Board", Icons.Default.ViewInAr),
    SIMULATION("Simulation", Icons.Default.Timeline),
    DRC("DRC Report", Icons.Default.CheckCircle),
    MANUFACTURING("Export", Icons.Default.Download),
    CATALOG("Components", Icons.Default.Category),
    LEARNING("Learning", Icons.Default.School),
    ASSISTANT("AI Assistant", Icons.Default.QuestionAnswer),
    ACCOUNT("Easy PCB Pro", Icons.Default.WorkspacePremium),
    MORE_TOOLS("EDA Suite", Icons.Default.MoreHoriz)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyPcbTheme {
                EasyPcbApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyPcbApp(
    viewModel: EasyPcbViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var screenHistory by remember { mutableStateOf(listOf(AppScreen.HOME)) }

    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            screenHistory = screenHistory + screen
            currentScreen = screen
        }
    }

    fun navigateBack() {
        if (screenHistory.size > 1) {
            screenHistory = screenHistory.dropLast(1)
            currentScreen = screenHistory.last()
        } else {
            currentScreen = AppScreen.HOME
        }
    }

    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        navigateBack()
    }

    val userProfile by viewModel.userProfile.collectAsState()
    val project by viewModel.currentProject.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PcbGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = PcbGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentScreen == AppScreen.HOME) "Easy PCB" else currentScreen.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = project?.name ?: "From Idea to PCB",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentScreen != AppScreen.HOME) {
                        IconButton(onClick = { navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    }
                },
                actions = {
                    // Quick Action Icons
                    IconButton(onClick = { navigateTo(AppScreen.ASSISTANT) }) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Assistant", tint = PcbGreenPrimary)
                    }
                    IconButton(onClick = { navigateTo(AppScreen.ACCOUNT) }) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Easy PCB Pro",
                            tint = if (userProfile.isProSubscribed) CopperGold else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavySurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavySurface,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                val primaryNavItems = listOf(
                    AppScreen.HOME,
                    AppScreen.PIPELINE,
                    AppScreen.SCHEMATIC,
                    AppScreen.PCB_2D,
                    AppScreen.MORE_TOOLS
                )

                primaryNavItems.forEach { screen ->
                    val isSelected = when (screen) {
                        AppScreen.MORE_TOOLS -> currentScreen in listOf(
                            AppScreen.MORE_TOOLS, AppScreen.BOARD_3D, AppScreen.SIMULATION,
                            AppScreen.DRC, AppScreen.MANUFACTURING, AppScreen.CATALOG,
                            AppScreen.LEARNING, AppScreen.ASSISTANT, AppScreen.ACCOUNT
                        )
                        else -> currentScreen == screen
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { navigateTo(screen) },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = PcbGreenPrimary,
                            indicatorColor = PcbGreenPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NavyBackground)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToProject = { navigateTo(AppScreen.SCHEMATIC) },
                    onNavigateToPipeline = { navigateTo(AppScreen.PIPELINE) },
                    onNavigateToPro = { navigateTo(AppScreen.ACCOUNT) }
                )
                AppScreen.PIPELINE -> AiGeneratorScreen(
                    viewModel = viewModel,
                    onNavigateToSchematic = { navigateTo(AppScreen.SCHEMATIC) },
                    onNavigateToPcb = { navigateTo(AppScreen.PCB_2D) }
                )
                AppScreen.SCHEMATIC -> SchematicEditorScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navigateBack() }
                )
                AppScreen.PCB_2D -> PcbEditorScreen(
                    viewModel = viewModel,
                    onNavigateTo3d = { navigateTo(AppScreen.BOARD_3D) }
                )
                AppScreen.BOARD_3D -> Board3DViewerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navigateBack() }
                )
                AppScreen.SIMULATION -> SimulationScreen(viewModel = viewModel)
                AppScreen.DRC -> DrcReportScreen(viewModel = viewModel)
                AppScreen.MANUFACTURING -> ManufacturingExportScreen(viewModel = viewModel)
                AppScreen.CATALOG -> ComponentCatalogScreen(viewModel = viewModel)
                AppScreen.LEARNING -> LearningCenterScreen(viewModel = viewModel)
                AppScreen.ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                AppScreen.ACCOUNT -> AccountBillingScreen(viewModel = viewModel)
                AppScreen.MORE_TOOLS -> MoreToolsHubScreen(onNavigate = { navigateTo(it) })
            }
        }
    }
}

@Composable
fun MoreToolsHubScreen(onNavigate: (AppScreen) -> Unit) {
    val toolsList = listOf(
        Triple(AppScreen.BOARD_3D, "Interactive 3D PCB Viewer", "Render full 3D board with component packages & solder mask finishes"),
        Triple(AppScreen.SIMULATION, "SPICE Circuit Simulation", "DC operating points, transient response & oscilloscope waveforms"),
        Triple(AppScreen.DRC, "DRC & Validation Reports", "IPC-2221 clearance rules, track capacities & creepage tests"),
        Triple(AppScreen.MANUFACTURING, "Manufacturing File Export", "Gerber X2, Excellon Drill, BOM & KiCad 8 project archives"),
        Triple(AppScreen.CATALOG, "Component Parts Database", "Search 40+ verified MPNs, footprints, and datasheets"),
        Triple(AppScreen.LEARNING, "Learning & Calculators", "Ohm's law, IPC-2152 trace width & Malayalam tutorials"),
        Triple(AppScreen.ASSISTANT, "AI Design Assistant", "Interactive circuit optimization & DRC troubleshooting"),
        Triple(AppScreen.ACCOUNT, "Easy PCB Pro & Account", "Google Play Billing, subscription and multi-device settings")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Easy PCB Professional EDA Suite",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Comprehensive electronic design automation tools",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(toolsList.size) { idx ->
            val (screen, title, desc) = toolsList[idx]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(screen) }
                    .testTag("tool_hub_${screen.name}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(NavySurfaceVariant, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = screen.icon, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}
