package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopperGold
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PcbGreenPrimary
import com.example.ui.theme.PcbSilkscreenWhite
import com.example.ui.theme.PcbSubstrateBlack
import com.example.ui.theme.PcbSubstrateBlue
import com.example.ui.theme.PcbSubstrateGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.EasyPcbViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Board3DViewerScreen(
    viewModel: EasyPcbViewModel,
    onNavigateBack: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    val maskColor by viewModel.solderMaskColor.collectAsState()
    val rotX by viewModel.rotX.collectAsState()
    val rotY by viewModel.rotY.collectAsState()

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active project for 3D view", color = TextSecondary)
        }
        return
    }

    val pcb = project!!.pcbLayout

    val substrateColor = when (maskColor) {
        "Black" -> PcbSubstrateBlack
        "Blue" -> PcbSubstrateBlue
        else -> PcbSubstrateGreen
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("board_3d_viewer_screen")
    ) {
        // Toolbar & Mask Color Toggles
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NavySurface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "3D PCB Board Viewer", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Drag to rotate in 3D • 1.6mm Standard FR4", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                // Solder Mask Palette Selector
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ColorPickerButton(name = "Green", color = PcbSubstrateGreen, isSelected = maskColor == "Green") {
                        viewModel.setSolderMaskColor("Green")
                    }
                    ColorPickerButton(name = "Black", color = Color(0xFF1E242B), isSelected = maskColor == "Black") {
                        viewModel.setSolderMaskColor("Black")
                    }
                    ColorPickerButton(name = "Blue", color = Color(0xFF133B63), isSelected = maskColor == "Blue") {
                        viewModel.setSolderMaskColor("Blue")
                    }
                    IconButton(
                        onClick = {
                            viewModel.update3dRotation(35f - rotY, -25f - rotX)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Camera", tint = TextPrimary)
                    }
                }
            }
        }

        // 3D Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF070E16))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.update3dRotation(dragAmount.x * 0.4f, -dragAmount.y * 0.4f)
                    }
                }
                .testTag("board_3d_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Isometric math with rotation angles
                val radX = Math.toRadians(rotX.toDouble())
                val radY = Math.toRadians(rotY.toDouble())

                val scale = 3.2f
                val bw = pcb.widthMm * scale
                val bh = pcb.heightMm * scale
                val bThick = 6.0f * scale // 1.6mm thickness exaggerated for 3D realism

                val centerX = canvasWidth / 2f
                val centerY = canvasHeight / 2f

                // Project 3D point (x, y, z) into 2D screen coordinates
                fun project3D(x: Float, y: Float, z: Float): Offset {
                    // Yaw (around Y axis)
                    val x1 = x * cos(radY).toFloat() + z * sin(radY).toFloat()
                    val z1 = -x * sin(radY).toFloat() + z * cos(radY).toFloat()
                    // Pitch (around X axis)
                    val y2 = y * cos(radX).toFloat() - z1 * sin(radX).toFloat()
                    val z2 = y * sin(radX).toFloat() + z1 * cos(radX).toFloat()

                    return Offset(centerX + x1, centerY + y2)
                }

                val halfW = bw / 2f
                val halfH = bh / 2f

                // 8 Vertices of the PCB Substrate Box
                val pTopLeftTop = project3D(-halfW, -halfH, 0f)
                val pTopRightTop = project3D(halfW, -halfH, 0f)
                val pBottomRightTop = project3D(halfW, halfH, 0f)
                val pBottomLeftTop = project3D(-halfW, halfH, 0f)

                val pTopLeftBot = project3D(-halfW, -halfH, bThick)
                val pTopRightBot = project3D(halfW, -halfH, bThick)
                val pBottomRightBot = project3D(halfW, halfH, bThick)
                val pBottomLeftBot = project3D(-halfW, halfH, bThick)

                // 1. Draw Substrate Thickness Edges (FR4 Core side walls)
                val edgeColor = substrateColor.copy(alpha = 0.85f)
                val sideDark = Color(0xFF04180F)

                // Front Side Wall
                val frontWallPath = Path().apply {
                    moveTo(pBottomLeftTop.x, pBottomLeftTop.y)
                    lineTo(pBottomRightTop.x, pBottomRightTop.y)
                    lineTo(pBottomRightBot.x, pBottomRightBot.y)
                    lineTo(pBottomLeftBot.x, pBottomLeftBot.y)
                    close()
                }
                drawPath(frontWallPath, color = sideDark)

                // Right Side Wall
                val rightWallPath = Path().apply {
                    moveTo(pTopRightTop.x, pTopRightTop.y)
                    lineTo(pBottomRightTop.x, pBottomRightTop.y)
                    lineTo(pBottomRightBot.x, pBottomRightBot.y)
                    lineTo(pTopRightBot.x, pTopRightBot.y)
                    close()
                }
                drawPath(rightWallPath, color = sideDark.copy(alpha = 0.9f))

                // 2. Draw Top Solder Mask Face
                val topFacePath = Path().apply {
                    moveTo(pTopLeftTop.x, pTopLeftTop.y)
                    lineTo(pTopRightTop.x, pTopRightTop.y)
                    lineTo(pBottomRightTop.x, pBottomRightTop.y)
                    lineTo(pBottomLeftTop.x, pBottomLeftTop.y)
                    close()
                }
                drawPath(topFacePath, color = substrateColor)
                drawPath(topFacePath, color = CopperGold.copy(alpha = 0.8f), style = Stroke(width = 1.5f))

                // 3. Render 3D Component Models on Top Face
                for (fp in pcb.footprints) {
                    val localX = (fp.posX - pcb.widthMm / 2f) * scale
                    val localY = (fp.posY - pcb.heightMm / 2f) * scale
                    val compW = fp.widthMm * scale
                    val compH = fp.heightMm * scale

                    // 3D package height based on component type
                    val compHeight = when {
                        fp.refDes.startsWith("K") -> 14f * scale // Songle Relay (tall blue cube)
                        fp.refDes.startsWith("J") -> 10f * scale // Screw terminal block (green/black block)
                        fp.refDes.startsWith("U1") -> 3.5f * scale // ESP32 shield can
                        fp.refDes.startsWith("U2") -> 4.0f * scale // TO-263 Buck IC
                        fp.refDes.startsWith("C1") -> 8.0f * scale // Electrolytic capacitor cylinder
                        else -> 1.5f * scale // SMT passives
                    }

                    val compColor = when {
                        fp.refDes.startsWith("K") -> Color(0xFF1E5BB0) // Classic blue relay
                        fp.refDes.startsWith("J") -> Color(0xFF14532D) // Phoenix screw terminal green
                        fp.refDes.startsWith("U1") -> Color(0xFF94A3B8) // Nickel ESP32 shield can
                        fp.refDes.startsWith("C1") -> Color(0xFF1E293B) // Black cap
                        else -> Color(0xFF232B36) // Molded epoxy IC
                    }

                    // Render component 3D bounding prism
                    val cTopLeft = project3D(localX - compW / 2, localY - compH / 2, -compHeight)
                    val cTopRight = project3D(localX + compW / 2, localY - compH / 2, -compHeight)
                    val cBottomRight = project3D(localX + compW / 2, localY + compH / 2, -compHeight)
                    val cBottomLeft = project3D(localX - compW / 2, localY + compH / 2, -compHeight)

                    val compPath = Path().apply {
                        moveTo(cTopLeft.x, cTopLeft.y)
                        lineTo(cTopRight.x, cTopRight.y)
                        lineTo(cBottomRight.x, cBottomRight.y)
                        lineTo(cBottomLeft.x, cBottomLeft.y)
                        close()
                    }
                    drawPath(compPath, color = compColor)
                    drawPath(compPath, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 1f))

                    // Copper Solder Pads / Pins around component base
                    for (pad in fp.pads) {
                        val padBase = project3D(localX + (pad.relX * scale), localY + (pad.relY * scale), 0f)
                        drawCircle(color = CopperGold, radius = 2.5f, center = padBase)
                    }
                }
            }
        }

        // Layer Stack Information Panel
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NavySurface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "PCB Layer Stack Specifications", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Top Solder Mask (0.1mm)", color = PcbGreenPrimary, fontSize = 11.sp)
                    Text(text = "1oz (35µm) Copper", color = CopperGold, fontSize = 11.sp)
                    Text(text = "FR4 Core (1.5mm)", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "Bottom 1oz Copper", color = CopperGold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ColorPickerButton(name: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, RoundedCornerShape(2.dp))
            )
        }
    }
}
