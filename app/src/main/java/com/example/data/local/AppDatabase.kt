package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PillarMasteryEntity::class,
        ErrorPatternEntity::class,
        SrsCardStateEntity::class,
        DiagnosticSessionLogEntity::class,
        ControllerConfigEntity::class,
        MistakeLedgerEntity::class,
        PrunedContentEntity::class,
        AiGeneratedSlideEntity::class,
        AiGeneratedVocabEntity::class,
        AiGeneratedQuizEntity::class,
        RepoUnitProgressEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosticDao(): DiagnosticDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "german_c1_diagnostic.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
