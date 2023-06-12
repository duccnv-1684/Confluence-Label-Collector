import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

@Composable
@Preview
fun App() {
    var filePath by remember { mutableStateOf("") }

    var data by remember { mutableStateOf(emptyList<LabelData>()) }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    MaterialTheme(
        MaterialTheme.colors.copy(
            primary = Color.Red,
            secondary = Color.White
        )
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(1.5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = {
                        val fileDialog = FileDialog(ComposeWindow())
                        fileDialog.filenameFilter = FilenameFilter { _, name -> name.contains(".html") }
                        fileDialog.mode = FileDialog.LOAD
                        fileDialog.isMultipleMode = false
                        fileDialog.isVisible = true
                        filePath = fileDialog.directory + fileDialog.file
                        data = DocumentParser.parseDocument(File(filePath))
                    }) {
                        Text("Select File")
                    }
                    Text(filePath)
                }
                if (data.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = {
                            val content = DocumentParser.generateContent(data, isIOS = false, isVi = false)
                            clipboardManager.setText(AnnotatedString(content))
                        }) {
                            Text("Copy for En Android")
                        }
                        Button(onClick = {
                            val content = DocumentParser.generateContent(data, isIOS = false, isVi = true)
                            clipboardManager.setText(AnnotatedString(content))
                        }) {
                            Text("Copy for Vi Android")
                        }
                        Button(onClick = {
                            val content = DocumentParser.generateContent(data, isIOS = true, isVi = false)
                            clipboardManager.setText(AnnotatedString(content))
                        }) {
                            Text("Copy for En iOS")
                        }
                        Button(onClick = {
                            val content = DocumentParser.generateContent(data, isIOS = true, isVi = true)
                            clipboardManager.setText(AnnotatedString(content))
                        }) {
                            Text("Copy for Vi iOS")
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
                    TableOfLabel(data)
                }
            }
        }
    }
}

@Composable
fun TableOfLabel(data: List<LabelData>) {
    Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Box(
            modifier = Modifier.weight(1f).border(BorderStroke(1.dp, Color.Black)).background(Color.Red),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = "Label ID", color = Color.White)
        }
        Box(
            modifier = Modifier.weight(1f).border(BorderStroke(1.dp, Color.Black)).background(Color.Red),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = "Label EN", color = Color.White)
        }
        Box(
            modifier = Modifier.weight(1f).border(BorderStroke(1.dp, Color.Black)).background(Color.Red),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = "Label VI", color = Color.White)
        }
    }
    LazyColumn {
        items(data) {
            RowOfLabel(it)
        }
    }
}

@Composable
fun RowOfLabel(data: LabelData) {
    Row(modifier = Modifier.fillMaxWidth().height(64.dp)) {
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().border(BorderStroke(1.dp, Color.Black)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = data.id)
        }
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().border(BorderStroke(1.dp, Color.Black)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = data.en)
        }
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().border(BorderStroke(1.dp, Color.Black)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = data.vi)
        }
    }
}

fun main() = application {
    Window(title = "Confluence Label Collector", onCloseRequest = ::exitApplication) {
        App()
    }
}
