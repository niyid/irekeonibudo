package com.techducat.irekeonibudo.data

import android.content.Context
import androidx.room.Room

class GameRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        GameDatabase::class.java,
        "ireke-onibudo.db"
    )
        // Single-player local save, nothing worth preserving across a schema bump —
        // if `version` in @Database ever changes, just rebuild the table instead of
        // requiring a Migration object.
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.saveGameDao()

    suspend fun save(player: Player, slot: Int = DEFAULT_SLOT) {
        dao.save(player.toEntity(slot))
    }

    suspend fun load(slot: Int = DEFAULT_SLOT): Player? = dao.load(slot)?.toPlayer()

    suspend fun hasSave(slot: Int = DEFAULT_SLOT): Boolean = dao.load(slot) != null

    suspend fun deleteSave(slot: Int = DEFAULT_SLOT) = dao.delete(slot)

    companion object {
        const val DEFAULT_SLOT = 0
    }
}
