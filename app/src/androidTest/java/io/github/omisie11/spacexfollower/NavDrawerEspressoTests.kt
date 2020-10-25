package io.github.omisie11.spacexfollower

import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
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
    fun clickOnNavDrawerOpenDashboardFragment() {
        // Open drawer
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())
        // Start fragment
        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.dashboard_dest))
        // Check if title text view is present
        onView(withId(R.id.text_launches_chart_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavDrawerOpenAllLaunchesFragment() {
        // Open drawer
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())
        // Start fragment
        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.launches_dest))
        // Check if fragment appeared
        onView(withId(R.id.root_recycler_sorting))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavDrawerOpenCapsulesFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.capsules_dest))

        onView(withId(R.id.root_recycler_sorting))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavDrawerOpenCoresFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.cores_dest))

        onView(withId(R.id.root_recycler_sorting))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavDrawerOpenLaunchPadsFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.launch_pads_dest))

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

    @Test
    fun clickOnNavDrawerOpenSettingsFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.settings_dest))

        // Check if settings are present by trying to click on each one
        // RecyclerView actions are used because normal approach is not
        // working with preferences from support lib
        onView(withId(R.id.recycler_view))
            .check(matches(hasDescendant(withText(R.string.settings_dark_mode_title))))

        onView(withId(R.id.recycler_view))
            .check(matches(hasDescendant(withText(R.string.settings_refresh_interval_title))))

        onView(withId(R.id.recycler_view))
            .check(matches(hasDescendant(withText(R.string.upcoming_launches_notifications))))
    }

    @Test
    fun clickOnNavDrawerOpenAboutFragment() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.about_dest))
        onView(withId(R.id.root_layout))
            .check(matches(isDisplayed()))
    }
}
