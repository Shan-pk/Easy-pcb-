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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchematicSymbol
import com.example.ui.theme.CopperGold
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceContainer
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PcbGreenPrimary
import com.example.ui.theme.SiliconTraceBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.EasyPcbViewModel

@Composable
fun SchematicEditorScreen(
    viewModel: EasyPcbViewModel,
    onNavigateBack: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    var selectedSymbol by remember { mutableStateOf<SchematicSymbol?>(null) }
    var highlightedNet by remember { mutableStateOf<String?>(null) }

    // Canvas Transform State
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active schematic project", color = TextSecondary)
        }
        return
    }

    val schematic = project!!.schematic

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("schematic_editor_screen")
    ) {
        // Schematic Toolbar
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
                        text = schematic.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${schematic.symbols.size} Symbols • ${schematic.nets.size} Nets • KiCad 8 Compatible",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { zoomScale = (zoomScale * 1.2f).coerceAtMost(3.0f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary)
                    }
                    IconButton(
                        onClick = { zoomScale = (zoomScale / 1.2f).coerceAtLeast(0.5f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary)
                    }
                    IconButton(
                        onClick = {
                            zoomScale = 1.0f
                            panOffsetX = 0f
                            panOffsetY = 0f
                            highlightedNet = null
                            selectedSymbol = null
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset View", tint = PcbGreenPrimary)
                    }
                }
            }
        }

        // Net Highlighting Quick Filter Pills
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
                Text(text = "Nets:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        NetHighlightChip(
                            name = "ALL",
                            isSelected = highlightedNet == null,
                            onClick = { highlightedNet = null }
                        )
                    }
                    items(schematic.nets) { net ->
                        NetHighlightChip(
                            name = net.name,
                            isSelected = highlightedNet == net.name,
                            onClick = { highlightedNet = if (highlightedNet == net.name) null else net.name }
                        )
                    }
                }
            }
        }

        // Interactive Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF071018))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.4f, 3.5f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                    }
                }
                .testTag("schematic_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 1. Engineering Grid
                val gridStep = 25f * zoomScale
                val startX = (panOffsetX % gridStep)
                val startY = (panOffsetY % gridStep)

                var x = startX
                while (x < canvasWidth) {
                    drawLine(
                        color = Color(0x1838BDF8),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = 0.8f
                    )
                    x += gridStep
                }

                var y = startY
                while (y < canvasHeight) {
                    drawLine(
                        color = Color(0x1838BDF8),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 0.8f
                    )
                    y += gridStep
                }

                // 2. Render Net Wires connecting components
                val symbols = schematic.symbols
                for (i in 0 until symbols.size - 1) {
                    val s1 = symbols[i]
                    val s2 = symbols[i + 1]

                    val sx1 = (s1.posX * 3.5f * zoomScale) + panOffsetX + 100f
                    val sy1 = (s1.posY * 3.5f * zoomScale) + panOffsetY + 80f
                    val sx2 = (s2.posX * 3.5f * zoomScale) + panOffsetX + 100f
                    val sy2 = (s2.posY * 3.5f * zoomScale) + panOffsetY + 80f

                    val isHighlighted = highlightedNet != null
                    val wireColor = if (isHighlighted) PcbGreenPrimary else Color(0xFF1E88E5)
                    val strokeW = if (isHighlighted) 2.5f else 1.5f

                    // Manhattan routing (L-shaped orthogonal wire)
                    val midX = (sx1 + sx2) / 2f
                    drawLine(color = wireColor, start = Offset(sx1, sy1), end = Offset(midX, sy1), strokeWidth = strokeW)
                    drawLine(color = wireColor, start = Offset(midX, sy1), end = Offset(midX, sy2), strokeWidth = strokeW)
                    drawLine(color = wireColor, start = Offset(midX, sy2), end = Offset(sx2, sy2), strokeWidth = strokeW)

                    // Junction dot
                    drawCircle(color = wireColor, radius = 3.5f * zoomScale, center = Offset(midX, sy1))
                }

                // 3. Render Symbols
                for (sym in symbols) {
                    val cx = (sym.posX * 3.5f * zoomScale) + panOffsetX + 100f
                    val cy = (sym.posY * 3.5f * zoomScale) + panOffsetY + 80f
                    val boxW = 55f * zoomScale
                    val boxH = 40f * zoomScale

                    val isSelected = selectedSymbol?.id == sym.id
                    val bodyColor = if (isSelected) Color(0xFF163826) else Color(0xFF0F1E2E)
                    val borderColor = if (isSelected) PcbGreenPrimary else SiliconTraceBlue

                    // Symbol Box
                    drawRect(
                        color = bodyColor,
                        topLeft = Offset(cx - boxW / 2, cy - boxH / 2),
                        size = Size(boxW, boxH)
                    )
                    drawRect(
                        color = borderColor,
                        topLeft = Offset(cx - boxW / 2, cy - boxH / 2),
                        size = Size(boxW, boxH),
                        style = Stroke(width = if (isSelected) 2.5f else 1.5f)
                    )

                    // Pins
                    val pinLen = 14f * zoomScale
                    // Left pin (Pin 1)
                    drawLine(color = borderColor, start = Offset(cx - boxW / 2 - pinLen, cy - 8f * zoomScale), end = Offset(cx - boxW / 2, cy - 8f * zoomScale), strokeWidth = 1.5f)
                    drawCircle(color = CopperGold, radius = 2f * zoomScale, center = Offset(cx - boxW / 2 - pinLen, cy - 8f * zoomScale))

                    // Left pin (Pin 2)
                    drawLine(color = borderColor, start = Offset(cx - boxW / 2 - pinLen, cy + 8f * zoomScale), end = Offset(cx - boxW / 2, cy + 8f * zoomScale), strokeWidth = 1.5f)
                    drawCircle(color = CopperGold, radius = 2f * zoomScale, center = Offset(cx - boxW / 2 - pinLen, cy + 8f * zoomScale))

                    // Right pin (Pin 3)
                    drawLine(color = borderColor, start = Offset(cx + boxW / 2, cy), end = Offset(cx + boxW / 2 + pinLen, cy), strokeWidth = 1.5f)
                    drawCircle(color = CopperGold, radius = 2f * zoomScale, center = Offset(cx + boxW / 2 + pinLen, cy))
                }
            }

            // Clickable Overlays for Symbols
            for (sym in schematic.symbols) {
                val cx = (sym.posX * 3.5f * zoomScale) + panOffsetX + 100f
                val cy = (sym.posY * 3.5f * zoomScale) + panOffsetY + 80f
                val boxW = 55f * zoomScale
                val boxH = 40f * zoomScale

                Box(
                    modifier = Modifier
                        .offset(
                            x = androidx.compose.ui.unit.Dp(cx - boxW / 2),
                            y = androidx.compose.ui.unit.Dp(cy - boxH / 2)
                        )
                        .size(
                            width = androidx.compose.ui.unit.Dp(boxW),
                            height = androidx.compose.ui.unit.Dp(boxH)
                        )
                        .clickable { selectedSymbol = sym }
                )
            }
        }

        // Bottom Property Inspector (if symbol selected)
        selectedSymbol?.let { sym ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("symbol_inspector_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PcbGreenPrimary)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sym.refDes,
                                color = PcbGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sym.value,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        IconButton(
                            onClick = { selectedSymbol = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Footprint: ${sym.footprint}", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pins (${sym.pins.size}):",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    sym.pins.forEach { pin ->
                        Text(
                            text = "Pin ${pin.number}: ${pin.name} (${pin.pinType}) ➔ Net: ${pin.netName}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetHighlightChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) PcbGreenPrimary else NavySurface,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.Black else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
        )
    }
}
