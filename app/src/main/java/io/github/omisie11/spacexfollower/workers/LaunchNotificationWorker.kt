package io.github.omisie11.spacexfollower.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.ui.MainActivity
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class LaunchNotificationWorker(
    context: Context,
    params: WorkerParameters
) :
    CoroutineWorker(context, params), KoinComponent {

    private val launchesDao: AllLaunchesDao by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val currentTime = System.currentTimeMillis() / 1000
        val launches = launchesDao.getLaunchesLaterThanDate(currentTime)
            .sortedBy { it.launchDateUnix }

        if (launches.isNullOrEmpty()) Result.retry()

        val nextLaunch: Launch = launches[0]
        val nextLaunchTime: Long = nextLaunch.launchDateUnix ?: 0L
        val missionName: String = nextLaunch.missionName
        val flightNumber: Int = nextLaunch.flightNumber

        if (nextLaunchTime == 0L || missionName.isBlank()) Result.retry()

        // check if next launch is in less than 24 hours
        if (nextLaunchTime - currentTime < NUMBER_OF_SECONDS_IN_24H) {
            Timber.d("nextLaunchTime: $nextLaunchTime, current time: $currentTime")
            triggerNotification(flightNumber, nextLaunchTime, missionName)
        }

        Result.success()
    }

    private fun triggerNotification(flightNumber: Int, nextLaunchTime: Long, missionName: String) {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_graph)
            .setDestination(R.id.launches_dest)
            .createPendingIntent()

        val contentText =
            applicationContext.getString(
                R.string.launch_notification_desc_template,
                missionName,
                getLocalTimeFromUnix(nextLaunchTime)
            )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm_24dp)
            .setContentTitle(
                applicationContext.resources.getString(
                    R.string.flight_number_template_in_less_than_24h,
                    flightNumber
                )
            )
            .setAutoCancel(true)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.resources.getString(R.string.launches_channel_name)
            val descriptionText =
                applicationContext.resources.getString(R.string.launches_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val NUMBER_OF_SECONDS_IN_24H = 86400
        private const val CHANNEL_ID = "launches_channel_01"
        private const val NOTIFICATION_ID = 100001
    }
}