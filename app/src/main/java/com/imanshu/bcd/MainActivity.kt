package com.imanshu.bcd

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

private val Night = Color(0xFF090D18)
private val Accent = Color(0xFF91A7FF)
private const val PROFILE = "https://github.com/Imanshu999"
private const val REPO = "https://github.com/Imanshu999/BCD-APK"
private const val PREFS = "player_hub_library"
private const val URI_SET = "saved_uris"

private data class HubFile(val uri: Uri, val name: String, val ext: String, val mime: String, val size: Long) {
    val engine: String get() = when (ext) {
        "html", "htm" -> "Built-in Web Engine"
        "txt", "md", "json", "xml", "log", "csv" -> "Built-in Text Reader"
        "png", "jpg", "jpeg", "webp", "gif" -> "Built-in Image Tool (preview pending)"
        "apk" -> "Android Package Installer (install required)"
        "pdf" -> "PDF Engine not added"
        "iso", "zip", "obb", "xapk", "apks" -> "Archive / Game Runtime not added"
        else -> "No internal tool registered"
    }
    val internal: Boolean get() = ext in setOf("html", "htm", "txt", "md", "json", "xml", "log", "csv")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlayerHubApp() }
    }
}

@Composable
private fun PlayerHubApp() {
    val context = LocalContext.current
    var page by remember { mutableStateOf("Library") }
    var loading by remember { mutableStateOf(true) }
    var preview by remember { mutableStateOf<HubFile?>(null) }
    val files = remember { mutableStateListOf<HubFile>() }
    var deleteTarget by remember { mutableStateOf<HubFile?>(null) }
    var clearAll by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet(URI_SET, emptySet()).orEmpty()
        saved.forEach { raw ->
            try { files.add(readMetadata(context, Uri.parse(raw))) } catch (_: Exception) { }
        }
        delay(700)
        loading = false
    }

    fun saveLibrary() {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putStringSet(URI_SET, files.map { it.uri.toString() }.toSet()).apply()
    }
    fun removeFromLibrary(file: HubFile) {
        files.removeAll { it.uri == file.uri }
        saveLibrary()
        try { context.contentResolver.releasePersistableUriPermission(file.uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
        Toast.makeText(context, "Removed from Player Hub (original file kept)", Toast.LENGTH_SHORT).show()
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: SecurityException) { }
            try {
                val f = readMetadata(context, uri)
                if (files.none { it.uri == uri }) files.add(f)
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                    .putStringSet(URI_SET, (files.map { it.uri.toString() } + uri.toString()).toSet()).apply()
                Toast.makeText(context, "Added to Player Hub", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) { Toast.makeText(context, "Unable to read this file", Toast.LENGTH_LONG).show() }
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Night, surface = Color(0xFF171E30), primary = Accent, onSurface = Color.White)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF202B4A), Night, Night)))) {
            Scaffold(containerColor = Color.Transparent, bottomBar = {
                NavigationBar(containerColor = Color(0xEE111827)) {
                    NavigationBarItem(page == "Library", { page = "Library" }, icon = { Icon(Icons.Default.SportsEsports, null) }, label = { Text("Library") })
                    NavigationBarItem(page == "Controllers", { page = "Controllers" }, icon = { Icon(Icons.Default.Gamepad, null) }, label = { Text("Controllers") })
                    NavigationBarItem(page == "Settings", { page = "Settings" }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
                }
            }) { padding ->
                LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        Text("PLAYER HUB", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text(page, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("Play • Run • Manage", color = Color(0xFFB7C0D8))
                    }
                    when (page) {
                        "Library" -> {
                            item {
                                CardBox {
                                    Text("YOUR GAME & FILE LIBRARY", color = Accent, fontWeight = FontWeight.Bold)
                                    Text("One hub. Built-in tools.", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Add a file once. Player Hub remembers it and shows which internal tool is available.", color = Color(0xFFC3CBE0))
                                    Button(onClick = { picker.launch(arrayOf("*/*")) }) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Add files / games") }
                                }
                            }
                            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("MY LIBRARY (" + files.size + ")", color = Color(0xFFB7C0D8), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    if (files.isNotEmpty()) TextButton(onClick = { clearAll = true }) { Icon(Icons.Default.Delete, null); Text("Clear all") }
                }
                OutlinedTextField(value = searchText, onValueChange = { searchText = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Search files…") }, leadingIcon = { Icon(Icons.Default.Search, null) })
            }
                            if (files.isEmpty()) item { CardBox { Text("No files added yet"); Text("Select HTML, text, APK, ISO, ZIP, OBB or another file.") } }
                            else items(files.filter { it.name.contains(searchText, ignoreCase = true) || it.ext.contains(searchText, ignoreCase = true) }.sortedBy { it.name.lowercase() }, key = { it.uri.toString() }) { file ->
                                CardBox {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(when (file.ext) {
                                            "apk" -> Icons.Default.Android
                                            "html", "htm", "js", "css", "kt", "java", "py" -> Icons.Default.Code
                                            "txt", "md", "json", "xml", "log", "csv" -> Icons.Default.Description
                                            "pdf" -> Icons.Default.PictureAsPdf
                                            "zip", "7z", "rar", "obb", "xapk", "apks", "iso" -> Icons.Default.Archive
                                            "mp3", "wav", "ogg", "m4a" -> Icons.Default.AudioFile
                                            "mp4", "mkv", "webm" -> Icons.Default.Movie
                                            else -> Icons.Default.InsertDriveFile
                                        }, null, tint = Accent, modifier = Modifier.size(42.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(file.name, fontWeight = FontWeight.SemiBold, maxLines = 2)
                                            Text(file.ext.uppercase() + " • " + sizeLabel(file.size), color = Color(0xFFB7C0D8), fontSize = 11.sp)
                                            Text(file.engine, color = Accent, fontSize = 10.sp)
                                        }
                                        IconButton(onClick = { deleteTarget = file }) { Icon(Icons.Default.Delete, "Remove from library", tint = Color(0xFFFF8295)) }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(onClick = {
                                            if (file.internal) preview = file
                                            else Toast.makeText(context, runMessage(file), Toast.LENGTH_LONG).show()
                                        }, modifier = Modifier.weight(1f)) { Text(if (file.internal) "Run inside" else "Run") }
                                        if (file.ext == "apk") OutlinedButton(onClick = { installApk(context, file.uri) }, modifier = Modifier.weight(1f)) { Text("Install") }
                                        else OutlinedButton(onClick = { Toast.makeText(context, file.engine, Toast.LENGTH_SHORT).show() }, modifier = Modifier.weight(1f)) { Text("Tool info") }
                                    }
                                }
                            }
                            item { Text("Remove deletes the library entry only, not the original device file. Clear all is also available.", color = Color(0xFF8995B2), fontSize = 11.sp) }
                        }
                        "Controllers" -> item { CardBox { Icon(Icons.Default.Gamepad, null, tint = Accent); Text("Player Hub Controllers", fontSize = 20.sp, fontWeight = FontWeight.SemiBold); Text("Custom touch controls, controller mapping and per-game profiles need a dedicated input engine. This module is still under development.", color = Color(0xFFC3CBE0)) } }
                        else -> {
                            item { CardBox { Text("ABOUT PLAYER HUB", color = Accent, fontWeight = FontWeight.Bold); Text("Your own Android game and file hub.") } }
                            item {
                                CardBox {
                                    Text("DEVELOPER LINKS", color = Accent, fontWeight = FontWeight.Bold)
                                    LinkRow("GH", "GitHub profile", "@Imanshu999") { openUrl(context, PROFILE) }
                                    LinkRow("GH", "Player Hub repository", "Imanshu999 / BCD-APK") { openUrl(context, REPO) }
                                }
                            }
                            item { CardBox { Text("INTERNAL TOOL REGISTRY", color = Accent, fontWeight = FontWeight.Bold); Text("Built in: HTML Web Engine and Text Reader. Not yet built: PDF reader, image renderer, archive tools, APK virtual runtime and Windows game runtime.", color = Color(0xFFC3CBE0)) } }
                            item { CardBox { Text("FILE ACCESS", color = Accent, fontWeight = FontWeight.Bold); Text("The Android file picker grants Player Hub access only to files you select. No broad storage permission is requested.", color = Color(0xFFC3CBE0)) } }
                        }
                    }
                    item { Text("PLAYER HUB • INTERNAL TOOLS PREVIEW", color = Color(0xFF7885A5), fontSize = 10.sp) }
                }
            }
            if (loading) {
                Box(Modifier.fillMaxSize().background(Night.copy(alpha = 0.97f)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HubLogo()
                        Spacer(Modifier.height(16.dp))
                        Text("PLAYER HUB", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        Text("Play • Run • Manage", color = Accent)
                        Spacer(Modifier.height(22.dp))
                        CircularProgressIndicator(color = Accent)
                        Text("Loading your library…", color = Color(0xFFB7C0D8), modifier = Modifier.padding(top = 10.dp))
                    }
                }
            }
        }
    }

    deleteTarget?.let { file ->
        AlertDialog(onDismissRequest = { deleteTarget = null }, title = { Text("Remove file?") },
            text = { Text("Remove " + file.name + " from Player Hub? The original file on your device will not be deleted.") },
            confirmButton = { TextButton(onClick = { removeFromLibrary(file); deleteTarget = null }) { Text("Remove") } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel") } })
    }
    if (clearAll) {
        AlertDialog(onDismissRequest = { clearAll = false }, title = { Text("Clear library?") },
            text = { Text("All entries will be removed from Player Hub. Original files will remain on your device.") },
            confirmButton = { TextButton(onClick = {
                files.toList().forEach { f -> try { context.contentResolver.releasePersistableUriPermission(f.uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { } }
                files.clear()
                saveLibrary()
                clearAll = false
            }) { Text("Clear all") } },
            dismissButton = { TextButton(onClick = { clearAll = false }) { Text("Cancel") } })
    }

    preview?.let { file ->
        AlertDialog(
            onDismissRequest = { preview = null },
            title = { Text(file.name) },
            text = {
                if (file.ext == "html" || file.ext == "htm") {
                    AndroidView(factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = false
                            webViewClient = WebViewClient()
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            loadDataWithBaseURL(null, readText(context, file.uri), "text/html", "UTF-8", null)
                        }
                    }, modifier = Modifier.fillMaxWidth().height(420.dp))
                } else {
                    Text(readText(context, file.uri).take(10000))
                }
            },
            confirmButton = { TextButton(onClick = { preview = null }) { Text("Close") }
            }
        )
    }
}

private fun readMetadata(context: Context, uri: Uri): HubFile {
    val name = queryName(context, uri)
    val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
    var size = -1L
    context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { c ->
        if (c.moveToFirst()) { val i = c.getColumnIndex(OpenableColumns.SIZE); if (i >= 0 && !c.isNull(i)) size = c.getLong(i) }
    }
    return HubFile(uri, name, name.substringAfterLast('.', "").lowercase(), mime, size)
}

private fun queryName(context: Context, uri: Uri): String {
    var name = uri.lastPathSegment ?: "Selected file"
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        if (c.moveToFirst()) { val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME); if (i >= 0) name = c.getString(i) ?: name }
    }
    return name
}

private fun readText(context: Context, uri: Uri): String =
    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: "Unable to read file."

private fun installApk(context: Context, uri: Uri) {
    try {
        val i = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(i)
    } catch (_: Exception) { Toast.makeText(context, "Android could not start APK installation.", Toast.LENGTH_LONG).show() }
}

private fun runMessage(file: HubFile): String = when (file.ext) {
    "apk" -> "An Android app cannot execute another APK as an in-app process without installing it. This needs a separate virtual Android runtime."
    "iso", "zip", "obb", "xapk", "apks" -> "Imported. Player Hub's archive/game runtime for this format has not been implemented yet."
    "pdf" -> "The built-in PDF reader is not implemented yet."
    else -> "No internal runtime is registered for this file type."
}

private fun sizeLabel(n: Long): String = when {
    n < 0 -> "Size unknown"
    n < 1024 -> n.toString() + " B"
    n < 1024 * 1024 -> (n / 1024).toString() + " KB"
    else -> String.format("%.1f MB", n / (1024f * 1024f))
}

private fun openUrl(context: Context, url: String) {
    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    catch (_: Exception) { Toast.makeText(context, "Could not open link.", Toast.LENGTH_SHORT).show() }
}

@Composable
private fun HubLogo() {
    Box(Modifier.size(84.dp).background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF0891B2))), RoundedCornerShape(26.dp)), contentAlignment = Alignment.Center) {
        Icon(Icons.Default.Gamepad, null, tint = Color.White, modifier = Modifier.size(50.dp))
    }
}

@Composable
private fun LinkRow(tag: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).background(Color(0xFF293657), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(tag, color = Accent, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Color(0xFFB7C0D8), fontSize = 12.sp) }
        Text("Open ↗", color = Accent, fontSize = 12.sp)
    }
}

@Composable
private fun CardBox(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xB91A2338), RoundedCornerShape(22.dp)).border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(22.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
}
