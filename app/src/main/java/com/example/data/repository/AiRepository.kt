package com.example.data.repository

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.AspectRatio
import com.google.firebase.ai.type.ImageConfig
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ImageSize
import com.google.firebase.ai.type.LatLng
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.RetrievalConfig
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.ToolConfig
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.withTimeout

data class GroundedWebSource(
    val title: String,
    val uri: String
)

data class GroundedPlaceSource(
    val title: String,
    val uri: String,
    val placeId: String?
)

data class GroundedResponse(
    val text: String,
    val webSources: List<GroundedWebSource> = emptyList(),
    val placeSources: List<GroundedPlaceSource> = emptyList(),
    val searchWidgetHtml: String? = null
)

data class ThinkingResponse(
    val answer: String,
    val thoughtSummary: String? = null,
    val thoughtsTokenCount: Int? = null
)

class AiRepository {
    // 1. Google Search Grounding with gemini-3.8-flash
    suspend fun queryWithSearchGrounding(prompt: String): GroundedResponse = withTimeout(60_000L) {
        val model = Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            tools = listOf(Tool.googleSearch())
        )
        val response = model.generateContent(prompt)
        val text = response.text.orEmpty()
        val candidate = response.candidates.firstOrNull()
        val groundingMetadata = candidate?.groundingMetadata

        val webSources = mutableListOf<GroundedWebSource>()
        groundingMetadata?.groundingChunks?.forEach { chunk ->
            chunk.web?.let { web ->
                val uriStr = web.uri ?: ""
                val titleStr = web.title ?: if (uriStr.isNotBlank()) uriStr else "Web Source"
                webSources.add(
                    GroundedWebSource(
                        title = titleStr,
                        uri = uriStr
                    )
                )
            }
        }
        val widgetHtml = groundingMetadata?.searchEntryPoint?.renderedContent
        GroundedResponse(
            text = text,
            webSources = webSources,
            searchWidgetHtml = widgetHtml
        )
    }

    // 2. Google Maps Grounding with gemini-3.8-flash
    suspend fun queryWithMapsGrounding(
        prompt: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): GroundedResponse = withTimeout(60_000L) {
        val toolConfig = if (latitude != null && longitude != null) {
            val retrieval = RetrievalConfig.builder()
                .setLatLng(LatLng(latitude = latitude, longitude = longitude))
                .setLanguageCode("th")
                .build()
            ToolConfig(null, retrieval)
        } else {
            null
        }

        val model = Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            tools = listOf(Tool.googleMaps()),
            toolConfig = toolConfig
        )

        val response = model.generateContent(prompt)
        val text = response.text.orEmpty()
        val candidate = response.candidates.firstOrNull()
        val groundingMetadata = candidate?.groundingMetadata

        val placeSources = mutableListOf<GroundedPlaceSource>()
        groundingMetadata?.groundingChunks?.forEach { chunk ->
            chunk.maps?.let { maps ->
                val uriStr = maps.uri ?: ""
                val titleStr = maps.title ?: if (uriStr.isNotBlank()) uriStr else "Google Maps Location"
                placeSources.add(
                    GroundedPlaceSource(
                        title = titleStr,
                        uri = uriStr,
                        placeId = maps.placeId
                    )
                )
            }
        }

        GroundedResponse(
            text = text,
            placeSources = placeSources
        )
    }

    // 3. Deep Thinking Mode with gemini-4-argon (ThinkingLevel.HIGH, no maxOutputTokens)
    suspend fun queryWithHighThinking(prompt: String): ThinkingResponse = withTimeout(60_000L) {
        val config = generationConfig {
            thinkingConfig = thinkingConfig {
                thinkingLevel = ThinkingLevel.HIGH
            }
        }

        val model = Firebase.ai.generativeModel(
            modelName = "gemini-4-argon",
            generationConfig = config
        )

        val response = model.generateContent(prompt)
        val answer = response.text.orEmpty()
        val candidate = response.candidates.firstOrNull()
        val thoughtSummary = candidate?.finishMessage
        val tokens = response.usageMetadata?.thoughtsTokenCount

        ThinkingResponse(
            answer = answer,
            thoughtSummary = thoughtSummary,
            thoughtsTokenCount = tokens
        )
    }

    // 4. Image Generation with gemini-3-pro-image-preview & ImageSize affordance (1K, 2K, 4K)
    suspend fun generateImage(
        prompt: String,
        size: ImageSize = ImageSize.SIZE_1K,
        aspectRatio: AspectRatio = AspectRatio.SQUARE_1x1
    ): Bitmap? = withTimeout(60_000L) {
        val config = generationConfig {
            responseModalities = listOf(ResponseModality.IMAGE)
            imageConfig = ImageConfig.builder()
                .setImageSize(size)
                .setAspectRatio(aspectRatio)
                .build()
        }

        val model = Firebase.ai.generativeModel(
            modelName = "gemini-3-pro-image-preview",
            generationConfig = config
        )

        val response = model.generateContent(prompt)
        val imagePart = response.candidates.firstOrNull()
            ?.content?.parts?.filterIsInstance<ImagePart>()
            ?.firstOrNull()

        imagePart?.image
    }
}
