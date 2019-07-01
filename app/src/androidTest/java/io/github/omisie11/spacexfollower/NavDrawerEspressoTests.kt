package io.github.omisie11.spacexfollower

import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import io.github.omisie11.spacexfollower.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavDrawerEspressoTests {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickOnNavDrawerOpenUpcomingLaunchesFragment() {
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
    }

    @Test
    fun clickOnNavDrawerOpenCapsulesFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.capsules_dest))

        onView(withId(R.id.capsules_list_root_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavDrawerOpenCoresFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.cores_dest))

        onView(withId(R.id.recycler_root_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavDrawerOpenCompanyFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.company_dest))
        // (swipeRefreshLayout is root in that fragment)
        onView(withId(R.id.swipeRefreshLayout))
            .check(matches(isDisplayed()))
    }
}