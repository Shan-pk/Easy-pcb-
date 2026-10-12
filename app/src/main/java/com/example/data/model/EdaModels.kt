package com.example.data.model

import java.util.UUID

// 1. Engineering Requirements Document
data class ProjectRequirements(
    val title: String = "Untitled Circuit",
    val purpose: String = "",
    val inputVoltageMin: Float = 5.0f,
    val inputVoltageMax: Float = 12.0f,
    val operatingVoltage: Float = 3.3f,
    val maxCurrentA: Float = 2.0f,
    val microcontroller: String = "ESP32-WROOM-32E",
    val interfaces: List<String> = listOf("Wi-Fi", "Bluetooth", "GPIO"),
    val channelCount: Int = 1,
    val targetBoardWidthMm: Float = 70.0f,
    val targetBoardHeightMm: Float = 50.0f,
    val layerCount: Int = 2,
    val safetyFeatures: List<String> = listOf("Reverse Polarity Protection", "Flyback Diodes", "Fuse"),
    val isolationRequired: Boolean = true,
    val operatingEnvironment: String = "Commercial / Indoor",
    val estimatedBudgetUsd: Float = 14.50f,
    val clarificationNotes: List<String> = emptyList()
)

// 2. System Architecture Block
data class ArchitectureBlock(
    val id: String,
    val name: String,
    val sectionType: String, // "Power", "MCU", "Driver", "Output", "Protection", "Interface"
    val description: String,
    val componentsUsed: List<String>,
    val inputNets: List<String>,
    val outputNets: List<String>
)

// 3. Electronic Component Specification
data class ComponentSpec(
    val refDes: String, // e.g. "U1", "R1", "C1", "Q1", "D1", "K1", "J1"
    val name: String,
    val mpn: String, // Manufacturer Part Number
    val manufacturer: String,
    val category: String,
    val value: String, // e.g. "10kΩ", "100nF", "12V/10A", "AMS1117-3.3"
    val packageType: String, // e.g. "0805", "SOT-223", "TO-220", "QFP-32", "Terminal Block 5.08mm"
    val voltageRating: String = "N/A",
    val currentRating: String = "N/A",
    val unitCostUsd: Float = 0.25f,
    val datasheetUrl: String = "https://www.alldatasheet.com",
    val inStock: Boolean = true
)

// 4. Schematic Symbol, Pins & Nets
data class SchematicPin(
    val number: String,
    val name: String,
    val pinType: String, // "POWER_IN", "POWER_OUT", "INPUT", "OUTPUT", "BIDIRECTIONAL", "PASSIVE"
    val netName: String,
    val offsetX: Float,
    val offsetY: Float
)

data class SchematicSymbol(
    val id: String = UUID.randomUUID().toString(),
    val refDes: String,
    val value: String,
    val footprint: String,
    val posX: Float,
    val posY: Float,
    val rotation: Float = 0f,
    val pins: List<SchematicPin> = emptyList()
)

data class SchematicNet(
    val name: String,
    val isPowerNet: Boolean = false,
    val voltageLevel: Float? = null,
    val pinConnections: List<String> = emptyList() // List of "RefDes:PinNumber"
)

data class ErcIssue(
    val severity: ErcSeverity, // ERROR, WARNING, INFO
    val code: String,
    val message: String,
    val componentRef: String? = null,
    val netName: String? = null
)

enum class ErcSeverity {
    ERROR, WARNING, INFO
}

data class SchematicDocument(
    val title: String,
    val revision: String = "v1.0",
    val symbols: List<SchematicSymbol> = emptyList(),
    val nets: List<SchematicNet> = emptyList(),
    val ercIssues: List<ErcIssue> = emptyList()
)

// 5. PCB Layout Models
enum class PcbLayer {
    F_CU,      // Front (Top) Copper
    B_CU,      // Back (Bottom) Copper
    F_SILK,    // Front Silkscreen
    B_SILK,    // Back Silkscreen
    F_MASK,    // Front Solder Mask
    B_MASK,    // Back Solder Mask
    EDGE_CUTS  // Board Outline
}

data class PcbPad(
    val padNumber: String,
    val netName: String,
    val relX: Float, // mm from footprint center
    val relY: Float,
    val widthMm: Float,
    val heightMm: Float,
    val shape: String = "RECT", // "RECT", "ROUND", "OVAL"
    val isThroughHole: Boolean = false,
    val drillDiameterMm: Float = 0.0f
)

data class PcbFootprintInstance(
    val id: String = UUID.randomUUID().toString(),
    val refDes: String,
    val value: String,
    val packageType: String,
    val posX: Float, // mm on board
    val posY: Float,
    val rotation: Float = 0f,
    val widthMm: Float,
    val heightMm: Float,
    val layer: PcbLayer = PcbLayer.F_CU,
    val pads: List<PcbPad> = emptyList()
)

data class PcbTrack(
    val id: String = UUID.randomUUID().toString(),
    val netName: String,
    val layer: PcbLayer = PcbLayer.F_CU,
    val startX: Float, // mm
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val widthMm: Float = 0.25f // 0.25mm signal, 0.6mm-1.0mm power
)

data class PcbVia(
    val id: String = UUID.randomUUID().toString(),
    val netName: String,
    val posX: Float,
    val posY: Float,
    val diameterMm: Float = 0.8f,
    val drillMm: Float = 0.4f
)

data class DrcViolation(
    val code: String,
    val title: String,
    val description: String,
    val severity: ErcSeverity,
    val locationX: Float? = null,
    val locationY: Float? = null
)

data class PcbBoardLayout(
    val widthMm: Float = 75.0f,
    val heightMm: Float = 55.0f,
    val layerCount: Int = 2,
    val copperThicknessOz: Float = 1.0f, // 1 oz / 35um
    val footprints: List<PcbFootprintInstance> = emptyList(),
    val tracks: List<PcbTrack> = emptyList(),
    val vias: List<PcbVia> = emptyList(),
    val routedRatio: Float = 1.0f,
    val totalTrackLengthMm: Float = 0.0f,
    val drcViolations: List<DrcViolation> = emptyList()
)

// 6. Circuit Simulation Models
data class SimWaveformPoint(
    val timeMs: Float,
    val value: Float
)

data class CircuitSimulationResult(
    val circuitType: String,
    val status: String,
    val dcOperatingPoints: Map<String, Float>, // Node Name -> Volts
    val waveforms: Map<String, List<SimWaveformPoint>>, // Trace Name -> Points
    val simulationLog: String,
    val warnings: List<String> = emptyList()
)

// 7. Manufacturing Export Manifest
data class ManufacturingPackage(
    val projectName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val gerberManifest: List<String>,
    val gerberFilesContent: Map<String, String>, // Filename -> RS-274X content
    val excellonDrillContent: String,
    val bomCsvContent: String,
    val pickAndPlaceCsvContent: String,
    val kicadSchContent: String,
    val kicadPcbContent: String,
    val safetyNotice: String = "Automated design checks cannot replace qualified engineering review, physical testing, and verification against the manufacturer's fabrication specifications."
)

// 8. Pipeline Stages
enum class GenerationStage(val stepNumber: Int, val title: String, val description: String) {
    REQUIREMENTS(1, "Requirements Analysis", "Extracting electrical specs, rails, thermal constraints & safety limits"),
    ARCHITECTURE(2, "Circuit Architecture", "Partitioning system blocks, power tree & protection topology"),
    COMPONENT_SELECTION(3, "Component Selection", "Verifying verified MPNs, footprints, ratings & datasheets"),
    SCHEMATIC_GEN(4, "Schematic Generation", "Synthesizing EDA netlists, symbols, pins and power rails"),
    ELECTRICAL_VALIDATION(5, "Electrical Rule Check (ERC)", "Validating voltages, current limits, pin compatibility & floating nets"),
    PCB_LAYOUT(6, "PCB Layout & Autorouting", "Arranging footprints, routing multi-layer copper tracks and vias"),
    MANUFACTURING_DRC(7, "DRC & Manufacturing Export", "Validating clearance rules, drills, Gerber files & BOM")
}

// 9. Full Easy PCB Project Entity
data class EasyPcbProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val userPrompt: String,
    val requirements: ProjectRequirements,
    val architectureBlocks: List<ArchitectureBlock>,
    val components: List<ComponentSpec>,
    val schematic: SchematicDocument,
    val pcbLayout: PcbBoardLayout,
    val simulationResult: CircuitSimulationResult? = null,
    val manufacturingPackage: ManufacturingPackage? = null,
    val currentStage: GenerationStage = GenerationStage.MANUFACTURING_DRC,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isProProject: Boolean = false,
    val language: String = "en"
)
