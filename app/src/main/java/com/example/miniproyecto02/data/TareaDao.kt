package com.example.miniproyecto02.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Insert
    fun insertar(tarea: Tarea)

    @Update
    fun actualizar(tarea: Tarea)

    @Delete
    fun eliminar(tarea: Tarea)

    @Query("DELETE FROM tareas WHERE estaCompletada = 1")
    fun eliminarCompletadas()
}