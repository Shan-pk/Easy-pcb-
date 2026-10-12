package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
fun ManufacturingExportScreen(viewModel: EasyPcbViewModel) {
    val project by viewModel.currentProject.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var viewingFileContent by remember { mutableStateOf<Pair<String, String>?>(null) }

    if (project == null || project?.manufacturingPackage == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No manufacturing package available", color = TextSecondary)
        }
        return
    }

    val pkg = project!!.manufacturingPackage!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("mfg_export_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Package Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PcbGreenPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Production Manufacturing Package", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Gerber X2 • Excellon Drill • BOM • KiCad 8", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val allExport = StringBuilder().apply {
                                appendLine("=== EASY PCB FABRICATION PACKAGE: ${pkg.projectName} ===")
                                appendLine("--- BOM CSV ---")
                                appendLine(pkg.bomCsvContent)
                                appendLine("--- PICK & PLACE CSV ---")
                                appendLine(pkg.pickAndPlaceCsvContent)
                                appendLine("--- EXCELLON DRILL (.drl) ---")
                                appendLine(pkg.excellonDrillContent)
                            }.toString()

                            clipboard.setText(AnnotatedString(allExport))
                            Toast.makeText(context, "Full manufacturing archive copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PcbGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("download_all_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Copy Complete Fabrication Package (ZIP Bundle)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Mandatory Disclaimer Notice
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F2A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CopperGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = CopperGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = pkg.safetyNotice,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // File Manifest List
        item {
            Text(
                text = "Generated Production Files (${pkg.gerberManifest.size})",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        items(pkg.gerberManifest) { fileName ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavySurfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = SiliconTraceBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = fileName, color = TextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val content = when {
                                    fileName.contains("F_Cu") -> pkg.gerberFilesContent.values.firstOrNull() ?: ""
                                    fileName.contains("BOM") -> pkg.bomCsvContent
                                    fileName.contains("Centroid") -> pkg.pickAndPlaceCsvContent
                                    fileName.contains("drl") -> pkg.excellonDrillContent
                                    fileName.contains("kicad_sch") -> pkg.kicadSchContent
                                    fileName.contains("kicad_pcb") -> pkg.kicadPcbContent
                                    else -> "Ready for manufacturing review."
                                }
                                viewingFileContent = fileName to content
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = "View Code", tint = PcbGreenPrimary, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                val content = when {
                                    fileName.contains("BOM") -> pkg.bomCsvContent
                                    fileName.contains("Centroid") -> pkg.pickAndPlaceCsvContent
                                    fileName.contains("drl") -> pkg.excellonDrillContent
                                    fileName.contains("kicad_sch") -> pkg.kicadSchContent
                                    fileName.contains("kicad_pcb") -> pkg.kicadPcbContent
                                    else -> pkg.gerberFilesContent.values.firstOrNull() ?: ""
                                }
                                clipboard.setText(AnnotatedString(content))
                                Toast.makeText(context, "$fileName copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // File Content Inspection Box (if active)
        viewingFileContent?.let { (title, content) ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF03070C)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PcbGreenPrimary)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Preview: $title", color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "Close",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.clickable { viewingFileContent = null }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = content.take(600) + if (content.length > 600) "\n... [truncated for display]" else "",
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
