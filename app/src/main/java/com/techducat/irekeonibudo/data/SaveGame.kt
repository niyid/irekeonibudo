package com.techducat.irekeonibudo.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * A save slot. Charms/visited-nodes/flags are stored as comma-joined strings
 * to keep the schema trivial for a small single-player save file — this is
 * not meant to scale past a handful of slots.
 */
@Entity(tableName = "save_games")
data class SaveGameEntity(
    @PrimaryKey val slot: Int,
    val savedAt: Long,
    val currentNodeId: String,
    val igboya: Int,
    val oogun: Int,
    val ilera: Int,
    val charms: String,
    val visitedNodes: String,
    val flags: String
)

class Converters {
    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString(separator = "|")

    @TypeConverter
    fun stringToList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")
}

@Dao
interface SaveGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(save: SaveGameEntity)

    @Query("SELECT * FROM save_games WHERE slot = :slot LIMIT 1")
    suspend fun load(slot: Int): SaveGameEntity?

    @Query("SELECT * FROM save_games ORDER BY savedAt DESC")
    suspend fun all(): List<SaveGameEntity>

    @Query("DELETE FROM save_games WHERE slot = :slot")
    suspend fun delete(slot: Int)
}

@Database(entities = [SaveGameEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun saveGameDao(): SaveGameDao
}

/** Maps between the Room entity and the in-memory [Player] model. */
fun Player.toEntity(slot: Int): SaveGameEntity = SaveGameEntity(
    slot = slot,
    savedAt = System.currentTimeMillis(),
    currentNodeId = currentNodeId,
    igboya = igboya,
    oogun = oogun,
    ilera = ilera,
    charms = charms.joinToString("|") { it.name },
    visitedNodes = visitedNodes.joinToString("|"),
    flags = flags.joinToString("|")
)

fun SaveGameEntity.toPlayer(): Player = Player(
    igboya = igboya,
    oogun = oogun,
    ilera = ilera,
    charms = if (charms.isBlank()) emptyList() else charms.split("|").map { Charm.valueOf(it) },
    currentNodeId = currentNodeId,
    visitedNodes = if (visitedNodes.isBlank()) emptySet() else visitedNodes.split("|").toSet(),
    flags = if (flags.isBlank()) emptySet() else flags.split("|").toSet()
)
