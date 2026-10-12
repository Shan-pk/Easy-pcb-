package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.AiEdaEngine
import com.example.data.engine.CircuitSimulator
import com.example.data.engine.EdaFileGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Easy PCB", appName)
    }

    @Test
    fun `ai eda engine synthesizes valid circuit`() {
        val prompt = "Design a Wi-Fi-controlled 4-channel relay controller using ESP32, 12V input, 5V regulation, screw terminals, status LEDs, and protection against reverse polarity."
        val project = AiEdaEngine.synthesizeCircuitFromPrompt(prompt)

        assertNotNull(project)
        assertEquals(4, project.requirements.channelCount)
        assertTrue(project.components.isNotEmpty())
        assertTrue(project.schematic.symbols.isNotEmpty())
        assertTrue(project.schematic.nets.isNotEmpty())
        assertEquals(2, project.pcbLayout.layerCount)
        assertTrue(project.pcbLayout.routedRatio >= 1.0f)
    }

    @Test
    fun `spice circuit simulation executes and produces waveforms`() {
        val project = AiEdaEngine.synthesizeCircuitFromPrompt("Design a 12V to 5V buck converter with LM2596")
        val sim = CircuitSimulator.runSimulation(project)

        assertNotNull(sim)
        assertEquals("CONVERGED", sim.status)
        assertTrue(sim.waveforms.isNotEmpty())
        assertTrue(sim.dcOperatingPoints.containsKey("GND"))
    }

    @Test
    fun `manufacturing package generates valid gerber and drill files`() {
        val project = AiEdaEngine.synthesizeCircuitFromPrompt("Design an ESP32 relay controller")
        val pkg = EdaFileGenerator.generateManufacturingPackage(project)

        assertNotNull(pkg)
        assertTrue(pkg.gerberManifest.isNotEmpty())
        assertTrue(pkg.gerberFilesContent.isNotEmpty())
        assertTrue(pkg.bomCsvContent.contains("Designator"))
        assertTrue(pkg.excellonDrillContent.contains("METRIC"))
        assertTrue(pkg.kicadSchContent.contains("kicad_sch"))
        assertTrue(pkg.kicadPcbContent.contains("kicad_pcb"))
    }
}
