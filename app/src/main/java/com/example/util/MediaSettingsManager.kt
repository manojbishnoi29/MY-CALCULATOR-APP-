package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MediaPosition(val label: String) {
    TOP_RIGHT("Top Right"),
    TOP_LEFT("Top Left"),
    BOTTOM_RIGHT("Bottom Right")
}

enum class MediaType(val label: String) {
    AUTO("Auto Detect"),
    PHOTO("Photo / GIF"),
    VIDEO("Video (MP4)")
}

data class RemoteMediaConfig(
    val isEnabled: Boolean = true,
    val mediaUrl: String = "",
    val mediaType: MediaType = MediaType.AUTO,
    val position: MediaPosition = MediaPosition.TOP_RIGHT,
    val isMinimized: Boolean = false,
    val isMuted: Boolean = true,
    val isLooping: Boolean = true,
    val githubRepo: String = "", // e.g. "username/repo"
    val githubFolder: String = "my media", // folder in repo
    val autoSyncGitHub: Boolean = true,
    val lastSyncedFileName: String? = null,
    val lastSyncMessage: String? = null
)

class MediaSettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("remote_media_prefs", Context.MODE_PRIVATE)

    private val gitHubService = GitHubMediaService()

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<RemoteMediaConfig> = _config.asStateFlow()

    private fun loadConfig(): RemoteMediaConfig {
        return RemoteMediaConfig(
            isEnabled = prefs.getBoolean("is_enabled", true),
            mediaUrl = prefs.getString("media_url", "") ?: "",
            mediaType = try {
                MediaType.valueOf(prefs.getString("media_type", MediaType.AUTO.name) ?: MediaType.AUTO.name)
            } catch (_: Exception) {
                MediaType.AUTO
            },
            position = try {
                MediaPosition.valueOf(prefs.getString("position", MediaPosition.TOP_RIGHT.name) ?: MediaPosition.TOP_RIGHT.name)
            } catch (_: Exception) {
                MediaPosition.TOP_RIGHT
            },
            isMinimized = prefs.getBoolean("is_minimized", false),
            isMuted = prefs.getBoolean("is_muted", true),
            isLooping = prefs.getBoolean("is_looping", true),
            githubRepo = prefs.getString("github_repo", "") ?: "",
            githubFolder = prefs.getString("github_folder", "my media") ?: "my media",
            autoSyncGitHub = prefs.getBoolean("auto_sync_github", true),
            lastSyncedFileName = prefs.getString("last_synced_file", null),
            lastSyncMessage = prefs.getString("last_sync_message", null)
        )
    }

    fun updateMediaUrl(url: String) {
        val trimmed = url.trim()
        prefs.edit().putString("media_url", trimmed).apply()
        _config.update { it.copy(mediaUrl = trimmed) }
    }

    fun updateMediaType(type: MediaType) {
        prefs.edit().putString("media_type", type.name).apply()
        _config.update { it.copy(mediaType = type) }
    }

    fun updatePosition(pos: MediaPosition) {
        prefs.edit().putString("position", pos.name).apply()
        _config.update { it.copy(position = pos) }
    }

    fun toggleEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_enabled", enabled).apply()
        _config.update { it.copy(isEnabled = enabled) }
    }

    fun toggleMinimized() {
        val newMin = !_config.value.isMinimized
        prefs.edit().putBoolean("is_minimized", newMin).apply()
        _config.update { it.copy(isMinimized = newMin) }
    }

    fun toggleMuted() {
        val newMuted = !_config.value.isMuted
        prefs.edit().putBoolean("is_muted", newMuted).apply()
        _config.update { it.copy(isMuted = newMuted) }
    }

    fun updateGitHubConfig(repo: String, folder: String, autoSync: Boolean) {
        val trimmedRepo = repo.trim().removePrefix("https://github.com/").trim('/')
        val trimmedFolder = folder.trim().trim('/')
        prefs.edit()
            .putString("github_repo", trimmedRepo)
            .putString("github_folder", trimmedFolder)
            .putBoolean("auto_sync_github", autoSync)
            .apply()
        _config.update {
            it.copy(
                githubRepo = trimmedRepo,
                githubFolder = trimmedFolder,
                autoSyncGitHub = autoSync
            )
        }
    }

    suspend fun syncFromGitHub(): String {
        val current = _config.value
        val parts = current.githubRepo.split("/")
        if (parts.size < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            val msg = "Please enter your GitHub repository in 'username/repository' format."
            updateSyncStatus(null, msg)
            return msg
        }

        val owner = parts[0]
        val repo = parts[1]
        val folder = current.githubFolder

        return when (val result = gitHubService.fetchLatestMediaFromRepo(owner, repo, folder)) {
            is GitHubMediaSyncResult.Success -> {
                prefs.edit()
                    .putString("media_url", result.downloadUrl)
                    .putString("media_type", result.mediaType.name)
                    .putString("last_synced_file", result.fileName)
                    .putString("last_sync_message", "Found ${result.fileName}")
                    .putBoolean("is_enabled", true)
                    .apply()

                _config.update {
                    it.copy(
                        mediaUrl = result.downloadUrl,
                        mediaType = result.mediaType,
                        isEnabled = true,
                        lastSyncedFileName = result.fileName,
                        lastSyncMessage = "Loaded ${result.fileName}"
                    )
                }
                "Successfully synced: ${result.fileName} (${result.mediaType.label})"
            }

            is GitHubMediaSyncResult.Empty -> {
                updateSyncStatus(null, result.message)
                result.message
            }

            is GitHubMediaSyncResult.Error -> {
                updateSyncStatus(null, result.message)
                "Error: ${result.message}"
            }
        }
    }

    private fun updateSyncStatus(fileName: String?, message: String) {
        prefs.edit()
            .putString("last_synced_file", fileName)
            .putString("last_sync_message", message)
            .apply()
        _config.update {
            it.copy(
                lastSyncedFileName = fileName,
                lastSyncMessage = message
            )
        }
    }
}
