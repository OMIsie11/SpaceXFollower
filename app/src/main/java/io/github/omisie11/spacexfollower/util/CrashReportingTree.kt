package io.github.omisie11.spacexfollower.util

import android.util.Log
import timber.log.Timber

/**
 * Class used by Timber for logging in debug builds
 */
class CrashReportingTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.INFO

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        // Implement crash reporting (Crashlytics) here in future
    }
}