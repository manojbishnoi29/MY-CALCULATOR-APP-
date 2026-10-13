package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.theme.BrutalistBorder
import com.example.ui.theme.BrutalistCyan
import com.example.ui.theme.BrutalistGreen
import com.example.ui.theme.BrutalistPink
import com.example.ui.theme.BrutalistYellow
import com.example.util.MediaType
import com.example.util.RemoteMediaConfig

@Composable
fun AutoMediaBanner(
    config: RemoteMediaConfig,
    isOnline: Boolean,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!config.isEnabled) return

    val hasActiveMedia = isOnline && config.mediaUrl.isNotBlank()
    val bannerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("auto_media_banner")
    ) {
        // Hard Black Drop Shadow (Neo-Brutalist base)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(BrutalistBorder, shape = bannerShape)
        )

        // Main Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, BrutalistBorder, bannerShape),
            shape = bannerShape,
            color = Color(0xFFFFFBEB) // Warm Brutalist Cream Paper
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Header bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tag chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (hasActiveMedia) BrutalistGreen else BrutalistYellow,
                        border = androidx.compose.foundation.BorderStroke(1.8.dp, BrutalistBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(BrutalistBorder, CircleShape)
                            )
                            Text(
                                text = if (hasActiveMedia) "AUTO-PLAYING MEDIA" else "MEDIA PLAYER (my media)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = BrutalistBorder,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Media Settings",
                                tint = BrutalistBorder,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Hide Media Banner",
                                tint = BrutalistPink,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Main Media Content / Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111111))
                        .border(2.dp, BrutalistBorder, RoundedCornerShape(12.dp))
                        .clickable { onOpenSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    if (hasActiveMedia) {
                        val isVideo = when (config.mediaType) {
                            MediaType.VIDEO -> true
                            MediaType.PHOTO -> false
                            MediaType.AUTO -> isLikelyVideo(config.mediaUrl)
                        }

                        if (isVideo) {
                            AutoPlayingVideoPlayer(
                                videoUrl = config.mediaUrl,
                                isMuted = config.isMuted,
                                isLooping = config.isLooping
                            )
                        } else {
                            AutoDisplayImage(imageUrl = config.mediaUrl)
                        }
                    } else {
                        // Neo-Brutalist Preview Area
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrutalistYellow,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrutalistBorder),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = BrutalistBorder,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isOnline) "Photos & Videos will auto-play here" else "Connect internet to play media",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Text(
                                text = "Upload to 'my media' on GitHub • Tap to configure",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrutalistCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AutoPlayingVideoPlayer(
    videoUrl: String,
    isMuted: Boolean,
    isLooping: Boolean
) {
    DisposableEffect(videoUrl) {
        onDispose {}
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(Uri.parse(videoUrl))
                setOnPreparedListener { mp ->
                    mp.isLooping = isLooping
                    if (isMuted) {
                        mp.setVolume(0f, 0f)
                    }
                    mp.start()
                }
                setOnCompletionListener {
                    if (isLooping) {
                        it.start()
                    }
                }
                setOnErrorListener { _, _, _ -> true }
                start()
            }
        },
        update = { videoView ->
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }
    )
}

@Composable
private fun AutoDisplayImage(imageUrl: String) {
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Auto Display Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = { isLoading = false }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = BrutalistYellow,
                strokeWidth = 2.5.dp
            )
        }
    }
}

private fun isLikelyVideo(url: String): Boolean {
    val lower = url.lowercase()
    return lower.endsWith(".mp4") ||
            lower.endsWith(".webm") ||
            lower.endsWith(".mkv") ||
            lower.endsWith(".mov") ||
            lower.endsWith(".3gp") ||
            lower.contains("/video/") ||
            lower.contains("googlevideo.com")
}
