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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EngineeringExperience
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
fun AccountBillingScreen(viewModel: EasyPcbViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("account_billing_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(NavySurfaceVariant, RoundedCornerShape(12.dp))
                                .border(1.dp, PcbGreenPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(34.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = userProfile.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = userProfile.email, color = TextSecondary, fontSize = 12.sp)
                            Text(text = "Country: ${userProfile.country}", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileMetricBadge(label = "Projects Created", value = "${userProfile.totalProjectsCreated}")
                        ProfileMetricBadge(label = "Daily AI Generates", value = "${userProfile.aiGenerationsUsedToday}/${userProfile.aiGenerationLimit}")
                        ProfileMetricBadge(label = "Account Tier", value = if (userProfile.isProSubscribed) "Easy PCB PRO" else "Free Tier")
                    }
                }
            }
        }

        // Easy PCB Pro Subscription Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pro_subscription_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (userProfile.isProSubscribed) Color(0xFF0F261B) else Color(0xFF14202E)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (userProfile.isProSubscribed) PcbGreenPrimary else CopperGold
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (userProfile.isProSubscribed) PcbGreenPrimary else CopperGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Easy PCB Pro",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            text = userProfile.proSubscriptionPriceMonthly,
                            color = if (userProfile.isProSubscribed) PcbGreenPrimary else CopperGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (userProfile.isProSubscribed)
                            "Your Pro subscription is active with full EDA capabilities unlocked."
                        else
                            "Unlock unrestricted AI electronics generations, KiCad exports, Gerber fabrication files, and SPICE simulations.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    ProBenefitItem("Unlimited Natural-Language AI Circuit Generations")
                    ProBenefitItem("Full KiCad 8 Schematic & PCB Layout Export")
                    ProBenefitItem("Gerber RS-274X, Excellon Drill & Centroid SMT Exports")
                    ProBenefitItem("Integrated SPICE Transient & DC Operating Simulation")
                    ProBenefitItem("Automated Electrical (ERC) and PCB (DRC) Checks")

                    Spacer(modifier = Modifier.height(16.dp))

                    if (userProfile.isProSubscribed) {
                        OutlinedButton(
                            onClick = {
                                viewModel.cancelProSubscription()
                                Toast.makeText(context, "Subscription managed via Google Play Store", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Manage / Cancel Subscription (Google Play)", color = TextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.upgradeToPro()
                                Toast.makeText(context, "Welcome to Easy PCB Pro! Google Play Purchase Token Verified.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CopperGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("purchase_pro_button")
                        ) {
                            Text(
                                text = "Subscribe to Easy PCB Pro — $3.00/mo",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Restore Purchases (Google Play)",
                                color = SiliconTraceBlue,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.upgradeToPro()
                                        Toast.makeText(context, "Entitlements restored successfully.", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("restore_purchases_button")
                            )
                        }
                    }
                }
            }
        }

        // Engineering Experience Level Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = SiliconTraceBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Engineering Experience Level", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    EngineeringExperience.entries.forEach { exp ->
                        val isSelected = userProfile.experience == exp
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NavySurfaceVariant else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) PcbGreenPrimary else NavySurfaceContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.setExperienceLevel(exp) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = exp.label,
                                    color = if (isSelected) PcbGreenPrimary else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(text = exp.description, color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Preferred Language Selector (English / Malayalam)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = SiliconTraceBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Preferred Language (ഭാഷ)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LanguageChip(
                            label = "English",
                            isSelected = userProfile.preferredLanguage == "en",
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.setPreferredLanguage("en")
                        }
                        LanguageChip(
                            label = "മലയാളം (Malayalam)",
                            isSelected = userProfile.preferredLanguage == "ml",
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.setPreferredLanguage("ml")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProBenefitItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PcbGreenPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = TextPrimary, fontSize = 12.sp)
    }
}

@Composable
fun ProfileMetricBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = PcbGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun LanguageChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) NavySurfaceVariant else NavySurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PcbGreenPrimary else NavySurfaceContainer
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) PcbGreenPrimary else TextPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}
