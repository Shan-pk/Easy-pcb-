package com.example.data.model

enum class EngineeringExperience(val label: String, val description: String) {
    BEGINNER("Beginner / Student", "Guided explanations, helpful tooltips, and simplified parameter wizards"),
    MAKER("Maker / Hobbyist", "Balanced workflow with practical module selection and rapid prototyping"),
    PROFESSIONAL("Professional Engineer", "Full engineering parameters, KiCad/Gerber exports, DRC tolerances, and SPICE")
}

data class UserProfile(
    val email: String = "engineer@easypcb.com",
    val displayName: String = "PCB Designer",
    val country: String = "United States",
    val preferredLanguage: String = "en", // "en" or "ml" (Malayalam)
    val experience: EngineeringExperience = EngineeringExperience.MAKER,
    val isProSubscribed: Boolean = false,
    val proSubscriptionPriceMonthly: String = "$3.00/month",
    val aiGenerationsUsedToday: Int = 1,
    val aiGenerationLimit: Int = 10,
    val totalProjectsCreated: Int = 3
)
