package com.example.miniproyecto02.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import kotlinx.coroutines.flow.Flow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.miniproyecto02.data.Tarea
import com.example.miniproyecto02.domain.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.tareaDao()
    val coroutineScope = rememberCoroutineScope()

    val tareas by dao.obtenerTodas().collectAsState(initial = emptyList())

    var menuExpandido by remember { mutableStateOf(false) }
    var nuevaTareaTitulo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Borrar completadas") },
                            onClick = {
                                menuExpandido = false
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.eliminarCompletadas()
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Fila para agregar nueva tarea
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevaTareaTitulo,
                    onValueChange = { nuevaTareaTitulo = it },
                    label = { Text("Nueva tarea") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (nuevaTareaTitulo.isNotBlank()) {
                            coroutineScope.launch(Dispatchers.IO) {
                                dao.insertar(Tarea(titulo = nuevaTareaTitulo))
                                nuevaTareaTitulo = ""
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Listado dinámico de tareas
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tareas) { tarea ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Checkbox y Título
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = tarea.estaCompletada,
                                    onCheckedChange = { estaCompletada ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            dao.actualizar(tarea.copy(estaCompletada = estaCompletada))
                                        }
                                    }
                                )
                                Text(
                                    text = tarea.titulo,
                                    textDecoration = if (tarea.estaCompletada) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }

                            // Botón eliminar individual
                            IconButton(onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.eliminar(tarea)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}