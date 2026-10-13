package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRose
import com.example.ui.theme.SecondaryText
import com.example.util.MediaPosition
import com.example.util.MediaSettingsManager
import com.example.util.MediaType
import com.example.util.RemoteMediaConfig
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSettingsDialog(
    config: RemoteMediaConfig,
    isOnline: Boolean,
    manager: MediaSettingsManager,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(if (config.githubRepo.isNotEmpty()) 0 else 0) }

    // GitHub Sync state
    var repoInput by remember { mutableStateOf(config.githubRepo) }
    var folderInput by remember { mutableStateOf(config.githubFolder) }
    var autoSyncGitHub by remember { mutableStateOf(config.autoSyncGitHub) }
    var isSyncing by remember { mutableStateOf(false) }
    var syncStatusText by remember { mutableStateOf(config.lastSyncMessage ?: "") }

    // Direct URL state
    var urlInput by remember { mutableStateOf(config.mediaUrl) }
    var selectedType by remember { mutableStateOf(config.mediaType) }
    var selectedPosition by remember { mutableStateOf(config.position) }
    var isEnabled by remember { mutableStateOf(config.isEnabled) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("media_settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = AccentCyan
                    )
                    Text(
                        text = "Corner Media Player",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Internet Status Chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOnline) Color(0xFF166534) else Color(0xFF991B1B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Filled.Cloud else Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isOnline) "ONLINE" else "OFFLINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Tab Selector: GitHub Repo Sync vs Direct URL
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = AccentCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AccentCyan
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("GitHub Live Sync", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Custom URL", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                // ==========================================
                // GITHUB SYNC TAB
                // ==========================================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💡 How GitHub Live Media Works",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Text(
                            text = "1. In your GitHub repository root, create a folder named 'my media'.\n" +
                                    "2. Upload any photo (.jpg, .png, .gif) or video (.mp4, .webm) into that folder.\n" +
                                    "3. Whenever you delete that file and upload a new one, the app detects it automatically when online!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "GitHub Repository (username/repository)",
                        style = MaterialTheme.typography.labelLarge
                    )
                    OutlinedTextField(
                        value = repoInput,
                        onValueChange = { repoInput = it },
                        placeholder = { Text("e.g. your-username/your-repo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Folder inside repository",
                        style = MaterialTheme.typography.labelLarge
                    )
                    OutlinedTextField(
                        value = folderInput,
                        onValueChange = { folderInput = it },
                        placeholder = { Text("my media") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Folder, contentDescription = null, tint = AccentCyan)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                // Sync Now Button
                Button(
                    onClick = {
                        manager.updateGitHubConfig(repoInput, folderInput, autoSyncGitHub)
                        coroutineScope.launch {
                            isSyncing = true
                            val resultMsg = manager.syncFromGitHub()
                            isSyncing = false
                            syncStatusText = resultMsg
                            Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = !isSyncing && isOnline,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF0F172A))
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Checking GitHub...")
                    } else {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Sync Now from GitHub", fontWeight = FontWeight.Bold)
                    }
                }

                if (syncStatusText.isNotEmpty()) {
                    Text(
                        text = syncStatusText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (syncStatusText.startsWith("Error") || syncStatusText.startsWith("Folder")) AccentRose else AccentCyan
                    )
                }

                if (config.lastSyncedFileName != null) {
                    Text(
                        text = "Active file: ${config.lastSyncedFileName} (${config.mediaType.label})",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                }
            } else {
                // ==========================================
                // CUSTOM URL TAB
                // ==========================================
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Direct Photo or Video URL",
                        style = MaterialTheme.typography.labelLarge
                    )

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        placeholder = { Text("https://example.com/media.mp4 or .jpg") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Link, contentDescription = null, tint = AccentCyan)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("media_url_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                // Media Type Picker
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Media Type", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MediaType.values().forEach { type ->
                            val isSel = selectedType == type
                            Surface(
                                onClick = { selectedType = type },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) AccentCyan else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = type.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Common Controls: Enable switch & Corner position
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable Corner Media", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentCyan,
                        checkedTrackColor = AccentCyan.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("enable_media_switch")
                )
            }

            // Corner Position Picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Display Corner Position", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MediaPosition.values().forEach { pos ->
                        val isSel = selectedPosition == pos
                        Surface(
                            onClick = { selectedPosition = pos },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) AccentCyan else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = pos.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Save & Apply Button
            Button(
                onClick = {
                    manager.toggleEnabled(isEnabled)
                    if (selectedTab == 0) {
                        manager.updateGitHubConfig(repoInput, folderInput, autoSyncGitHub)
                        coroutineScope.launch {
                            manager.syncFromGitHub()
                        }
                    } else {
                        manager.updateMediaUrl(urlInput)
                        manager.updateMediaType(selectedType)
                    }
                    manager.updatePosition(selectedPosition)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_media_settings_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF0F172A))
            ) {
                Text("Save & Apply", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
