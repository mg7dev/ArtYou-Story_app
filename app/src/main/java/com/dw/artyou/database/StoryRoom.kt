package com.dw.artyou.database

import android.content.ClipDescription
import android.content.Context
import androidx.room.*
import com.dw.artyou.fragments.FragmentStoryList

@Entity
data class story(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "segment") val segment: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val profile: ByteArray?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val cover: ByteArray?


)

@Dao
interface storyDao {
    @Query("SELECT * FROM story")
    fun getAll(): List<story>


    @Insert
    fun insertStory(vararg storys: story)


    @Query("DELETE FROM story")
    fun deleteAll()
}

@Database(entities = arrayOf(story::class), version = 1)
abstract class StoryRoom : RoomDatabase() {
    abstract fun storyDao(): storyDao

    companion object {
        @Volatile
        private var instance: StoryRoom? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            StoryRoom::class.java, FragmentStoryList.DATABSE_NAME
        )
            .build()
    }
}