package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun LearningCenterScreen(viewModel: EasyPcbViewModel) {
    val ohmsV by viewModel.ohmsVoltage.collectAsState()
    val ohmsI by viewModel.ohmsCurrent.collectAsState()
    val ohmsR by viewModel.ohmsResistance.collectAsState()

    val traceI by viewModel.traceCurrent.collectAsState()
    val traceOz by viewModel.traceThicknessOz.collectAsState()
    val computedWidth = viewModel.calculateTraceWidth(traceI, traceOz)

    var vDivIn by remember { mutableStateOf("12.0") }
    var vDivR1 by remember { mutableStateOf("10000") }
    var vDivR2 by remember { mutableStateOf("3300") }
    val vOut = run {
        val vin = vDivIn.toFloatOrNull() ?: 12f
        val r1 = vDivR1.toFloatOrNull() ?: 10000f
        val r2 = vDivR2.toFloatOrNull() ?: 3300f
        if (r1 + r2 > 0) vin * (r2 / (r1 + r2)) else 0f
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("learning_center_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Electronics Learning & Engineering Calculators", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Educational formulas, calculators, and tutorials in English & Malayalam", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Calculator 1: Ohm's Law
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = SiliconTraceBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Ohm's Law Calculator (V = I × R)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = ohmsV,
                            onValueChange = { viewModel.calculateOhmsLaw(it, ohmsI) },
                            label = { Text("Voltage (V)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = calcFieldColors()
                        )
                        OutlinedTextField(
                            value = ohmsI,
                            onValueChange = { viewModel.calculateOhmsLaw(ohmsV, it) },
                            label = { Text("Current (A)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = calcFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Required Resistance (R):", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "$ohmsR Ω", color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Calculator 2: IPC-2152 PCB Trace Width Calculator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = CopperGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "IPC-2152 PCB Trace Width Calculator", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(
                        text = "Calculates minimum copper trace width for 10°C thermal rise.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = traceI,
                            onValueChange = { viewModel.calculateTraceWidth(it, traceOz) },
                            label = { Text("Current (A)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = calcFieldColors()
                        )
                        OutlinedTextField(
                            value = traceOz,
                            onValueChange = { viewModel.calculateTraceWidth(traceI, it) },
                            label = { Text("Copper Weight (oz)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = calcFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Recommended Trace Width:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", computedWidth)} mm (${(computedWidth * 39.37f).toInt()} mils)",
                                color = CopperGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Calculator 3: Voltage Divider
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Resistor Voltage Divider (Level Shifter)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = vDivIn,
                            onValueChange = { vDivIn = it },
                            label = { Text("V_in", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = calcFieldColors()
                        )
                        OutlinedTextField(
                            value = vDivR1,
                            onValueChange = { vDivR1 = it },
                            label = { Text("R1 (Ω)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = calcFieldColors()
                        )
                        OutlinedTextField(
                            value = vDivR2,
                            onValueChange = { vDivR2 = it },
                            label = { Text("R2 (Ω)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = calcFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Output Voltage (V_out):", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", vOut)} V",
                                color = PcbGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Malayalam & English Educational Guides
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2030)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SiliconTraceBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = SiliconTraceBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ഇലക്ട്രോണിക്സ് പാഠങ്ങൾ (Malayalam Tutorials)", color = SiliconTraceBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. ഡീകപ്ലിംഗ് കപ്പാസിറ്റർ (Decoupling Capacitor): മൈക്രോകൺട്രോളറിന്റെ VCC പിന്നിന് തൊട്ടടുത്ത് 100nF (0.1µF) സെറാമിക് കപ്പാസിറ്റർ നൽകുന്നത് വോൾട്ടേജ് നോയ്സ് ഒഴിവാക്കാൻ സഹായിക്കുന്നു.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "2. ഗ്രൗണ്ട് പ്ലേൻ (Ground Plane): PCB യുടെ താഴത്തെ വശത്ത് വലിയ ഗ്രൗണ്ട് പ്ലേൻ നൽകുന്നത് സിഗ്നലുകളുടെ ഇന്റർഫെറൻസ് കുറയ്ക്കുന്നു.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun calcFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = NavySurfaceVariant,
    unfocusedContainerColor = NavySurfaceVariant,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = PcbGreenPrimary,
    unfocusedBorderColor = NavySurfaceContainer
)
