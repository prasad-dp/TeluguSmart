package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.KeyboardRepository

class TeluguSmartApplication : Application() {

    companion object {
        lateinit var instance: TeluguSmartApplication
            private set
    }

    lateinit var database: AppDatabase
        private set

    lateinit var repository: KeyboardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        repository = KeyboardRepository(
            context = this,
            userWordDao = database.userWordDao(),
            clipboardDao = database.clipboardDao()
        )
    }
}
