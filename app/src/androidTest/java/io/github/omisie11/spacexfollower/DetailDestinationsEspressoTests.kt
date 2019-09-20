package io.github.omisie11.spacexfollower

import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import io.github.omisie11.spacexfollower.ui.MainActivity
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesAdapter
import io.github.omisie11.spacexfollower.ui.cores.CoresAdapter
import io.github.omisie11.spacexfollower.ui.upcoming_launches.UpcomingLaunchesAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DetailDestinationsEspressoTests {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun openUpcomingLaunchesDetailFragment() {
        // Open drawer
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())
        // Start fragment
        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.upcoming_launches_dest))
        // Check if fragment appeared
        onView(withId(R.id.recycler_root_view))
            .check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<UpcomingLaunchesAdapter.ViewHolder>(1, click())
            )
        onView(withId(R.id.text_flight_number))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_mission_name))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_launch_date))
            .check(matches(isDisplayed()))
    }

    @Test
    fun openCapsulesDetailFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.capsules_dest))

        onView(withId(R.id.capsules_list_root_view))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<CapsulesAdapter.ViewHolder>(1, click())
            )
        onView(withId(R.id.text_capsule_serial))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_capsule_type))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_capsule_status))
            .check(matches(isDisplayed()))
    }

    @Test
    fun openCoresDetailFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.cores_dest))

        onView(withId(R.id.recycler_root_view))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<CoresAdapter.ViewHolder>(1, click())
            )
        onView(withId(R.id.text_core_serial))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_core_block))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_core_status))
            .check(matches(isDisplayed()))
    }
}