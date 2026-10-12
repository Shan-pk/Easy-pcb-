package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EasyPcbProject
import com.example.ui.theme.CopperGold
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
fun HomeScreen(
    viewModel: EasyPcbViewModel,
    onNavigateToProject: (String) -> Unit,
    onNavigateToPipeline: () -> Unit,
    onNavigateToPro: () -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentStage by viewModel.currentPipelineStage.collectAsState()
    val stageProgress by viewModel.pipelineStageProgress.collectAsState()
    val promptText by viewModel.promptText.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val quickPrompts = listOf(
        "ESP32 4-Ch Relay 12V",
        "LM2596 5V 3A Buck",
        "USB-C Li-Ion Smart Charger",
        "RP2040 Dual Sensor Hub",
        "STM32 Motor Driver Shield"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Brand Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F1E2E),
                                    Color(0xFF0A1520),
                                    Color(0xFF06291C)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PcbGreenPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, PcbGreenPrimary, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        contentDescription = "Easy PCB Logo",
                                        tint = PcbGreenPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Easy PCB",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "From Idea to PCB • AI EDA Engine",
                                        color = PcbGreenPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Pro Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (userProfile.isProSubscribed) CopperGold.copy(alpha = 0.2f) else NavySurfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (userProfile.isProSubscribed) CopperGold else Color.Gray.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .clickable { onNavigateToPro() }
                                    .testTag("pro_badge_button")
                            ) {
                                Text(
                                    text = if (userProfile.isProSubscribed) "PRO ACTIVE" else "GET PRO $3/mo",
                                    color = if (userProfile.isProSubscribed) CopperGold else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI-Powered Electronic Circuit & PCB Design",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Transform natural language ideas into validated schematics, 2D/3D PCB layouts, SPICE simulations, and Gerber manufacturing files.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Natural-Language AI Prompt Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_prompt_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PcbGreenPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = PcbGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Natural-Language PCB Generator",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Describe the electronic device you want to build...",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { viewModel.setPrompt(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavySurfaceVariant,
                            unfocusedContainerColor = NavySurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PcbGreenPrimary,
                            unfocusedBorderColor = NavySurfaceContainer
                        ),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Prompt Pills
                    Text(
                        text = "Suggested Electronics Presets:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPrompts.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavySurfaceVariant,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.setPrompt("Design a $preset with regulated power, status LEDs, and protection circuitry.")
                                    }
                                    .testTag("preset_${preset.replace(" ", "_")}")
                            ) {
                                Text(
                                    text = preset,
                                    color = SiliconTraceBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.startAiCircuitGeneration()
                            onNavigateToPipeline()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_circuit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PcbGreenPrimary),
                        enabled = !isGenerating && promptText.isNotBlank()
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Synthesizing Circuit...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Generate",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate Schematic & PCB Layout",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Generation Progress Card (if generating)
        item {
            AnimatedVisibility(visible = isGenerating) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generation_progress_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PcbGreenPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Stage ${currentStage.stepNumber}/7: ${currentStage.title}",
                                color = PcbGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${(stageProgress * 100).toInt()}%",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentStage.description,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { stageProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = PcbGreenPrimary,
                            trackColor = NavySurfaceContainer
                        )
                    }
                }
            }
        }

        // Current Active Project Card
        currentProject?.let { project ->
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Active Design Project",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "View Pipeline ➔",
                        color = PcbGreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToPipeline() }
                            .testTag("view_pipeline_link")
                    )
                }
            }

            item {
                ActiveProjectSummaryCard(
                    project = project,
                    onOpenEditor = { onNavigateToProject(project.id) }
                )
            }
        }

        // Project History / Library List
        item {
            Text(
                text = "Recent Projects & Verified Designs (${projects.size})",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(projects) { p ->
            ProjectListItemCard(
                project = p,
                isSelected = p.id == currentProject?.id,
                onClick = {
                    viewModel.selectProject(p)
                    onNavigateToProject(p.id)
                }
            )
        }
    }
}

@Composable
fun ActiveProjectSummaryCard(
    project: EasyPcbProject,
    onOpenEditor: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenEditor() }
            .testTag("active_project_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = project.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PcbGreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "DRC VERIFIED",
                        color = PcbGreenPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = project.description,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Specs Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecBadge(icon = Icons.Default.ElectricBolt, label = "Input Rail", value = "${project.requirements.inputVoltageMax}V Max")
                SpecBadge(icon = Icons.Default.Layers, label = "Layer Stack", value = "${project.pcbLayout.layerCount}-Layer FR4")
                SpecBadge(icon = Icons.Default.Speed, label = "Track Length", value = "${project.pcbLayout.totalTrackLengthMm.toInt()} mm")
                SpecBadge(icon = Icons.Default.ViewInAr, label = "Board Area", value = "${project.pcbLayout.widthMm.toInt()}x${project.pcbLayout.heightMm.toInt()}mm")
            }
        }
    }
}

@Composable
fun SpecBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = SiliconTraceBlue,
            modifier = Modifier.size(16.dp)
        )
        Text(text = label, color = TextMuted, fontSize = 9.sp)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@Composable
fun ProjectListItemCard(
    project: EasyPcbProject,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_item_${project.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NavySurfaceVariant else NavySurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PcbGreenPrimary.copy(alpha = 0.6f) else NavySurfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = "${project.components.size} components • ${project.schematic.nets.size} nets • ${project.pcbLayout.widthMm.toInt()}x${project.pcbLayout.heightMm.toInt()}mm",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Ready",
                tint = PcbGreenPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
