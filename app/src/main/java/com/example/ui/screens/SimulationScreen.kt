package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopperGold
import com.example.ui.theme.DrcWarningAmber
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
fun SimulationScreen(
    viewModel: EasyPcbViewModel
) {
    val project by viewModel.currentProject.collectAsState()

    if (project == null || project?.simulationResult == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No simulation data available", color = TextSecondary)
        }
        return
    }

    val sim = project!!.simulationResult!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("simulation_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Circuit SPICE Simulation", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PcbGreenPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = sim.status,
                                color = PcbGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = sim.circuitType, color = TextSecondary, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.reRunSimulation() },
                        colors = ButtonDefaults.buttonColors(containerColor = PcbGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("rerun_simulation_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Re-Run Transient & DC Simulation", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Oscilloscope Waveform Display
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF040A10)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PcbGreenPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Oscilloscope Waveform Display", color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "CH1: Green  •  CH2: Cyan  •  CH3: Amber", color = TextMuted, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Waveform Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF02060A))
                            .border(1.dp, Color(0xFF1E3A2B))
                            .testTag("oscilloscope_canvas")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Oscilloscope Grid Lines (10x8 divisions)
                            val gridCols = 10
                            val gridRows = 8
                            for (i in 0..gridCols) {
                                val gx = i * (w / gridCols)
                                drawLine(color = Color(0x1534D399), start = Offset(gx, 0f), end = Offset(gx, h), strokeWidth = 0.8f)
                            }
                            for (j in 0..gridRows) {
                                val gy = j * (h / gridRows)
                                drawLine(color = Color(0x1534D399), start = Offset(0f, gy), end = Offset(w, gy), strokeWidth = 0.8f)
                            }

                            // Draw Waveform Traces
                            val colors = listOf(PcbGreenPrimary, SiliconTraceBlue, CopperGold)
                            var colorIdx = 0

                            for ((traceName, points) in sim.waveforms) {
                                if (points.size < 2) continue
                                val traceColor = colors[colorIdx % colors.size]
                                colorIdx++

                                val maxVal = points.maxOf { it.value }.coerceAtLeast(1f)
                                val minVal = points.minOf { it.value }.coerceAtMost(0f)
                                val valRange = (maxVal - minVal).coerceAtLeast(0.1f)

                                val maxTime = points.last().timeMs
                                val minTime = points.first().timeMs
                                val timeRange = (maxTime - minTime).coerceAtLeast(0.1f)

                                val path = Path()
                                for ((idx, pt) in points.withIndex()) {
                                    val px = ((pt.timeMs - minTime) / timeRange) * w
                                    // Inverted Y (0 at bottom)
                                    val py = h - (((pt.value - minVal) / valRange) * (h * 0.85f)) - (h * 0.08f)

                                    if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                                }

                                drawPath(path, color = traceColor, style = Stroke(width = 2.0f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Time Division: 50µs/div • Voltage: 2V/div",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // DC Node Operating Points Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DC Operating Points (Node Voltages)",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    sim.dcOperatingPoints.forEach { (node, volts) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = node, color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", volts)} V",
                                color = PcbGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // SPICE Log Output
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF03070C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "SPICE Solver Log", color = SiliconTraceBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sim.simulationLog,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Simulation Scope Notice
        item {
            sim.warnings.forEach { warning ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DrcWarningAmber)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = DrcWarningAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = warning, color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
