package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRose
import com.example.util.MediaPosition
import com.example.util.MediaType
import com.example.util.RemoteMediaConfig

@Composable
fun CornerMediaWidget(
    config: RemoteMediaConfig,
    isOnline: Boolean,
    onOpenSettings: () -> Unit,
    onToggleMinimize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!config.isEnabled) return

    val alignment = when (config.position) {
        MediaPosition.TOP_RIGHT -> Alignment.TopEnd
        MediaPosition.TOP_LEFT -> Alignment.TopStart
        MediaPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
    }

    // If no media is linked yet, show a sleek indicator badge showing where media will play
    if (config.mediaUrl.isBlank()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 12.dp, end = 12.dp, start = 12.dp),
            contentAlignment = alignment
        ) {
            Surface(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E222D).copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentCyan.copy(alpha = 0.8f)),
                shadowElevation = 6.dp,
                modifier = Modifier.testTag("media_placeholder_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(AccentCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Media Plays Here",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Text(
                            text = "Tap to sync GitHub",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
        return
    }

    // When media is configured, only display if online
    if (!isOnline) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = alignment
    ) {
        if (config.isMinimized) {
            // Minimized Floating Pill
            Surface(
                onClick = onToggleMinimize,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(46.dp)
                    .border(2.dp, AccentCyan, CircleShape)
                    .testTag("media_widget_minimized")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Expand Corner Media",
                        tint = AccentCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        } else {
            // Expanded Floating Media Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF14171F),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .width(170.dp)
                    .border(1.5.dp, AccentCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .testTag("media_widget_card")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Bar with status & action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E222D))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Live green pulse badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22C55E),
                                fontSize = 10.sp
                            )
                        }

                        // Action Controls
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Media Settings",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            IconButton(
                                onClick = onToggleMinimize,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Minimize Media",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Hide Media",
                                    tint = AccentRose,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Media Content View: Photo or Video
                    val isVideo = when (config.mediaType) {
                        MediaType.VIDEO -> true
                        MediaType.PHOTO -> false
                        MediaType.AUTO -> isLikelyVideoUrl(config.mediaUrl)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 10f)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isVideo) {
                            VideoPlayerView(
                                videoUrl = config.mediaUrl,
                                isMuted = config.isMuted,
                                isLooping = config.isLooping
                            )
                        } else {
                            ImageViewer(imageUrl = config.mediaUrl)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageViewer(imageUrl: String) {
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Remote Corner Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = { isLoading = false }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AccentCyan,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun VideoPlayerView(
    videoUrl: String,
    isMuted: Boolean,
    isLooping: Boolean
) {
    val context = LocalContext.current
    var mediaPlayerInstance: MediaPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(videoUrl) {
        onDispose {
            mediaPlayerInstance?.release()
            mediaPlayerInstance = null
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(Uri.parse(videoUrl))
                setOnPreparedListener { mp ->
                    mediaPlayerInstance = mp
                    mp.isLooping = isLooping
                    if (isMuted) {
                        mp.setVolume(0f, 0f)
                    }
                    start()
                }
                setOnCompletionListener {
                    if (isLooping) {
                        start()
                    }
                }
                setOnErrorListener { _, _, _ ->
                    // Prevent error dialog on bad url
                    true
                }
            }
        },
        update = { view ->
            if (!view.isPlaying) {
                view.start()
            }
        }
    )
}

private fun isLikelyVideoUrl(url: String): Boolean {
    val lower = url.lowercase()
    return lower.endsWith(".mp4") ||
            lower.endsWith(".webm") ||
            lower.endsWith(".mkv") ||
            lower.endsWith(".3gp") ||
            lower.contains("/video/") ||
            lower.contains("googlevideo.com")
}
