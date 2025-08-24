package com.example.sharefileserver.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ButtonType { PRIMARY, SUCCESS, DANGER }

@Composable
fun AppButton(
    text: String,
    type: ButtonType = ButtonType.PRIMARY,
    onClick: () -> Unit
) {
    val background = when (type) {
        ButtonType.PRIMARY -> Color(0xFF2196F3)
        ButtonType.SUCCESS -> Color(0xFF4CAF50)
        ButtonType.DANGER  -> Color(0xFFF44336)
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

