package com.example.data.engine

import com.example.data.model.ComponentSpec
import com.example.data.model.EasyPcbProject
import com.example.data.model.ManufacturingPackage
import com.example.data.model.PcbLayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EdaFileGenerator {

    fun generateManufacturingPackage(project: EasyPcbProject): ManufacturingPackage {
        val timestampStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val nameSafe = project.name.replace(" ", "_")

        // 1. Gerber Files
        val fCuGerber = generateGerberLayer(project, PcbLayer.F_CU, "Top Copper (F.Cu)")
        val bCuGerber = generateGerberLayer(project, PcbLayer.B_CU, "Bottom Copper (B.Cu)")
        val fMaskGerber = generateGerberLayer(project, PcbLayer.F_MASK, "Top Solder Mask (F.Mask)")
        val bMaskGerber = generateGerberLayer(project, PcbLayer.B_MASK, "Bottom Solder Mask (B.Mask)")
        val fSilkGerber = generateGerberLayer(project, PcbLayer.F_SILK, "Top Silkscreen (F.Silkscreen)")
        val edgeCutsGerber = generateGerberEdgeCuts(project)

        val gerberMap = mapOf(
            "${nameSafe}-F_Cu.gbr" to fCuGerber,
            "${nameSafe}-B_Cu.gbr" to bCuGerber,
            "${nameSafe}-F_Mask.gbr" to fMaskGerber,
            "${nameSafe}-B_Mask.gbr" to bMaskGerber,
            "${nameSafe}-F_Silkscreen.gbr" to fSilkGerber,
            "${nameSafe}-Edge_Cuts.gbr" to edgeCutsGerber
        )

        // 2. Excellon Drill File
        val drillContent = generateExcellonDrill(project)

        // 3. Bill of Materials (BOM) CSV
        val bomContent = generateBomCsv(project.components)

        // 4. Pick-and-Place (Centroid) CSV
        val pnpContent = generatePickAndPlaceCsv(project)

        // 5. KiCad 8 Schematic (.kicad_sch)
        val kicadSch = generateKiCadSchematic(project)

        // 6. KiCad 8 Board (.kicad_pcb)
        val kicadPcb = generateKiCadPcb(project)

        val manifestList = listOf(
            "${nameSafe}-F_Cu.gbr (Top Copper)",
            "${nameSafe}-B_Cu.gbr (Bottom Copper)",
            "${nameSafe}-F_Mask.gbr (Top Solder Mask)",
            "${nameSafe}-B_Mask.gbr (Bottom Solder Mask)",
            "${nameSafe}-F_Silkscreen.gbr (Top Silkscreen Legend)",
            "${nameSafe}-Edge_Cuts.gbr (Board Mechanical Outline)",
            "${nameSafe}-PTH-drl.drl (Excellon NC Drill)",
            "${nameSafe}-BOM.csv (Bill of Materials)",
            "${nameSafe}-Centroid.pos (SMT Pick and Place)",
            "${nameSafe}.kicad_sch (KiCad 8 Schematic)",
            "${nameSafe}.kicad_pcb (KiCad 8 PCB Layout)",
            "MANIFEST.txt (Fabrication Notes & DRC Verification Status)"
        )

        return ManufacturingPackage(
            projectName = project.name,
            timestamp = System.currentTimeMillis(),
            gerberManifest = manifestList,
            gerberFilesContent = gerberMap,
            excellonDrillContent = drillContent,
            bomCsvContent = bomContent,
            pickAndPlaceCsvContent = pnpContent,
            kicadSchContent = kicadSch,
            kicadPcbContent = kicadPcb
        )
    }

    private fun generateGerberLayer(project: EasyPcbProject, layer: PcbLayer, description: String): String {
        val sb = StringBuilder()
        sb.appendLine("G04 Easy PCB v1.0 Gerber Generator*")
        sb.appendLine("G04 Layer: $description*")
        sb.appendLine("G04 Project: ${project.name}*")
        sb.appendLine("%FSLAX35Y35*%") // Format specification 3.5 leading zeros omitted
        sb.appendLine("%MOMM*%") // Millimeters mode
        sb.appendLine("%LPD*%")  // Dark polarity
        sb.appendLine("%ADD10C,0.2500*%") // Aperture 10: Circle 0.25mm (signal trace)
        sb.appendLine("%ADD11C,0.8000*%") // Aperture 11: Circle 0.80mm (power trace)
        sb.appendLine("%ADD12R,1.6000X1.0000*%") // Aperture 12: SMD Rect Pad 1.6x1.0mm
        sb.appendLine("%ADD13C,1.6000*%") // Aperture 13: Circular PTH Pad 1.6mm
        sb.appendLine("G01*") // Linear interpolation
        sb.appendLine("D10*") // Select 0.25mm aperture

        // Render tracks for this layer
        val layerTracks = project.pcbLayout.tracks.filter { it.layer == layer }
        for (track in layerTracks) {
            val startX = (track.startX * 100000).toInt()
            val startY = (track.startY * 100000).toInt()
            val endX = (track.endX * 100000).toInt()
            val endY = (track.endY * 100000).toInt()
            sb.appendLine("X${startX}Y${startY}D02*") // Move to start
            sb.appendLine("X${endX}Y${endY}D01*")     // Draw line with exposure
        }

        // Render pads
        sb.appendLine("D12*")
        for (fp in project.pcbLayout.footprints) {
            for (pad in fp.pads) {
                val padX = ((fp.posX + pad.relX) * 100000).toInt()
                val padY = ((fp.posY + pad.relY) * 100000).toInt()
                sb.appendLine("X${padX}Y${padY}D03*") // Flash pad
            }
        }

        sb.appendLine("M02*") // End of file
        return sb.toString()
    }

    private fun generateGerberEdgeCuts(project: EasyPcbProject): String {
        val width = (project.pcbLayout.widthMm * 100000).toInt()
        val height = (project.pcbLayout.heightMm * 100000).toInt()
        return """
            G04 Easy PCB - Mechanical Board Outline (Edge.Cuts)*
            %FSLAX35Y35*%
            %MOMM*%
            %LPD*%
            %ADD10C,0.1500*%
            G01*
            D10*
            X0Y0D02*
            X${width}Y0D01*
            X${width}Y${height}D01*
            X0Y${height}D01*
            X0Y0D01*
            M02*
        """.trimIndent()
    }

    private fun generateExcellonDrill(project: EasyPcbProject): String {
        val sb = StringBuilder()
        sb.appendLine("M48")
        sb.appendLine("; Easy PCB Excellon Drill Export")
        sb.appendLine("METRIC,TZ")
        sb.appendLine("T01C0.400") // Tool 1: 0.4mm Via drill
        sb.appendLine("T02C1.000") // Tool 2: 1.0mm Component Pin drill
        sb.appendLine("T03C3.200") // Tool 3: 3.2mm M3 Mounting Hole drill
        sb.appendLine("%")
        sb.appendLine("G90") // Absolute coordinates

        // Vias
        sb.appendLine("T01")
        for (via in project.pcbLayout.vias) {
            val vx = String.format(Locale.US, "%.3f", via.posX)
            val vy = String.format(Locale.US, "%.3f", via.posY)
            sb.appendLine("X${vx}Y${vy}")
        }

        // Through-hole pads
        sb.appendLine("T02")
        for (fp in project.pcbLayout.footprints) {
            for (pad in fp.pads.filter { it.isThroughHole }) {
                val px = String.format(Locale.US, "%.3f", fp.posX + pad.relX)
                val py = String.format(Locale.US, "%.3f", fp.posY + pad.relY)
                sb.appendLine("X${px}Y${py}")
            }
        }

        // M3 Mounting holes in corners
        sb.appendLine("T03")
        sb.appendLine("X4.000Y4.000")
        sb.appendLine(String.format(Locale.US, "X%.3fY4.000", project.pcbLayout.widthMm - 4f))
        sb.appendLine(String.format(Locale.US, "X4.000Y%.3f", project.pcbLayout.heightMm - 4f))
        sb.appendLine(String.format(Locale.US, "X%.3fY%.3f", project.pcbLayout.widthMm - 4f, project.pcbLayout.heightMm - 4f))

        sb.appendLine("M30")
        return sb.toString()
    }

    private fun generateBomCsv(components: List<ComponentSpec>): String {
        val sb = StringBuilder()
        sb.appendLine("Designator,Quantity,Name,MPN,Manufacturer,Value,Package,Unit_Price_USD")
        val grouped = components.groupBy { "${it.name}_${it.value}_${it.packageType}" }
        for ((_, group) in grouped) {
            val first = group.first()
            val designators = group.joinToString(" ") { it.refDes }
            val count = group.size
            sb.appendLine("\"$designators\",$count,\"${first.name}\",\"${first.mpn}\",\"${first.manufacturer}\",\"${first.value}\",\"${first.packageType}\",${first.unitCostUsd}")
        }
        return sb.toString()
    }

    private fun generatePickAndPlaceCsv(project: EasyPcbProject): String {
        val sb = StringBuilder()
        sb.appendLine("Designator,Val,Package,Mid X (mm),Mid Y (mm),Rotation,Layer")
        for (fp in project.pcbLayout.footprints) {
            val layerName = if (fp.layer == PcbLayer.F_CU) "Top" else "Bottom"
            sb.appendLine("${fp.refDes},\"${fp.value}\",\"${fp.packageType}\",${String.format(Locale.US, "%.2f", fp.posX)},${String.format(Locale.US, "%.2f", fp.posY)},${fp.rotation.toInt()},$layerName")
        }
        return sb.toString()
    }

    private fun generateKiCadSchematic(project: EasyPcbProject): String {
        val sb = StringBuilder()
        sb.appendLine("(kicad_sch (version 20231120) (generator \"Easy PCB AI Studio\")")
        sb.appendLine("  (uuid \"${project.id}\")")
        sb.appendLine("  (paper \"A4\")")
        sb.appendLine("  (title_block (title \"${project.name}\") (date \"${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())}\") (rev \"v1.0\"))")

        for (sym in project.schematic.symbols) {
            sb.appendLine("  (symbol (lib_id \"Device:${sym.value}\") (at ${sym.posX} ${sym.posY} ${sym.rotation.toInt()}) (unit 1)")
            sb.appendLine("    (property \"Reference\" \"${sym.refDes}\" (at ${sym.posX} ${sym.posY - 4} 0))")
            sb.appendLine("    (property \"Value\" \"${sym.value}\" (at ${sym.posX} ${sym.posY + 4} 0))")
            sb.appendLine("    (property \"Footprint\" \"${sym.footprint}\" (at ${sym.posX} ${sym.posY + 8} 0))")
            sb.appendLine("  )")
        }

        for (net in project.schematic.nets) {
            sb.appendLine("  (label \"${net.name}\" (at 50.0 50.0 0))")
        }

        sb.appendLine(")")
        return sb.toString()
    }

    private fun generateKiCadPcb(project: EasyPcbProject): String {
        val sb = StringBuilder()
        sb.appendLine("(kicad_pcb (version 20231120) (generator \"Easy PCB AI Studio\")")
        sb.appendLine("  (general (thickness 1.6))")
        sb.appendLine("  (layers (0 \"F.Cu\" signal) (31 \"B.Cu\" signal) (36 \"B.SilkS\" user) (37 \"F.SilkS\" user) (38 \"B.Mask\" user) (39 \"F.Mask\" user) (44 \"Edge.Cuts\" user))")
        
        // Edge cuts
        val w = project.pcbLayout.widthMm
        val h = project.pcbLayout.heightMm
        sb.appendLine("  (gr_rect (start 0 0) (end $w $h) (stroke (width 0.15) (type solid)) (layer \"Edge.Cuts\"))")

        // Footprints
        for (fp in project.pcbLayout.footprints) {
            val layerStr = if (fp.layer == PcbLayer.F_CU) "F.Cu" else "B.Cu"
            sb.appendLine("  (footprint \"${fp.packageType}\" (layer \"$layerStr\") (at ${fp.posX} ${fp.posY} ${fp.rotation})")
            sb.appendLine("    (property \"Reference\" \"${fp.refDes}\" (at 0 -2 0) (layer \"F.SilkS\"))")
            for (pad in fp.pads) {
                val padType = if (pad.isThroughHole) "thru_hole" else "smd"
                val shape = if (pad.shape == "ROUND") "circle" else "rect"
                sb.appendLine("    (pad \"${pad.padNumber}\" $padType $shape (at ${pad.relX} ${pad.relY}) (size ${pad.widthMm} ${pad.heightMm}) (layers \"$layerStr\"))")
            }
            sb.appendLine("  )")
        }

        // Tracks
        for (track in project.pcbLayout.tracks) {
            val layerStr = if (track.layer == PcbLayer.F_CU) "F.Cu" else "B.Cu"
            sb.appendLine("  (segment (start ${track.startX} ${track.startY}) (end ${track.endX} ${track.endY}) (width ${track.widthMm}) (layer \"$layerStr\") (net 1))")
        }

        sb.appendLine(")")
        return sb.toString()
    }
}
