package com.example.sharefileserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import com.example.sharefileserver.ui.theme.ShareFileServerTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sharefileserver.common.SERVER_STOPPED
import com.example.sharefileserver.service.startHttpServer
import com.example.sharefileserver.service.stopHttpServer
import com.example.sharefileserver.common.Tab
import com.example.sharefileserver.common.checkStoragePermissions
import com.example.sharefileserver.common.getIPAddress
import com.example.sharefileserver.common.requestStoragePermissions
import com.example.sharefileserver.ui.homescreen.HomeTab
import com.example.sharefileserver.ui.homescreen.MoreTab
import com.example.sharefileserver.ui.homescreen.SettingTab
import com.example.sharefileserver.ui.navbar.BottomNavigationBar

class MainActivity : ComponentActivity() {
    private lateinit var serverReceiver: BroadcastReceiver
    private lateinit var isServerRunning: MutableState<Boolean>
    private lateinit var hasStoragePermission: MutableState<Boolean>
    private lateinit var showPermissionDialog: MutableState<Boolean>


    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        hasStoragePermission.value = allGranted
        showPermissionDialog.value = !allGranted
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        serverReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == (packageName+ SERVER_STOPPED)) {
                    Log.d("MainActivity", "Received SERVER_STOPPED broadcast")
                    isServerRunning.value = false
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(serverReceiver, IntentFilter(packageName + SERVER_STOPPED))

        setContent {
            ShareFileServerTheme {
                var  selectedTab by remember { mutableStateOf<Tab>(Tab.Home)}
                isServerRunning = remember { mutableStateOf(false) }
                val ipAddress = getIPAddress() ?: "192.168.31.80"
                hasStoragePermission = remember { mutableStateOf(checkStoragePermissions(this)) }
                showPermissionDialog = remember { mutableStateOf(false) }

                // Check permissions periodically
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    hasStoragePermission.value = checkStoragePermissions(context)
                    if(hasStoragePermission.value) showPermissionDialog.value= false
                }

                // Permission Dialog
                if (!hasStoragePermission.value && showPermissionDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showPermissionDialog.value = false },
                        title = { Text("Storage Permission Required") },
                        text = {
                            Text("This app needs storage permission to browse and serve files through the HTTP server. Please grant the permission to continue.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showPermissionDialog.value = false
                                    requestStoragePermissions(this, permissionLauncher)
                                }
                            ) {
                                Text("Grant Permission")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showPermissionDialog.value = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(selectedTab, onTabSelected = { selectedTab = it} )
                    }
                ) { paddingValues ->
                    when (selectedTab){
                        is Tab.Home ->
                            HomeTab(
                                isServerRunning= isServerRunning,
                                ipAddress = ipAddress,
                                onToggleServer = {
                                    hasStoragePermission.value = checkStoragePermissions(this);
                                    if (isServerRunning.value) {
                                        stopHttpServer()
                                        isServerRunning.value = !isServerRunning.value
                                    } else {
                                        if(hasStoragePermission.value){
                                            startHttpServer(8080)
                                            isServerRunning.value = !isServerRunning.value
                                        }else{
                                            showPermissionDialog.value = true
                                        }

                                    }

                                },
                                paddingValues = paddingValues
                            )
                        is Tab.Settings -> SettingTab()
                        is Tab.More -> MoreTab()
                    }
                }
            }
        }
    }
}
