package com.makeyourhomeai.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeyourhomeai.BuildConfig
import com.makeyourhomeai.data.models.DesignStyle
import com.makeyourhomeai.data.models.RoomType
import com.makeyourhomeai.data.models.TransformResult
import com.makeyourhomeai.data.repository.ImageTransformRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransformViewModel(context: Context) : ViewModel() {
    
    private val repository = ImageTransformRepository(
        context = context.applicationContext,
        apiKey = BuildConfig.STABILITY_API_KEY
    )
    
    private val _uiState = MutableStateFlow<TransformUiState>(TransformUiState.Initial)
    val uiState: StateFlow<TransformUiState> = _uiState.asStateFlow()
    
    private val _selectedRoomType = MutableStateFlow<RoomType?>(null)
    val selectedRoomType: StateFlow<RoomType?> = _selectedRoomType.asStateFlow()
    
    private val _selectedStyle = MutableStateFlow<DesignStyle?>(null)
    val selectedStyle: StateFlow<DesignStyle?> = _selectedStyle.asStateFlow()
    
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()
    
    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        _uiState.value = TransformUiState.ReadyToTransform
    }
    
    fun setRoomType(roomType: RoomType) {
        _selectedRoomType.value = roomType
    }
    
    fun setStyle(style: DesignStyle) {
        _selectedStyle.value = style
    }
    
    fun transformImage() {
        val uri = _imageUri.value
        val roomType = _selectedRoomType.value
        val style = _selectedStyle.value
        
        if (uri == null || roomType == null || style == null) {
            _uiState.value = TransformUiState.Error("Seleziona immagine, ambiente e stile")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = TransformUiState.Loading
            
            when (val result = repository.transformImage(uri, roomType, style)) {
                is ImageTransformRepository.Result.Success -> {
                    _uiState.value = TransformUiState.Success(result.data)
                }
                is ImageTransformRepository.Result.Error -> {
                    _uiState.value = TransformUiState.Error(result.message)
                }
                else -> {
                    _uiState.value = TransformUiState.Error("Errore sconosciuto")
                }
            }
        }
    }
    
    fun reset() {
        _imageUri.value = null
        _selectedRoomType.value = null
        _selectedStyle.value = null
        _uiState.value = TransformUiState.Initial
    }
}

sealed class TransformUiState {
    object Initial : TransformUiState()
    object ReadyToTransform : TransformUiState()
    object Loading : TransformUiState()
    data class Success(val result: TransformResult) : TransformUiState()
    data class Error(val message: String) : TransformUiState()
}
