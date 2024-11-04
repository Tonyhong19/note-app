package io.garrit.android.demo.tododemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.garrit.android.demo.tododemo.ui.theme.TodoDemoTheme
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var text: String,
    var isChecked: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val notes = remember { mutableStateListOf<Note>() }

            TodoDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(notes = notes)
                }
            }
        }
    }
}

@Composable
fun MainScreen(notes: MutableList<Note>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        NoteInputView(notes = notes)
        NoteListView(notes = notes)
    }
}

@Composable
fun NoteInputView(notes: MutableList<Note>) {
    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            isError = errorMessage.isNotEmpty()
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") },
            isError = errorMessage.isNotEmpty()
        )
        Button(onClick = {
            errorMessage = validateNoteInput(title, text)
            if (errorMessage.isEmpty()) {
                notes.add(Note(title = title, text = text))
                title = ""
                text = ""
            }
        }) {
            Text("Add Note")
        }
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

fun validateNoteInput(title: String, text: String): String {
    return when {
        title.length < 3 -> "Title must be at least 3 characters."
        title.length > 50 -> "Title must be at most 50 characters."
        text.length > 120 -> "Text must be at most 120 characters."
        else -> ""
    }
}

@Composable
fun NoteListView(notes: List<Note>) {
    LazyColumn {
        items(notes) { note ->
            NoteRowView(note)
        }
    }
}

@Composable
fun NoteRowView(note: Note) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = note.isChecked.value,
            onCheckedChange = { note.isChecked.value = !note.isChecked.value }
        )
        Text(note.title)
        Button(onClick = {  }) {
            Text("Edit")
        }
        Button(onClick = { }) {
            Text("Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteRowViewPreview() {
    TodoDemoTheme {
        NoteRowView(Note(title = "Sample Note", text = "This is a sample note."))
    }
}