package com.thesubgraph.askstack.features.assistant.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.thesubgraph.askstack.features.assistant.data.local.database.dao.ConversationDao
import com.thesubgraph.askstack.features.assistant.data.local.database.dao.MessageDao
import com.thesubgraph.askstack.features.assistant.data.local.database.entities.ConversationEntity
import com.thesubgraph.askstack.features.assistant.data.local.database.entities.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    
    companion object {
        const val DATABASE_NAME = "chat_database"
        
        fun create(context: Context): ChatDatabase {
            return Room.databaseBuilder(
                context,
                ChatDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
