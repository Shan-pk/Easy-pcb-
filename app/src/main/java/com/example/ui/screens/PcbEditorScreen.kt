package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PcbFootprintInstance
import com.example.data.model.PcbLayer
import com.example.ui.theme.CopperGold
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceContainer
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PcbGreenDark
import com.example.ui.theme.PcbGreenPrimary
import com.example.ui.theme.PcbSilkscreenWhite
import com.example.ui.theme.PcbSubstrateGreen
import com.example.ui.theme.PcbTraceBottomCopper
import com.example.ui.theme.PcbTraceTopCopper
import com.example.ui.theme.SiliconTraceBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.EasyPcbViewModel

@Composable
fun PcbEditorScreen(
    viewModel: EasyPcbViewModel,
    onNavigateTo3d: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    val activeLayer by viewModel.activePcbLayer.collectAsState()
    var selectedFootprint by remember { mutableStateOf<PcbFootprintInstance?>(null) }

    // Canvas Transform State
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active PCB layout project", color = TextSecondary)
        }
        return
    }

    val pcb = project!!.pcbLayout

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("pcb_editor_screen")
    ) {
        // Toolbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NavySurface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "2D PCB Layout Editor",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${pcb.widthMm.toInt()}×${pcb.heightMm.toInt()}mm • ${pcb.layerCount}-Layer FR4 • ${(pcb.routedRatio * 100).toInt()}% Routed",
                        color = PcbGreenPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { zoomScale = (zoomScale * 1.25f).coerceAtMost(4.0f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary)
                    }
                    IconButton(
                        onClick = { zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.4f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary)
                    }
                    IconButton(
                        onClick = {
                            zoomScale = 1.0f
                            panOffsetX = 0f
                            panOffsetY = 0f
                            selectedFootprint = null
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset View", tint = PcbGreenPrimary)
                    }
                }
            }
        }

        // Layer Filter Chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NavySurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Layers, contentDescription = null, tint = SiliconTraceBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Layer:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        LayerChip("ALL LAYERS", isSelected = activeLayer == null) {
                            viewModel.setActivePcbLayer(null)
                        }
                    }
                    item {
                        LayerChip("F.Cu (Top)", isSelected = activeLayer == PcbLayer.F_CU, color = PcbTraceTopCopper) {
                            viewModel.setActivePcbLayer(PcbLayer.F_CU)
                        }
                    }
                    item {
                        LayerChip("B.Cu (Bottom)", isSelected = activeLayer == PcbLayer.B_CU, color = PcbTraceBottomCopper) {
                            viewModel.setActivePcbLayer(PcbLayer.B_CU)
                        }
                    }
                    item {
                        LayerChip("F.Silk (Legend)", isSelected = activeLayer == PcbLayer.F_SILK, color = Color.White) {
                            viewModel.setActivePcbLayer(PcbLayer.F_SILK)
                        }
                    }
                    item {
                        LayerChip("Edge.Cuts", isSelected = activeLayer == PcbLayer.EDGE_CUTS, color = CopperGold) {
                            viewModel.setActivePcbLayer(PcbLayer.EDGE_CUTS)
                        }
                    }
                }
            }
        }

        // Interactive 2D Board Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF060B10))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.4f, 4.0f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                    }
                }
                .testTag("pcb_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Board scale: 1mm = 4.0px * zoomScale
                val mmScale = 4.0f * zoomScale
                val boardW = pcb.widthMm * mmScale
                val boardH = pcb.heightMm * mmScale
                val originX = (canvasWidth - boardW) / 2f + panOffsetX
                val originY = (canvasHeight - boardH) / 2f + panOffsetY

                // 1. PCB Substrate (FR4 Green Solder Mask Board)
                drawRoundRect(
                    color = PcbSubstrateGreen,
                    topLeft = Offset(originX, originY),
                    size = Size(boardW, boardH),
                    cornerRadius = CornerRadius(12f * zoomScale, 12f * zoomScale)
                )

                // Board Edge Outline (Edge.Cuts)
                drawRoundRect(
                    color = CopperGold,
                    topLeft = Offset(originX, originY),
                    size = Size(boardW, boardH),
                    cornerRadius = CornerRadius(12f * zoomScale, 12f * zoomScale),
                    style = Stroke(width = 2.0f * zoomScale)
                )

                // 4 M3 Corner Mounting Holes (3.2mm drill)
                val mHoleDist = 4.0f * mmScale
                val holeRad = 1.6f * mmScale
                val holeCorners = listOf(
                    Offset(originX + mHoleDist, originY + mHoleDist),
                    Offset(originX + boardW - mHoleDist, originY + mHoleDist),
                    Offset(originX + mHoleDist, originY + boardH - mHoleDist),
                    Offset(originX + boardW - mHoleDist, originY + boardH - mHoleDist)
                )
                for (hc in holeCorners) {
                    drawCircle(color = CopperGold, radius = holeRad + 2f * zoomScale, center = hc) // Annular ring
                    drawCircle(color = Color(0xFF060B10), radius = holeRad, center = hc) // Hole
                }

                // 2. Copper Tracks (Bottom Copper B.Cu - Cyan)
                if (activeLayer == null || activeLayer == PcbLayer.B_CU) {
                    val bTracks = pcb.tracks.filter { it.layer == PcbLayer.B_CU }
                    for (track in bTracks) {
                        val sx = originX + (track.startX * mmScale)
                        val sy = originY + (track.startY * mmScale)
                        val ex = originX + (track.endX * mmScale)
                        val ey = originY + (track.endY * mmScale)
                        val trackW = (track.widthMm * mmScale).coerceAtLeast(1.5f)
                        drawLine(
                            color = PcbTraceBottomCopper,
                            start = Offset(sx, sy),
                            end = Offset(ex, ey),
                            strokeWidth = trackW
                        )
                    }
                }

                // 3. Copper Tracks (Top Copper F.Cu - Bright Green)
                if (activeLayer == null || activeLayer == PcbLayer.F_CU) {
                    val fTracks = pcb.tracks.filter { it.layer == PcbLayer.F_CU }
                    for (track in fTracks) {
                        val sx = originX + (track.startX * mmScale)
                        val sy = originY + (track.startY * mmScale)
                        val ex = originX + (track.endX * mmScale)
                        val ey = originY + (track.endY * mmScale)
                        val trackW = (track.widthMm * mmScale).coerceAtLeast(1.5f)
                        drawLine(
                            color = PcbTraceTopCopper,
                            start = Offset(sx, sy),
                            end = Offset(ex, ey),
                            strokeWidth = trackW
                        )
                    }
                }

                // 4. Vias
                if (activeLayer == null || activeLayer == PcbLayer.F_CU || activeLayer == PcbLayer.B_CU) {
                    for (via in pcb.vias) {
                        val vx = originX + (via.posX * mmScale)
                        val vy = originY + (via.posY * mmScale)
                        val dia = (via.diameterMm * mmScale / 2f).coerceAtLeast(2.5f)
                        val drill = (via.drillMm * mmScale / 2f).coerceAtLeast(1.2f)
                        drawCircle(color = CopperGold, radius = dia, center = Offset(vx, vy))
                        drawCircle(color = Color(0xFF060B10), radius = drill, center = Offset(vx, vy))
                    }
                }

                // 5. Footprints & Pads
                for (fp in pcb.footprints) {
                    val fx = originX + (fp.posX * mmScale)
                    val fy = originY + (fp.posY * mmScale)
                    val fw = fp.widthMm * mmScale
                    val fh = fp.heightMm * mmScale

                    val isSelected = selectedFootprint?.id == fp.id

                    // Silkscreen Outlines (White)
                    if (activeLayer == null || activeLayer == PcbLayer.F_SILK) {
                        drawRect(
                            color = if (isSelected) PcbGreenPrimary else PcbSilkscreenWhite,
                            topLeft = Offset(fx - fw / 2, fy - fh / 2),
                            size = Size(fw, fh),
                            style = Stroke(width = if (isSelected) 2.0f * zoomScale else 1.0f * zoomScale)
                        )
                    }

                    // Component Pads
                    for (pad in fp.pads) {
                        val px = fx + (pad.relX * mmScale)
                        val py = fy + (pad.relY * mmScale)
                        val pw = (pad.widthMm * mmScale).coerceAtLeast(2f)
                        val ph = (pad.heightMm * mmScale).coerceAtLeast(2f)

                        if (pad.isThroughHole) {
                            // Circular through-hole pad with gold annular ring
                            drawCircle(color = CopperGold, radius = pw / 2f, center = Offset(px, py))
                            drawCircle(color = Color(0xFF060B10), radius = (pad.drillDiameterMm * mmScale / 2f).coerceAtLeast(1f), center = Offset(px, py))
                        } else {
                            // SMD rectangular copper pad
                            drawRect(
                                color = CopperGold,
                                topLeft = Offset(px - pw / 2, py - ph / 2),
                                size = Size(pw, ph)
                            )
                        }
                    }
                }
            }
        }

        // Autorouter & DRC Bottom Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NavySurface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Pass", tint = PcbGreenPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Autorouter: 100% Routed", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "0 Unrouted Nets • 0 DRC Violations", color = PcbGreenPrimary, fontSize = 11.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PcbGreenPrimary,
                    modifier = Modifier.clickable { onNavigateTo3d() }
                ) {
                    Text(
                        text = "3D Viewer ➔",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LayerChip(title: String, isSelected: Boolean, color: Color = PcbGreenPrimary, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) color.copy(alpha = 0.25f) else NavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else Color.Transparent),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = if (isSelected) color else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
        )
    }
}
