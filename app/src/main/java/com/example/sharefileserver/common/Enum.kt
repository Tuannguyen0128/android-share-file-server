package com.example.sharefileserver.common

sealed class Tab {
    object Home : Tab()
    object Settings : Tab()
    object More : Tab()
}