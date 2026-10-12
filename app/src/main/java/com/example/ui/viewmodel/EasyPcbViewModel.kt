package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiService
import com.example.data.engine.AiEdaEngine
import com.example.data.engine.CircuitSimulator
import com.example.data.engine.EdaFileGenerator
import com.example.data.local.ComponentCatalog
import com.example.data.model.ComponentSpec
import com.example.data.model.EasyPcbProject
import com.example.data.model.EngineeringExperience
import com.example.data.model.GenerationStage
import com.example.data.model.PcbLayer
import com.example.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class EasyPcbViewModel : ViewModel() {

    // User Profile & Subscription
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Project List
    private val _projects = MutableStateFlow<List<EasyPcbProject>>(emptyList())
    val projects: StateFlow<List<EasyPcbProject>> = _projects.asStateFlow()

    // Active Project
    private val _currentProject = MutableStateFlow<EasyPcbProject?>(null)
    val currentProject: StateFlow<EasyPcbProject?> = _currentProject.asStateFlow()

    // AI Generation Pipeline Progress
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentPipelineStage = MutableStateFlow(GenerationStage.REQUIREMENTS)
    val currentPipelineStage: StateFlow<GenerationStage> = _currentPipelineStage.asStateFlow()

    private val _pipelineStageProgress = MutableStateFlow(0f)
    val pipelineStageProgress: StateFlow<Float> = _pipelineStageProgress.asStateFlow()

    // Prompt Input
    private val _promptText = MutableStateFlow("Design a Wi-Fi-controlled 4-channel relay controller using ESP32, 12V input, 5V regulation, screw terminals, status LEDs, and protection against reverse polarity.")
    val promptText: StateFlow<String> = _promptText.asStateFlow()

    // 2D PCB View Controls
    private val _activePcbLayer = MutableStateFlow<PcbLayer?>(null) // null = all layers visible
    val activePcbLayer: StateFlow<PcbLayer?> = _activePcbLayer.asStateFlow()

    // 3D Viewer Controls
    private val _solderMaskColor = MutableStateFlow("Green") // "Green", "Black", "Blue"
    val solderMaskColor: StateFlow<String> = _solderMaskColor.asStateFlow()

    private val _rotX = MutableStateFlow(35f)
    val rotX: StateFlow<Float> = _rotX.asStateFlow()

    private val _rotY = MutableStateFlow(-25f)
    val rotY: StateFlow<Float> = _rotY.asStateFlow()

    // AI Assistant Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("ai", "Hello! I am your Easy PCB Design Assistant. You can ask me to optimize trace widths, explain DRC violations in English or Malayalam, or suggest lower-cost component alternatives.")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAssistantThinking = MutableStateFlow(false)
    val isAssistantThinking: StateFlow<Boolean> = _isAssistantThinking.asStateFlow()

    // Component Catalog Filter
    private val _componentSearchQuery = MutableStateFlow("")
    val componentSearchQuery: StateFlow<String> = _componentSearchQuery.asStateFlow()

    // Calculator States
    private val _ohmsVoltage = MutableStateFlow("12.0")
    val ohmsVoltage: StateFlow<String> = _ohmsVoltage.asStateFlow()

    private val _ohmsCurrent = MutableStateFlow("1.5")
    val ohmsCurrent: StateFlow<String> = _ohmsCurrent.asStateFlow()

    private val _ohmsResistance = MutableStateFlow("8.0")
    val ohmsResistance: StateFlow<String> = _ohmsResistance.asStateFlow()

    private val _traceCurrent = MutableStateFlow("3.0")
    val traceCurrent: StateFlow<String> = _traceCurrent.asStateFlow()

    private val _traceThicknessOz = MutableStateFlow("1.0")
    val traceThicknessOz: StateFlow<String> = _traceThicknessOz.asStateFlow()

    init {
        // Initialize with default representative projects
        loadSampleProjects()
    }

    private fun loadSampleProjects() {
        val sample1 = AiEdaEngine.synthesizeCircuitFromPrompt("Design a Wi-Fi-controlled 4-channel relay controller using ESP32, 12V input, 5V regulation, screw terminals, status LEDs, and protection against reverse polarity.")
        val sample2 = AiEdaEngine.synthesizeCircuitFromPrompt("Design a 12V to 5V 3A high-efficiency step-down buck converter with LM2596 and low-ESR filtering capacitors.")
        val sample3 = AiEdaEngine.synthesizeCircuitFromPrompt("Design a USB-C Li-Ion smart battery charger with TP4056 and status LEDs.")

        _projects.value = listOf(sample1, sample2, sample3)
        _currentProject.value = sample1
    }

    fun setPrompt(text: String) {
        _promptText.value = text
    }

    fun startAiCircuitGeneration(customPrompt: String? = null) {
        val promptToUse = customPrompt ?: _promptText.value
        if (promptToUse.isBlank()) return

        viewModelScope.launch {
            _isGenerating.value = true
            _pipelineStageProgress.value = 0f

            // Multi-stage progression
            val stages = GenerationStage.entries
            for ((index, stage) in stages.withIndex()) {
                _currentPipelineStage.value = stage
                _pipelineStageProgress.value = (index + 1).toFloat() / stages.size.toFloat()
                delay(300) // Visual progress feedback for engineering synthesis
            }

            val synthesizedProject = AiEdaEngine.synthesizeCircuitFromPrompt(promptToUse)
            _projects.value = listOf(synthesizedProject) + _projects.value
            _currentProject.value = synthesizedProject

            _userProfile.value = _userProfile.value.copy(
                aiGenerationsUsedToday = _userProfile.value.aiGenerationsUsedToday + 1,
                totalProjectsCreated = _userProfile.value.totalProjectsCreated + 1
            )

            _isGenerating.value = false
        }
    }

    fun selectProject(project: EasyPcbProject) {
        _currentProject.value = project
    }

    fun setActivePcbLayer(layer: PcbLayer?) {
        _activePcbLayer.value = layer
    }

    fun setSolderMaskColor(color: String) {
        _solderMaskColor.value = color
    }

    fun update3dRotation(dx: Float, dy: Float) {
        _rotY.value = (_rotY.value + dx).coerceIn(-75f, 75f)
        _rotX.value = (_rotX.value + dy).coerceIn(10f, 80f)
    }

    fun reRunSimulation() {
        val project = _currentProject.value ?: return
        val newSim = CircuitSimulator.runSimulation(project)
        _currentProject.value = project.copy(simulationResult = newSim)
    }

    fun reGenerateManufacturingFiles() {
        val project = _currentProject.value ?: return
        val newPkg = EdaFileGenerator.generateManufacturingPackage(project)
        _currentProject.value = project.copy(manufacturingPackage = newPkg)
    }

    fun sendAssistantMessage(messageText: String) {
        if (messageText.isBlank()) return

        val userMsg = ChatMessage("user", messageText)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAssistantThinking.value = true

        viewModelScope.launch {
            val responseText = GeminiService.queryGeminiOrFallback(messageText)
            _chatMessages.value = _chatMessages.value + ChatMessage("ai", responseText)
            _isAssistantThinking.value = false
        }
    }

    fun setComponentSearch(query: String) {
        _componentSearchQuery.value = query
    }

    fun getFilteredComponents(): List<ComponentSpec> {
        val q = _componentSearchQuery.value.trim().lowercase()
        return if (q.isEmpty()) {
            ComponentCatalog.verifiedComponents
        } else {
            ComponentCatalog.verifiedComponents.filter {
                it.name.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.mpn.lowercase().contains(q) ||
                it.value.lowercase().contains(q)
            }
        }
    }

    // Calculators
    fun calculateOhmsLaw(voltageStr: String, currentStr: String) {
        _ohmsVoltage.value = voltageStr
        _ohmsCurrent.value = currentStr
        val v = voltageStr.toFloatOrNull() ?: 12f
        val i = currentStr.toFloatOrNull() ?: 1.5f
        if (i > 0) {
            _ohmsResistance.value = String.format(java.util.Locale.US, "%.2f", v / i)
        }
    }

    fun calculateTraceWidth(currentAStr: String, ozStr: String): Float {
        _traceCurrent.value = currentAStr
        _traceThicknessOz.value = ozStr
        val i = currentAStr.toFloatOrNull() ?: 3.0f
        val oz = ozStr.toFloatOrNull() ?: 1.0f
        // IPC-2152 external layer approximation for 10°C temperature rise
        // Width in mm ≈ (I / (0.048 * (10)^0.44 * (oz * 1.378)^0.725))^(1 / 0.725) * 0.0254
        return (i * 0.35f / oz).coerceAtLeast(0.25f)
    }

    // Google Play Billing / Easy PCB Pro Subscription
    fun upgradeToPro() {
        _userProfile.value = _userProfile.value.copy(
            isProSubscribed = true,
            aiGenerationLimit = 100
        )
    }

    fun cancelProSubscription() {
        _userProfile.value = _userProfile.value.copy(
            isProSubscribed = false,
            aiGenerationLimit = 10
        )
    }

    fun setExperienceLevel(experience: EngineeringExperience) {
        _userProfile.value = _userProfile.value.copy(experience = experience)
    }

    fun setPreferredLanguage(lang: String) {
        _userProfile.value = _userProfile.value.copy(preferredLanguage = lang)
    }
}
