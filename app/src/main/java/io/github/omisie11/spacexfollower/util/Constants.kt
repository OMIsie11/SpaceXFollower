package io.github.omisie11.spacexfollower.util

// Base URL for API
const val SPACE_X_BASE_URL = "https://api.spacexdata.com/"

// Shared preferences keys
// Keys for getting time of last data refresh
const val KEY_CAPSULES_LAST_REFRESH = "key_capsules_last_refresh"
const val KEY_CORES_LAST_REFRESH = "key_cores_last_refresh"
const val KEY_COMPANY_LAST_REFRESH = "key_company_last_refresh"
const val KEY_NEXT_LAUNCH_LAST_REFRESH = "key_next_launch_last_refresh"
const val KEY_UPCOMING_LAUNCHES_LAST_REFRESH = "key_upcoming_launches_last_refresh"

const val PREFS_KEY_REFRESH_INTERVAL = "prefs_refresh_interval"
const val PREFS_KEY_DARK_MODE = "prefs_dark_mode"

// Sort types for data in ViewModel
const val CAPSULES_SORT_SERIAL_ASC = "capsules_sort_serial_asc"
const val CAPSULES_SORT_SERIAL_DESC = "capsules_sort_serial_desc"