package com.thesubgraph.askstack.features.assistant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thesubgraph.askstack.features.assistant.data.local.database.entities.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    
    @Query("SELECT * FROM conversations ORDER BY updated_at DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>
    
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)
    
    @Update
    suspend fun updateConversation(conversation: ConversationEntity)
    
    @Query("UPDATE conversations SET title = :title, updated_at = :updatedAt WHERE id = :conversationId")
    suspend fun updateConversationTitle(conversationId: String, title: String, updatedAt: Long)
    
    @Query("UPDATE conversations SET last_message = :lastMessage, updated_at = :updatedAt WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId: String, lastMessage: String, updatedAt: Long)
    
    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)
    
    @Query("DELETE FROM conversations")
    suspend fun deleteAllConversations()
}
