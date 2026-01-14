package com.librio

import android.app.Application
import java.io.File

class LibrioApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: LibrioApplication

        /**
         * Returns the Librio root folder in app-specific external storage.
         * Location: /Android/data/com.librio.android/files/Librio/
         * This location requires no permissions and survives app updates.
         */
        fun getLibrioRoot(): File {
            val root = File(instance.getExternalFilesDir(null), "Librio")
            if (!root.exists()) {
                root.mkdirs()
            }
            return root
        }
    }
}
