package com.hevsosapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hevsosapp.ui.theme.EmergencyRed
import com.hevsosapp.ui.theme.EmergencyRedDark
import com.hevsosapp.ui.theme.EmergencyRedLight
import com.hevsosapp.ui.theme.HevSosAppTheme
import com.hevsosapp.ui.theme.StatusGreen
import com.hevsosapp.ui.theme.StatusGreenLight

@Composable
fun MainSOSScreen(
    userName: String = "Felhasználó",
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSOSActivated: () -> Unit,
    onCallEmergency: () -> Unit,
    onSendLocation: () -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sosActive by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val sosPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_scale"
    )

    val sosButtonColor by animateColorAsState(
        targetValue = if (sosActive) EmergencyRedDark else EmergencyRed,
        animationSpec = tween(300),
        label = "sos_color"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Top bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        // GPS status indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreenLight)
                            )
                            Text(
                                text = "GPS aktív",
                                style = MaterialTheme.typography.labelMedium,
                                color = StatusGreen
                            )
                        }
                    }
                }

                // Action icons
                Row {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Beállítások",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // ── Central SOS button ────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (sosActive) {
                    Text(
                        text = "SOS AKTÍV – SEGÍTSÉG ÚTBAN",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmergencyRedLight,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Nyomja meg vészhelyzet esetén",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Outer pulse ring
                Box(
                    modifier = Modifier
                        .size(if (sosActive) (220 * sosPulse).dp else 220.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = if (sosActive) EmergencyRedLight.copy(alpha = 0.5f) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // SOS Button
                    Button(
                        onClick = {
                            sosActive = !sosActive
                            if (sosActive) onSOSActivated()
                        },
                        modifier = Modifier.size(200.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = sosButtonColor,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "SOS",
                                modifier = Modifier.size(56.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SOS",
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                if (sosActive) {
                    Button(
                        onClick = { sosActive = false },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "Mégse",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // ── Secondary action buttons ──────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmergencyActionCard(
                        icon = Icons.Default.Call,
                        label = "112 Hívás",
                        color = EmergencyRed,
                        onClick = onCallEmergency,
                        modifier = Modifier.weight(1f)
                    )
                    EmergencyActionCard(
                        icon = Icons.Default.LocationOn,
                        label = "Helyszín",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onSendLocation,
                        modifier = Modifier.weight(1f)
                    )
                    EmergencyActionCard(
                        icon = Icons.Default.Message,
                        label = "Üzenet",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onSendMessage,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Status bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusItem(label = "GPS", active = true)
                        StatusDivider()
                        StatusItem(label = "Hálózat", active = true)
                        StatusDivider()
                        StatusItem(label = "SOS", active = sosActive)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyActionCard(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatusItem(label: String, active: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (active) StatusGreenLight else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusDivider() {
    Box(
        modifier = Modifier
            .height(16.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainSOSScreenPreview() {
    HevSosAppTheme {
        MainSOSScreen(
            userName = "Kovács János",
            onProfileClick = {},
            onSettingsClick = {},
            onSOSActivated = {},
            onCallEmergency = {},
            onSendLocation = {},
            onSendMessage = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark Mode")
@Composable
fun MainSOSScreenDarkPreview() {
    HevSosAppTheme(darkTheme = true) {
        MainSOSScreen(
            userName = "Kovács János",
            onProfileClick = {},
            onSettingsClick = {},
            onSOSActivated = {},
            onCallEmergency = {},
            onSendLocation = {},
            onSendMessage = {}
        )
    }
}
