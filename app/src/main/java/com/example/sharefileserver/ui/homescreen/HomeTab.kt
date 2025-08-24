package com.example.sharefileserver.ui.homescreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharefileserver.R
import kotlinx.coroutines.delay

@Composable
fun HomeTab(
    isServerRunning: MutableState<Boolean>,
    ipAddress: String,
    onToggleServer: () -> Unit,
    paddingValues: PaddingValues
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2F))
            .padding(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Indicator
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color(0xFF2A2A3D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { onToggleServer() },
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color.White,
                            CircleShape
                        ),

                    ) {
                    Icon(
                        painter = painterResource( if (isServerRunning.value) R.drawable.power_on
                        else R.drawable.power_off) ,
                        contentDescription = if (isServerRunning.value) "Turn Off" else "Turn On",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
            ServerStatusText(isServerRunning)

            Text(
                text = ipAddress,
                color = Color.LightGray,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.Cyan,
                modifier = Modifier.padding(top = 4.dp)
            )

            Card(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .width(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A3D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ServerInfoItem("server", "")
                    ServerInfoItem("port", "8080")
                    ServerInfoItem("webdav", "")
                    ServerInfoItem("https", "")
                    Text(
                        text = "v1.1.12",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun ServerStatusText(isServerRunning: MutableState<Boolean>) {
    // Local loading state when toggling
    val dots = remember { listOf("", ".", "..", "...") }
    var dotIndex by remember { mutableStateOf(0) }

    // Animate the dots while loading
    if (isServerRunning.value) {
        LaunchedEffect(Unit) {
            while (isServerRunning.value) {
                delay(500)
                dotIndex = (dotIndex + 1) % dots.size
            }
        }
    }

    AnimatedContent(
        targetState = isServerRunning,
        label = ""
    ) { (running) ->
        Text(
            text = when {
                running -> "Server is running${dots[dotIndex]}"
                else -> "Server is not running"
            },
            color = if(running) Color(0xFF4CAF50) else Color.LightGray,
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

}
@Composable
fun ServerInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red, CircleShape)
            )
            Text(
                text = value,
                color = Color.LightGray,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
