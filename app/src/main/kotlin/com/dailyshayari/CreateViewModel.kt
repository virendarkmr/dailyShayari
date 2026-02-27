package com.dailyshayari

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateViewModel : ViewModel() {

    private val _shayariText = MutableStateFlow("")
    val shayariText = _shayariText.asStateFlow()

    private val _textColor = MutableStateFlow(Color.White)
    val textColor = _textColor.asStateFlow()

    private val _fontSize = MutableStateFlow(24f)
    val fontSize = _fontSize.asStateFlow()

    private val _fontFamily = MutableStateFlow<FontFamily>(FontFamily.Default)
    val fontFamily = _fontFamily.asStateFlow()

    private val _selectedImage = MutableStateFlow<Any?>(null)
    val selectedImage = _selectedImage.asStateFlow()

    private val _selectedAbstractIndex = MutableStateFlow<Int?>(0)
    val selectedAbstractIndex = _selectedAbstractIndex.asStateFlow()

    private val _offsetX = MutableStateFlow(0f)
    val offsetX = _offsetX.asStateFlow()

    private val _offsetY = MutableStateFlow(0f)
    val offsetY = _offsetY.asStateFlow()

    private val _aspectRatio = MutableStateFlow(1f)
    val aspectRatio = _aspectRatio.asStateFlow()

    fun onShayariTextChange(newText: String) {
        _shayariText.value = newText
    }

    fun onTextColorChange(newColor: Color) {
        _textColor.value = newColor
    }

    fun onFontSizeChange(newSize: Float) {
        _fontSize.value = newSize
    }

    fun onFontFamilyChange(newFontFamily: FontFamily) {
        _fontFamily.value = newFontFamily
    }

    fun onImageSelected(newImage: Any?, aspectRatio: Float = 1f) {
        _selectedImage.value = newImage
        _selectedAbstractIndex.value = null
        _aspectRatio.value = aspectRatio
    }

    fun onAbstractSelected(newIndex: Int?) {
        _selectedAbstractIndex.value = newIndex
        _selectedImage.value = null
        _aspectRatio.value = 1f
    }

    fun onOffsetChange(x: Float, y: Float) {
        _offsetX.value += x
        _offsetY.value += y
    }

    fun resetState() {
        _shayariText.value = ""
        _textColor.value = Color.White
        _fontSize.value = 24f
        _fontFamily.value = FontFamily.Default
        _selectedImage.value = null
        _selectedAbstractIndex.value = 0
        _offsetX.value = 0f
        _offsetY.value = 0f
        _aspectRatio.value = 1f
    }
}