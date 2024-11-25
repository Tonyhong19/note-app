package io.garrit.android.demo.tododemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import io.garrit.android.demo.tododemo.ui.theme.TodoDemoTheme
import java.util.UUID
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
            val navController = rememberNavController()

            TodoDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(
                                notes = notes,
                                onEditNote = { note ->
                                    navController.navigate("edit/${note.id}")
                                }
                            )
                        }
                        composable("edit/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")
                            val noteToEdit = notes.find { it.id == noteId }
                            if (noteToEdit != null) {
                                EditNoteScreen(note = noteToEdit, onSave = {
                                    navController.popBackStack()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(notes: MutableList<Note>, onEditNote: (Note) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        NoteInputView(notes = notes, onEditNote = onEditNote)
        NoteListView(notes = notes, onEditNote = onEditNote)
    }
}

@Composable
fun NoteInputView(notes: MutableList<Note>, onEditNote: (Note) -> Unit) {
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
fun NoteListView(notes: MutableList<Note>, onEditNote: (Note) -> Unit) {
    LazyColumn {
        items(notes) { note ->
            NoteRowView(note, onEditNote = onEditNote, onDeleteNote = { notes.remove(note) })
        }
    }
}

@Composable
fun NoteRowView(note: Note, onEditNote: (Note) -> Unit, onDeleteNote: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClickableText(
            text = AnnotatedString(note.title),
            onClick = {
                onEditNote(note)
            }
        )
        Button(onClick = { onDeleteNote() }) {
            Text("Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteRowViewPreview() {
    TodoDemoTheme {
        NoteRowView(Note(title = "Sample Note", text = "This is a sample note."), onEditNote = {}, onDeleteNote = {})
    }
}

@Composable
fun EditNoteScreen(note: Note, onSave: () -> Unit) {
    var title by rememberSaveable { mutableStateOf(note.title) }
    var text by rememberSaveable { mutableStateOf(note.text) }

    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") }
        )
        Button(onClick = {
            note.title = title
            note.text = text
            onSave()
        }) {
            Text("Save")
        }
    }
}