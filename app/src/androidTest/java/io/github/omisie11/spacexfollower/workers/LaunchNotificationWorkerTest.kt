package io.github.omisie11.spacexfollower.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchNotificationWorkerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testLaunchNotificationWorker() {
        val worker = TestListenableWorkerBuilder<LaunchNotificationWorker>(context).build()

        runBlocking {
            val result: ListenableWorker.Result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}