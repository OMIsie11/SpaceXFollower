package io.github.omisie11.spacexfollower.test_utils

import com.github.mikephil.charting.data.PieEntry
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.local.model.Core

val testCore1 = Core(
    1L,
    "Merlin1A",
    null,
    "expended",
    "2006-03-24T22:30:00.000Z",
    1143239400,
    mutableListOf(
        Capsule.Mission(
            "FalconSat",
            1
        )
    ),
    0,
    0,
    0,
    0,
    0,
    false,
    "Engine failure at T+33 seconds resulted in loss of vehicle"
)
val testCore2 = Core(
    2L,
    "Merlin2A",
    null,
    "expended",
    "2007-03-21T01:10:00.000Z",
    1174439400,
    mutableListOf(
        Capsule.Mission(
            "DemoSat",
            2
        )
    ),
    0,
    0,
    0,
    0,
    0,
    false,
    "Successful first-stage burn and transition to second stage, maximal altitude 289 km." +
            " Harmonic oscillation at T+5 minutes Premature engine shutdown at T+7 min 30 s." +
            " Failed to reach orbit."
)
val testCore3 = Core(
    3L,
    "Merlin1C",
    null,
    "expended",
    "2008-08-02T03:34:00.000Z",
    1217648040,
    mutableListOf(
        Capsule.Mission(
            "Trailblazer",
            3
        )
    ),
    0,
    0,
    0,
    0,
    0,
    false,
    "Residual stage-1 thrust led to collision between stage 1 and stage 2."
)

/**
 * List of entries that will be constructed by DashboardRepository with use of
 * testCore1, testCore2 and testCore3
 */
val testCoresPieEntriesList = listOf(
    PieEntry(3f, "expended")
)