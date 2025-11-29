package com.example.jobify

import android.app.Application
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class MyApp : Application() {
    companion object {
        private const val TAG = "MyApp"
        const val CRASH_FILE = "last_crash.txt"
        // Expose application instance for global access (use carefully)
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Install default uncaught exception handler that writes stack trace to cache file
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val trace = "Thread: ${thread.name}\nException: ${throwable::class.java.name}\n\n${sw}"
                val outFile = File(cacheDir, CRASH_FILE)
                outFile.writeText(trace)
                Log.e(TAG, "Wrote crash trace to ${outFile.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write crash file", e)
            } finally {
                // give system a moment to flush logs, then delegate to default handler or kill process
                try {
                    Thread.sleep(200)
                } catch (_: InterruptedException) {}
                // Delegate to default handler if present (so system crash dialog & tombstone behave normally)
                defaultHandler?.uncaughtException(thread, throwable)
                // If default handler returns (unlikely), kill process
                try {
                    exitProcess(2)
                } catch (_: Throwable) {}
            }
        }
    }
}
