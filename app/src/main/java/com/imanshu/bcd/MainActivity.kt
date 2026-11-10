package com.imanshu.bcd

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF090D18)
private val Panel = Color(0xFF171E30)
private val Accent = Color(0xFF91A7FF)
private const val GITHUB_PROFILE = "https://github.com/Imanshu999"
private const val GITHUB_REPO = "https://github.com/Imanshu999/BCD-APK"

private data class ImportedFile(val uri: Uri, val name: String, val extension: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlayerHubApp() }
    }
}

@Composable
private fun PlayerHubApp() {
    val context = LocalContext.current
    var selected by remember { mutableStateOf("Library") }
    val importedFiles = remember { mutableStateListOf<ImportedFile>() }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { }
            val name = queryDisplayName(context, uri)
            val ext = name.substringAfterLast('.', "").lowercase()
            if (importedFiles.none { it.uri == uri }) importedFiles.add(ImportedFile(uri, name, ext))
            Toast.makeText(context, "Added to Player Hub", Toast.LENGTH_SHORT).show()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Night, surface = Panel, primary = Accent,
        onBackground = Color(0xFFF4F6FF), onSurface = Color(0xFFF4F6FF)
    )) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF202B4A), Night, Night)))) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    NavigationBar(containerColor = Color(0xEE111827)) {
                        NavigationBarItem(selected == "Library", { selected = "Library" }, icon = { Icon(Icons.Default.SportsEsports, null) }, label = { Text("Library") })
                        NavigationBarItem(selected == "Controllers", { selected = "Controllers" }, icon = { Icon(Icons.Default.Gamepad, null) }, label = { Text("Controllers") })
                        NavigationBarItem(selected == "Settings", { selected = "Settings" }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
                    }
                }
            ) { padding ->
                LazyColumn(
                    Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("PLAYER HUB", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text(selected, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (selected == "Library") "Your games and supported files, in one place."
                            else if (selected == "Controllers") "Controller tools are coming in a future update."
                            else "App preferences and developer links.",
                            color = Color(0xFFB7C0D8), fontSize = 14.sp
                        )
                    }
                    when (selected) {
                        "Library" -> {
                            item {
                                GlassCard {
                                    Text("YOUR GAME LIBRARY", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Bring your game files together.", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(6.dp))
                                    Text("Choose files from your device. Android's file picker lets you approve exactly which files Player Hub can access.", color = Color(0xFFC3CBE0), lineHeight = 20.sp)
                                    Spacer(Modifier.height(14.dp))
                                    Button(onClick = { filePicker.launch(arrayOf("*/*")) }) {
                                        Icon(Icons.Default.Add, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Add files / games")
                                    }
                                }
                            }
                            item { Text("IMPORTED FILES (" + importedFiles.size + ")", color = Color(0xFFB7C0D8), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                            if (importedFiles.isEmpty()) {
                                item {
                                    GlassCard {
                                        Text("Your library is empty", fontWeight = FontWeight.SemiBold)
                                        Text("Add APK, ISO, ZIP, OBB or other game files to see them here.", color = Color(0xFFB7C0D8), fontSize = 13.sp)
                                    }
                                }
                            } else {
                                items(importedFiles, key = { it.uri.toString() }) { file ->
                                    GlassCard {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(Modifier.size(48.dp).background(Color(0xFF293657), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                                                Icon(if (file.extension == "apk") Icons.Default.Android else Icons.Default.SportsEsports, null, tint = Accent)
                                            }
                                            Spacer(Modifier.width(12.dp))
                                            Column(Modifier.weight(1f)) {
                                                Text(file.name, fontWeight = FontWeight.SemiBold)
                                                Text(file.extension.uppercase().ifBlank { "FILE" }, color = Color(0xFFB7C0D8), fontSize = 11.sp)
                                            }
                                            TextButton(onClick = { openImportedFile(context, file) }) { Text(if (file.extension == "apk") "Install" else "Open") }
                                        }
                                    }
                                }
                            }
                            item {
                                Text("APK files are handed to Android's package installer. ISO, ZIP, OBB and PC game files can be imported, but need a compatible app or separate game runtime to run.", color = Color(0xFF8995B2), fontSize = 11.sp, lineHeight = 16.sp)
                            }
                        }
                        "Controllers" -> item {
                            GlassCard {
                                Icon(Icons.Default.Gamepad, null, tint = Accent, modifier = Modifier.size(34.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Player Hub Controllers", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                Text("Controller mapping, touch controls and per-game profiles are planned. They are not implemented in this preview yet.", color = Color(0xFFC3CBE0), lineHeight = 20.sp)
                            }
                        }
                        else -> {
                            item {
                                GlassCard {
                                    Text("ABOUT PLAYER HUB", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text("Player Hub", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Android game library and file launcher.", color = Color(0xFFB7C0D8))
                                }
                            }
                            item {
                                GlassCard {
                                    Text("DEVELOPER", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(Modifier.height(12.dp))
                                    LinkRow("GH", "GitHub profile", "@Imanshu999") { openUrl(context, GITHUB_PROFILE) }
                                    Spacer(Modifier.height(10.dp))
                                    LinkRow("GH", "Player Hub repository", "Imanshu999 / BCD-APK") { openUrl(context, GITHUB_REPO) }
                                }
                            }
                            item {
                                GlassCard {
                                    Text("SOCIAL MEDIA", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text("Only GitHub is configured right now. Other social profile links can be added when their URLs are provided.", color = Color(0xFFC3CBE0), lineHeight = 20.sp)
                                }
                            }
                            item {
                                GlassCard {
                                    Text("FILE ACCESS", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text("No blanket storage permission is requested. Select files using Android's system picker; access is limited to the items you choose.", color = Color(0xFFC3CBE0), lineHeight = 20.sp)
                                }
                            }
                        }
                    }
                    item { Text("PLAYER HUB • ANDROID PREVIEW", color = Color(0xFF7885A5), fontSize = 10.sp, letterSpacing = 1.5.sp) }
                }
            }
        }
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String {
    var name = uri.lastPathSegment ?: "Selected file"
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) name = cursor.getString(index) ?: name
        }
    }
    return name
}

private fun openImportedFile(context: Context, file: ImportedFile) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            if (file.extension == "apk") setDataAndType(file.uri, "application/vnd.android.package-archive")
            else setDataAndType(file.uri, context.contentResolver.getType(file.uri) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, if (file.extension == "apk") "Install APK" else "Open file with"))
    } catch (_: Exception) {
        Toast.makeText(context, "No compatible app found for this file.", Toast.LENGTH_LONG).show()
    }
}

private fun openUrl(context: Context, url: String) {
    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    catch (_: Exception) { Toast.makeText(context, "Could not open link.", Toast.LENGTH_SHORT).show() }
}

@Composable
private fun LinkRow(tag: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).background(Color(0xFF293657), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
            Text(tag, color = Accent, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color(0xFFB7C0D8), fontSize = 12.sp)
        }
        Text("Open ↗", color = Accent, fontSize = 12.sp)
    }
}

@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color(0xB91A2338), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(24.dp)).padding(20.dp),
        content = content
    )
}
