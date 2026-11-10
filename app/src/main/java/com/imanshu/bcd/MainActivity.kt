package com.imanshu.bcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF090D18)
private val Panel = Color(0xFF171E30)
private val Accent = Color(0xFF91A7FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BcdApp() }
    }
}

@Composable
private fun BcdApp() {
    var selected by remember { mutableStateOf("Library") }
    var showImport by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Night,
            surface = Panel,
            primary = Accent,
            onBackground = Color(0xFFF4F6FF),
            onSurface = Color(0xFFF4F6FF)
        )
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF202B4A), Night, Night))
            )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    NavigationBar(containerColor = Color(0xEE111827)) {
                        NavigationBarItem(
                            selected == "Library",
                            { selected = "Library" },
                            icon = { Icon(Icons.Default.SportsEsports, null) },
                            label = { Text("Library") }
                        )
                        NavigationBarItem(
                            selected == "Controllers",
                            { selected = "Controllers" },
                            icon = { Icon(Icons.Default.Gamepad, null) },
                            label = { Text("Controllers") }
                        )
                        NavigationBarItem(
                            selected == "Settings",
                            { selected = "Settings" },
                            icon = { Icon(Icons.Default.Settings, null) },
                            label = { Text("Settings") }
                        )
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Text("BCD GAMES", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text(selected, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Text("Your PC game library, reimagined for Android.", color = Color(0xFFB7C0D8), fontSize = 14.sp)
                    }
                    item {
                        GlassCard {
                            Text("WELCOME TO BCD", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("A new home for your games.", fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                            Text("This early build is the Android launcher shell. Game runtime support is not included yet.", color = Color(0xFFC3CBE0), lineHeight = 20.sp)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { showImport = true }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Add a game")
                            }
                        }
                    }
                    item {
                        Text(if (selected == "Library") "YOUR LIBRARY" else selected.uppercase(), color = Color(0xFFB7C0D8), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                    item {
                        GlassCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(52.dp).background(Color(0xFF293657), RoundedCornerShape(15.dp)),
                                    contentAlignment = Alignment.Center
                                ) { Icon(Icons.Default.SportsEsports, null, tint = Accent, modifier = Modifier.size(28.dp)) }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(if (selected == "Library") "No games added" else "$selected setup", fontWeight = FontWeight.SemiBold)
                                    Text(if (selected == "Library") "Your imported titles will appear here." else "This section is a prototype.", color = Color(0xFFB7C0D8), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    item {
                        Text("BUILD 0.1.0 • ANDROID PREVIEW", color = Color(0xFF7885A5), fontSize = 10.sp, letterSpacing = 1.5.sp)
                    }
                }
            }
        }
        if (showImport) {
            AlertDialog(
                onDismissRequest = { showImport = false },
                title = { Text("Game import") },
                text = { Text("The library interface is ready. File selection and Windows game installation will be added in a later milestone.") },
                confirmButton = { TextButton(onClick = { showImport = false }) { Text("Got it") } }
            )
        }
    }
}

@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color(0xB91A2338), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(24.dp))
            .padding(20.dp),
        content = content
    )
}
