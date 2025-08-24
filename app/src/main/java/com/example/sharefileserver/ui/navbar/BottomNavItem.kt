package com.example.sharefileserver.ui.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sharefileserver.common.Tab

@Composable
fun BottomNavItem(
    tab: Tab,
    selectedTab: Tab,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    val isSelected = tab == selectedTab
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) Color.Gray else Color.Transparent)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) Color.White else Color.Cyan
        )
    }
}