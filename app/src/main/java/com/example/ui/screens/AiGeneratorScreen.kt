package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArchitectureBlock
import com.example.data.model.ComponentSpec
import com.example.data.model.ErcSeverity
import com.example.data.model.GenerationStage
import com.example.data.model.ProjectRequirements
import com.example.ui.theme.CopperGold
import com.example.ui.theme.DrcErrorRed
import com.example.ui.theme.DrcWarningAmber
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceContainer
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PcbGreenPrimary
import com.example.ui.theme.SiliconTraceBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.EasyPcbViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiGeneratorScreen(
    viewModel: EasyPcbViewModel,
    onNavigateToSchematic: () -> Unit,
    onNavigateToPcb: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabTitles = listOf("Pipeline", "Requirements", "Architecture", "Components", "ERC Validation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_generator_screen")
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavySurface,
            contentColor = PcbGreenPrimary,
            edgePadding = 16.dp,
            modifier = Modifier.testTag("pipeline_tab_row")
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) PcbGreenPrimary else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        if (project == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No circuit project selected", color = TextSecondary)
            }
            return
        }

        val curProject = project!!

        when (selectedTab) {
            0 -> PipelineOverviewTab(curProject)
            1 -> RequirementsTab(curProject.requirements)
            2 -> ArchitectureTab(curProject.architectureBlocks)
            3 -> ComponentsTab(curProject.components)
            4 -> ErcValidationTab(curProject)
        }
    }
}

@Composable
fun PipelineOverviewTab(project: com.example.data.model.EasyPcbProject) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "7-Stage AI Electronics Design Pipeline",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Every stage generates inspectable engineering artifacts validated by DRC engines.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        items(GenerationStage.entries) { stage ->
            StageCard(stage = stage, isCompleted = true)
        }
    }
}

@Composable
fun StageCard(stage: GenerationStage, isCompleted: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stage_card_${stage.stepNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PcbGreenPrimary.copy(alpha = 0.2f))
                    .border(1.dp, PcbGreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = PcbGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Stage ${stage.stepNumber}: ${stage.title}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = stage.description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RequirementsTab(req: ProjectRequirements) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Electrical & Mechanical Specifications", color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReqRow("Target System Purpose", req.purpose)
                    ReqRow("Input Voltage Rail", "${req.inputVoltageMin}V - ${req.inputVoltageMax}V DC")
                    ReqRow("Internal Operating Rail", "${req.operatingVoltage}V Logic")
                    ReqRow("Maximum Peak Current", "${req.maxCurrentA} Amperes")
                    ReqRow("Microcontroller / Core", req.microcontroller)
                    ReqRow("Channels / Outputs", "${req.channelCount} Channel(s)")
                    ReqRow("Target Board Dimensions", "${req.targetBoardWidthMm}mm × ${req.targetBoardHeightMm}mm")
                    ReqRow("PCB Layer Count", "${req.layerCount}-Layer (FR4 Standard 1.6mm)")
                    ReqRow("Operating Environment", req.operatingEnvironment)
                    ReqRow("Estimated BOM Budget", "$${String.format(java.util.Locale.US, "%.2f", req.estimatedBudgetUsd)} USD")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Safety & Isolation Protections", color = CopperGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    req.safetyFeatures.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = CopperGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = feature, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReqRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun ArchitectureTab(blocks: List<ArchitectureBlock>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Circuit Architecture Functional Blocks",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        items(blocks) { block ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = block.name, color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = block.sectionType, color = SiliconTraceBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = block.description, color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Components: ${block.componentsUsed.joinToString(", ")}", color = TextMuted, fontSize = 11.sp)
                    Text(text = "Input Nets: ${block.inputNets.joinToString(", ")}", color = TextMuted, fontSize = 11.sp)
                    Text(text = "Output Nets: ${block.outputNets.joinToString(", ")}", color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ComponentsTab(components: List<ComponentSpec>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Selected Parts List (${components.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "Total BOM: $${String.format(java.util.Locale.US, "%.2f", components.sumOf { it.unitCostUsd.toDouble() })}",
                    color = PcbGreenPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        items(components) { comp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = comp.refDes, color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = comp.name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                        Text(text = "${comp.mpn} • ${comp.packageType} • ${comp.value}", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "Ratings: ${comp.voltageRating}", color = TextMuted, fontSize = 10.sp)
                    }
                    Text(
                        text = "$${String.format(java.util.Locale.US, "%.2f", comp.unitCostUsd)}",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ErcValidationTab(project: com.example.data.model.EasyPcbProject) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Electrical Rule Check (ERC) Report",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Validates power rail voltages, pin types, floating nets, and driver compatibility.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        items(project.schematic.ercIssues) { issue ->
            val icon = when (issue.severity) {
                ErcSeverity.ERROR -> Icons.Default.Warning
                ErcSeverity.WARNING -> Icons.Default.Warning
                ErcSeverity.INFO -> Icons.Default.CheckCircle
            }
            val tint = when (issue.severity) {
                ErcSeverity.ERROR -> DrcErrorRed
                ErcSeverity.WARNING -> DrcWarningAmber
                ErcSeverity.INFO -> PcbGreenPrimary
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "[${issue.code}] ${issue.severity.name}", color = tint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = issue.message, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
