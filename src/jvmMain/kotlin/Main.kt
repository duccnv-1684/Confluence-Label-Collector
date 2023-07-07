import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
                        val fileDialog = FileDialog(ComposeWindow()).apply {
                            filenameFilter = FilenameFilter { _, name -> name.contains(".html") }
                            mode = FileDialog.LOAD
                            isMultipleMode = false
                            isVisible = true
                        }
                        filePath = fileDialog.directory + fileDialog.file
                        data = DocumentParser.parseDocument(File(filePath))
                    }) {
                        Text("Select File")
                    }
                    Text(filePath)
                }
                if (data.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        CopyButton("Copy for En Android") {
                            val content = DocumentParser.generateContent(data, isIOS = false, isVi = false)
                            clipboardManager.setText(AnnotatedString(content))
                        }
                        CopyButton("Copy for Vi Android") {
                            val content = DocumentParser.generateContent(data, isIOS = false, isVi = true)
                            clipboardManager.setText(AnnotatedString(content))
                        }
                        CopyButton("Copy for En iOS") {
                            val content = DocumentParser.generateContent(data, isIOS = true, isVi = false)
                            clipboardManager.setText(AnnotatedString(content))
                        }
                        CopyButton("Copy for Vi iOS") {
                            val content = DocumentParser.generateContent(data, isIOS = true, isVi = true)
                            clipboardManager.setText(AnnotatedString(content))
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
fun CopyButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text)
    }
}

@Composable
fun TableOfLabel(data: List<LabelData>) {
    Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        TableHeaderCell("Label ID", this)
        TableHeaderCell("Label EN", this)
        TableHeaderCell("Label VI", this)
    }
    LazyColumn {
        items(data) {
            RowOfLabel(it)
        }
    }
}

@Composable
fun TableHeaderCell(label: String, rowScope: RowScope) = with(rowScope) {
    Box(
        modifier = Modifier.weight(1f).border(BorderStroke(1.dp, Color.Black)).background(Color.Red),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(modifier = Modifier.fillMaxSize().padding(4.dp), text = "Label ID", color = Color.White)
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
