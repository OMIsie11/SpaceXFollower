package io.github.omisie11.spacexfollower

import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.data.model.launch.LaunchSite
import io.github.omisie11.spacexfollower.data.model.launch.Rocket
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch

val capsule1 = Capsule(
    "C101", "dragon1", "retired",
    "2010-12-08T15:43:00.000Z", 1291822980,
    mutableListOf(Capsule.Mission("COTS 1", 7)), 0, "Dragon 1.0",
    "Reentered after three weeks in orbit", 0
)

val capsule2 = Capsule(
    "C102", "dragon1", "retired",
    "2012-05-02T07:44:00.000Z", 1335944640,
    mutableListOf(Capsule.Mission("COTS 2", 8)), 1, "Dragon 1.0",
    "First Dragon spacecraft", 0
)

val capsule3 = Capsule(
    "C103", "dragon1", "unknown",
    "2012-10-08T00:35:00.000Z", 1349656500,
    mutableListOf(Capsule.Mission("CRS-1", 9)), 1, "Dragon 1.0",
    "First of twenty missions flown under the CRS1 contract", 0
)

val core1 = Core(
    "Merlin1A", null, "expended", "2006-03-24T22:30:00.000Z",
    1143239400, mutableListOf(Capsule.Mission("FalconSat", 1)), 0,
    0,
    0, 0, 0, false,
    "Engine failure at T+33 seconds resulted in loss of vehicle"
)
val core2 = Core(
    "Merlin2A", null, "expended", "2007-03-21T01:10:00.000Z",
    1174439400, mutableListOf(Capsule.Mission("DemoSat", 2)), 0,
    0,
    0, 0, 0, false,
    "Successful first-stage burn and transition to second stage, maximal altitude 289 km. Harmonic" +
            " oscillation at T+5 minutes Premature engine shutdown at T+7 min 30 s. Failed to reach orbit."
)
val core3 = Core(
    "Merlin1C", null, "expended", "2008-08-02T03:34:00.000Z",
    1217648040, mutableListOf(Capsule.Mission("Trailblazer", 3)), 0,
    0,
    0, 0, 0, false,
    "Residual stage-1 thrust led to collision between stage 1 and stage 2."
)

val testCompanyInfo = Company(
    1, "SpaceX", "Elon Musk", 2002, 7000, 3,
    3, 1, "Elon Musk", "Elon Musk", "Gwynne Shotwell",
    "Tom Mueller", 15000000000, Company.Headquarters(
        "Rocket Road", "Hawthorne",
        "California"
    ), "SpaceX designs, manufactures and launches advanced rockets and " +
            "spacecraft. The company was founded in 2002 to revolutionize space technology, with the ultimate " +
            "goal of enabling people to live on other planets."
)

val launch1 = UpcomingLaunch(
    75, "Nusantara Satu (PSN-6) / GTO-1 / Beresheet", null,
    1550799900, false,
    LaunchSite(
        "ccafs_slc_40", "ccafs_slc_40", "Cape Canaveral Air Force Station Space " +
                "Launch Complex 40"
    ), Rocket(
        "falcon9", "Falcon 9", "FT",
        Rocket.FirstStage(
            listOf(
                Rocket.Core(
                    "B1048", 3, 4, gridfins = true, legs = true, reused = true,
                    land_success = null, landing_intent = true, landing_type = "ASDS", landing_vehicle = "OCISLY"
                )
            )
        ), Rocket.SecondStage(
            5, listOf(
                Rocket.Payload(
                    "Nusantara Satu (PSN-6)",
                    false, listOf("Pasifik Satelit Nusantara"), "Indonesia", "SSL",
                    "Satellite", 5000, 11023.11, "GTO"
                )
            )
        )
    ), null
)

val launch2 = UpcomingLaunch(
    76, "CCtCap Demo Mission 1", mutableListOf("EE86F74"),
    1551512700, false,
    LaunchSite(
        "ksc_lc_39a", "KSC LC 39A", "Kennedy Space Center Historic Launch Complex 39A"
    ), Rocket(
        "falcon9", "Falcon 9", "FT",
        Rocket.FirstStage(
            listOf(
                Rocket.Core(
                    "B1051", 1, 5, gridfins = false, legs = true, reused = false,
                    land_success = null, landing_intent = true, landing_type = "ASDS", landing_vehicle = "OCISLY"
                )
            )
        ), Rocket.SecondStage(
            5, listOf(
                Rocket.Payload(
                    "CCtCap Demo Mission 1", false,
                    listOf("NASA (CCtCap)"), "United States", "SpaceX", "Crew Dragon",
                    null, null, "ISS"
                )
            )
        )
    ), "Demonstration mission to ISS for NASA with an uncrewed Dragon 2 capsule."
)