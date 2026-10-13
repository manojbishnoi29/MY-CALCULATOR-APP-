package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.Locale
import java.util.concurrent.TimeUnit

sealed class GitHubMediaSyncResult {
    data class Success(
        val fileName: String,
        val downloadUrl: String,
        val mediaType: MediaType
    ) : GitHubMediaSyncResult()

    data class Empty(val message: String) : GitHubMediaSyncResult()
    data class Error(val message: String) : GitHubMediaSyncResult()
}

class GitHubMediaService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun fetchLatestMediaFromRepo(
        owner: String,
        repo: String,
        folderPath: String = "my media"
    ): GitHubMediaSyncResult = withContext(Dispatchers.IO) {
        val cleanOwner = owner.trim().removePrefix("https://github.com/").trim('/')
        val cleanRepo = repo.trim().trim('/')
        val cleanFolder = folderPath.trim().trim('/')

        if (cleanOwner.isBlank() || cleanRepo.isBlank()) {
            return@withContext GitHubMediaSyncResult.Error("Please enter valid GitHub username and repository name.")
        }

        val encodedFolder = cleanFolder.split("/").joinToString("/") { segment ->
            java.net.URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
        }

        val apiUrl = if (cleanFolder.isEmpty()) {
            "https://api.github.com/repos/$cleanOwner/$cleanRepo/contents"
        } else {
            "https://api.github.com/repos/$cleanOwner/$cleanRepo/contents/$encodedFolder"
        }

        try {
            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "Android-Calculator-App")
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext if (response.code == 404) {
                    GitHubMediaSyncResult.Empty("Folder '$cleanFolder' not found in $cleanOwner/$cleanRepo.")
                } else {
                    GitHubMediaSyncResult.Error("GitHub API responded with code: ${response.code}")
                }
            }

            val responseBody = response.body?.string()
                ?: return@withContext GitHubMediaSyncResult.Empty("Empty response from GitHub.")

            val jsonArray = JSONArray(responseBody)
            if (jsonArray.length() == 0) {
                return@withContext GitHubMediaSyncResult.Empty("No files found in folder '$cleanFolder'.")
            }

            // Find first media file (photos or videos)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val type = item.optString("type", "")
                if (type != "file") continue

                val fileName = item.optString("name", "")
                val rawDownloadUrl = item.optString("download_url", "")

                if (rawDownloadUrl.isBlank() || fileName.isBlank()) continue

                val detectedType = detectMediaType(fileName)
                if (detectedType != null) {
                    // Append cache-buster timestamp query so newly uploaded files reload immediately
                    val freshUrl = if (rawDownloadUrl.contains("?")) {
                        "$rawDownloadUrl&cb=${System.currentTimeMillis()}"
                    } else {
                        "$rawDownloadUrl?cb=${System.currentTimeMillis()}"
                    }

                    return@withContext GitHubMediaSyncResult.Success(
                        fileName = fileName,
                        downloadUrl = freshUrl,
                        mediaType = detectedType
                    )
                }
            }

            GitHubMediaSyncResult.Empty("No supported photo or video files found in '$cleanFolder'. (Supported: .mp4, .webm, .jpg, .png, .gif, .webp)")
        } catch (e: Exception) {
            GitHubMediaSyncResult.Error(e.message ?: "Network error connecting to GitHub.")
        }
    }

    private fun detectMediaType(fileName: String): MediaType? {
        val lower = fileName.lowercase(Locale.ROOT)
        return when {
            lower.endsWith(".mp4") ||
                    lower.endsWith(".webm") ||
                    lower.endsWith(".mkv") ||
                    lower.endsWith(".mov") ||
                    lower.endsWith(".3gp") -> MediaType.VIDEO

            lower.endsWith(".jpg") ||
                    lower.endsWith(".jpeg") ||
                    lower.endsWith(".png") ||
                    lower.endsWith(".gif") ||
                    lower.endsWith(".webp") ||
                    lower.endsWith(".bmp") -> MediaType.PHOTO

            else -> null
        }
    }
}
