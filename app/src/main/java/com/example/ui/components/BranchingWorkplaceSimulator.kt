package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.ContextTrack
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent

enum class BranchQuality(val label: String, val score: Int) {
    INFORMAL_WEAK("Umgangssprachlich", 45),
    DEFENSIVE("Defensiv / Ausweichend", 60),
    EXECUTIVE_C1("C1 Exekutiv (Optimal)", 100)
}

data class RoleplayOption(
    val id: String,
    val textDe: String,
    val quality: BranchQuality,
    val counterpartReactionDe: String,
    val technicalRationaleDe: String
)

data class RoleplayScenarioSpec(
    val id: String,
    val contextTrack: ContextTrack,
    val institutionBadge: String,
    val interlocutorName: String,
    val interlocutorRole: String,
    val icon: ImageVector,
    val initialPromptDe: String,
    val options: List<RoleplayOption>
)

val SampleWorkplaceScenarios = listOf(
    RoleplayScenarioSpec(
        id = "mb_sindelfingen_inverter",
        contextTrack = ContextTrack.ENGINEERING,
        institutionBadge = "Mercedes-Benz R&D · Sindelfingen",
        interlocutorName = "Dr. Markus Weber",
        interlocutorRole = "Leiter E-Antrieb & Leistungselektronik",
        icon = Icons.Rounded.Engineering,
        initialPromptDe = "„Auf dem Rollenprüfstand erreicht der SiC-Inverter unter Volllast kritische 92 Grad. Wie begründen Sie gegenüber dem Vorstand die temporäre Drosselung der Spitzenleistung?“",
        options = listOf(
            RoleplayOption(
                id = "mb_opt_1",
                textDe = "„Wir müssen halt langsamer machen, weil der Inverter sonst zu heiß wird und kaputtgeht.“",
                quality = BranchQuality.INFORMAL_WEAK,
                counterpartReactionDe = "„Das ist viel zu umgangssprachlich formuliert! Vor dem Vorstand brauchen wir fundierte C1-Kausalität im Nominalstil.“",
                technicalRationaleDe = "Verwende Kausalangaben wie 'infolge thermischer Belastungsspitzen' statt 'weil es zu heiß wird'."
            ),
            RoleplayOption(
                id = "mb_opt_2",
                textDe = "„Die thermischen Grenzwerte der Halbleiter wurden vom Zulieferer zu optimistisch spezifiziert, dafür haftet die Entwicklung nicht.“",
                quality = BranchQuality.DEFENSIVE,
                counterpartReactionDe = "„Keine Schuldzuweisungen im Lenkungskreis. Bitte formulieren Sie die konkrete Ingenieursmaßnahme sachlich.“",
                technicalRationaleDe = "Vermeide defensive Vorwürfe; fokussiere auf die Systemsicherheit und Validierungsschritte."
            ),
            RoleplayOption(
                id = "mb_opt_3",
                textDe = "„Infolge thermischer Belastungsspitzen am Wechselrichter empfiehlt das Team eine temporäre Spitzenleistungsbegrenzung zwecks Gewährleistung der Systemintegrität.“",
                quality = BranchQuality.EXECUTIVE_C1,
                counterpartReactionDe = "„Hervorragend präzisiert! Genau so präsentieren wir das im Vorstand: sachlich, präzise und lösungsorientiert.“",
                technicalRationaleDe = "Exzellenter C1-Nominalstil ('infolge Belastungsspitzen', 'zwecks Gewährleistung') mit maximaler Fachschärfe."
            )
        )
    ),
    RoleplayScenarioSpec(
        id = "porsche_weissach_aero",
        contextTrack = ContextTrack.ENGINEERING,
        institutionBadge = "Porsche Motorsport · Weissach",
        interlocutorName = "Jörg Bergmeister",
        interlocutorRole = "Chef-Entwicklungsfahrer GT-Modelle",
        icon = Icons.Rounded.DirectionsCar,
        initialPromptDe = "„Im Schwedenkreuz bei 245 km/h wird das Heck des 911 GT3 unruhig. Wie stimmen wir die Aerodynamik für den nächsten Stint ab?“",
        options = listOf(
            RoleplayOption(
                id = "porsche_opt_1",
                textDe = "„Vielleicht stellen wir einfach den Flügel ein Stückchen steiler und gucken mal, was passiert?“",
                quality = BranchQuality.INFORMAL_WEAK,
                counterpartReactionDe = "„'Gucken mal' reicht bei 245 km/h nicht. Ich brauche klare aerodynamische Parameter.“",
                technicalRationaleDe = "Modalpartikeln und vage Verben schwächen die Aussage; nutze Konjunktiv II mit klaren Parametern."
            ),
            RoleplayOption(
                id = "porsche_opt_2",
                textDe = "„Zur Stabilisierung des Fahrverhaltens bei Hochgeschwindigkeit böte sich eine stufenweise Erhöhung des Heckflügel-Anstellwinkels um 2 Grad an, um die aerodynamische Balance nach hinten zu verlagern.“",
                quality = BranchQuality.EXECUTIVE_C1,
                counterpartReactionDe = "„Exakt auf den Punkt. 2 Grad bringen 18 kg mehr Anpressdruck ohne spürbaren Topspeed-Verlust. Machen wir so!“",
                technicalRationaleDe = "Perfekter Konjunktiv II ('böte sich an') und präzise Fachbegriffe ('aerodynamische Balance', 'Anstellwinkel')."
            ),
            RoleplayOption(
                id = "porsche_opt_3",
                textDe = "„Der Fahrer lenkt zu abrupt ein; das Fahrwerk ist von uns optimal kalibriert worden.“",
                quality = BranchQuality.DEFENSIVE,
                counterpartReactionDe = "„Telemetrie zeigt sauberen Lenkwinkel. Bleiben wir bei aerodynamischen Anpassungen.“",
                technicalRationaleDe = "Kritik am Piloten statt konstruktiver Setupschritte schadet der Zusammenarbeit im Rennstall."
            )
        )
    ),
    RoleplayScenarioSpec(
        id = "bvb_press_conference",
        contextTrack = ContextTrack.COACHING,
        institutionBadge = "BVB 09 · Signal Iduna Park Medienraum",
        interlocutorName = "Kicker-Chefreporter",
        interlocutorRole = "Akkreditierter Sportjournalist",
        icon = Icons.Rounded.SportsSoccer,
        initialPromptDe = "„Herr Co-Trainer, in der 68. Minute bei 1:0-Führung haben Sie auf eine defensive Fünferkette umgestellt. Warum dieser Sicherheitsfußball?“",
        options = listOf(
            RoleplayOption(
                id = "bvb_opt_1",
                textDe = "„Wir wollten halt hinten dichtmachen, damit nichts mehr anbrennt.“",
                quality = BranchQuality.INFORMAL_WEAK,
                counterpartReactionDe = "„Klingt nach Verlegenheitslösung. Gab es dafür einen taktischen Matchplan?“",
                technicalRationaleDe = "'Dichtmachen' ist Stammtisch-Slang; im Profifußball spricht man von 'Kettenstaffelung' und 'Halbraumverdichtung'."
            ),
            RoleplayOption(
                id = "bvb_opt_2",
                textDe = "„Zwecks Verdichtung der Halbräume und Unterbindung gegnerischer Schnittstellenpässe erschien die Umstellung auf eine gestaffelte Fünferkette taktisch geboten.“",
                quality = BranchQuality.EXECUTIVE_C1,
                counterpartReactionDe = "„Präzise Taktikanalyse. Damit haben Sie die Angriffswellen des Gegners neutralisiert. Danke.“",
                technicalRationaleDe = "Erstklassige Fachterminologie ('zwecks Verdichtung', 'Schnittstellenpässe', 'taktisch geboten')."
            ),
            RoleplayOption(
                id = "bvb_opt_3",
                textDe = "„Die Frage ist unverschämt, ein 1:0 sind drei Punkte, mehr interessiert uns nicht.“",
                quality = BranchQuality.DEFENSIVE,
                counterpartReactionDe = "„Auf Pressekonferenzen erwarten wir sachliche Auskünfte über taktische Wechsel.“",
                technicalRationaleDe = "Aggressive Abwehrhaltung wirkt unprofessionell; sachliche Souveränität ist C1-Standard."
            )
        )
    )
)

/**
 * Interactive Workplace Branching Roleplay Simulator:
 * Provides multi-turn simulations with dynamic counterpart reactions,
 * C1 register evaluation, and authentic situational decision-making.
 */
@Composable
fun BranchingWorkplaceSimulator(
    scenario: RoleplayScenarioSpec,
    onSpeakPhrase: (String) -> Unit,
    onSimulationDone: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    var selectedOptionId by remember(scenario.id) { mutableStateOf<String?>(null) }
    val chosenOption = scenario.options.find { it.id == selectedOptionId }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("branching_workplace_simulator"),
        shape = RoundedCornerShape(22.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Institution badge + Interlocutor Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = palette.primaryAccent.copy(alpha = 0.20f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(scenario.icon, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                    Column {
                        Text(
                            text = scenario.institutionBadge.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.primaryAccent
                        )
                        Text(
                            text = "${scenario.interlocutorName} · ${scenario.interlocutorRole}",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.imprintStageSubtext
                        )
                    }
                }
            }

            // Scenario Question / Superior's Challenge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.imprintStageSurface,
                border = BorderStroke(1.dp, palette.borderStroke.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = scenario.initialPromptDe,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = palette.imprintStageText
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onSpeakPhrase(scenario.initialPromptDe) },
                        shape = CircleShape,
                        color = palette.secondaryAccent.copy(alpha = 0.16f)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Vorlesen",
                            tint = palette.secondaryAccent,
                            modifier = Modifier.padding(8.dp).size(18.dp)
                        )
                    }
                }
            }

            // Branching Response Options (Learner's Choice)
            Text(
                text = "WÄHLE DEINE C1-REAKTION IM DIALOG:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = palette.imprintStageSubtext
            )

            scenario.options.forEach { opt ->
                val isSelected = selectedOptionId == opt.id
                val isOptimal = opt.quality == BranchQuality.EXECUTIVE_C1
                val col = when {
                    isSelected && isOptimal -> palette.tertiaryAccent
                    isSelected -> palette.warningRed
                    else -> palette.borderStroke
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedOptionId = opt.id }
                        .testTag("roleplay_opt_${opt.id}"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) col.copy(alpha = 0.14f) else palette.imprintStageSurface,
                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, col)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = opt.textDe,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = palette.imprintStageText
                        )
                    }
                }
            }

            // Dynamic Reaction & Executive Rationale once chosen
            chosenOption?.let { opt ->
                val isSuccess = opt.quality == BranchQuality.EXECUTIVE_C1
                val badgeCol = if (isSuccess) palette.tertiaryAccent else palette.warningRed

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = badgeCol.copy(alpha = 0.12f),
                    border = BorderStroke(1.2.dp, badgeCol)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REAKTION DES GESPRÄCHSPARTNERS (${opt.quality.label}):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = badgeCol
                            )
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = badgeCol
                            ) {
                                Text(
                                    text = "${opt.quality.score}/100",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = contentColorOnAccent(badgeCol),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = opt.counterpartReactionDe,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = palette.imprintStageText
                        )

                        Text(
                            text = "💡 C1-Tipp: ${opt.technicalRationaleDe}",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.imprintStageSubtext
                        )
                    }
                }

                Button(
                    onClick = { onSimulationDone(isSuccess) },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("conclude_roleplay_btn")
                ) {
                    val fg = contentColorOnAccent(palette.primaryAccent)
                    Text(
                        text = if (isSuccess) "Erfolgreich abgeschlossen →" else "Verstanden & Weiter →",
                        color = fg,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
