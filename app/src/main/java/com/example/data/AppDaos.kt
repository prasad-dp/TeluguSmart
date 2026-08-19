package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWordDao {
    @Query("SELECT * FROM user_words ORDER BY frequency DESC, lastUsedTimestamp DESC")
    fun getAllUserWords(): Flow<List<UserWord>>

    @Query("SELECT * FROM user_words WHERE englishPhonetic = :phonetic LIMIT 5")
    suspend fun getWordsForPhonetic(phonetic: String): List<UserWord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(word: UserWord): Long

    @Delete
    suspend fun delete(word: UserWord)

    @Query("DELETE FROM user_words WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_snippets ORDER BY isPinned DESC, timestamp DESC LIMIT 50")
    fun getAllSnippets(): Flow<List<ClipboardSnippet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: ClipboardSnippet): Long

    @Update
    suspend fun updateSnippet(snippet: ClipboardSnippet)

    @Delete
    suspend fun deleteSnippet(snippet: ClipboardSnippet)

    @Query("DELETE FROM clipboard_snippets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_snippets WHERE isPinned = 0")
    suspend fun clearUnpinned()
}
