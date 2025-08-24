package com.example.sharefileserver.ui.navbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sharefileserver.common.Tab

@Composable
fun BottomNavigationBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    BottomAppBar(
        containerColor = Color.DarkGray,
        contentColor = Color.LightGray
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                tab = Tab.Home,
                selectedTab = selectedTab,
                onClick = { onTabSelected(Tab.Home) },
                icon = Icons.Default.Home,
                contentDescription = "Home"
            )
            BottomNavItem(
                tab = Tab.Settings,
                selectedTab = selectedTab,
                onClick = { onTabSelected(Tab.Settings) },
                icon = Icons.Default.Settings,
                contentDescription = "Settings"
            )
            BottomNavItem(
                tab = Tab.More,
                selectedTab = selectedTab,
                onClick = { onTabSelected(Tab.More) },
                icon = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }
    }
}