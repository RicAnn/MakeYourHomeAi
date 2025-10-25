package com.makeyourhomeai.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeyourhomeai.repository.ImageTransformRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransformViewModel : ViewModel() {
    private val repository = ImageTransformRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val imageUrl: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun transformImage(
        context: Context,
        imageUri: Uri,
        roomType: String,
        style: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val prompt = buildPrompt(roomType, style)
                val result = repository.transformImage(context, imageUri, prompt)
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    private fun buildPrompt(roomType: String, style: String): String {
        val roomDescription = when (roomType) {
            "living_room" -> "living room"
            "bedroom" -> "bedroom"
            "kitchen" -> "kitchen"
            "bathroom" -> "bathroom"
            else -> "room"
        }

        val styleDescription = when (style) {
            "modern" -> "modern, contemporary design with clean lines and neutral colors"
            "minimalist" -> "minimalist design with simple furniture and uncluttered space"
            "industrial" -> "industrial style with exposed brick, metal fixtures, and raw materials"
            "scandinavian" -> "scandinavian style with light wood, white walls, and cozy textiles"
            else -> "beautifully designed"
        }

        return "A $styleDescription $roomDescription interior design, professional photography, well-lit, high quality"
    }
}