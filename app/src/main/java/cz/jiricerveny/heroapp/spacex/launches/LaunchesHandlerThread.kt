package cz.jiricerveny.heroapp.spacex.launches

import android.os.Handler
import android.os.HandlerThread
import android.os.Process


private const val TAG = "LaunchesHandlerThread"

class LaunchesHandlerThread() :
    HandlerThread("LaunchesHandlerThread", Process.THREAD_PRIORITY_BACKGROUND) {
    private lateinit var handler: Handler

    override fun onLooperPrepared() {
        handler = Handler()
    }

    fun getHandler(): Handler {
        return handler
    }
}