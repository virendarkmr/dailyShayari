package com.dailyshayari.util

fun isHindi(text: String): Boolean {
    return text.any { it in '\u0900'..'\u097F' }
}
