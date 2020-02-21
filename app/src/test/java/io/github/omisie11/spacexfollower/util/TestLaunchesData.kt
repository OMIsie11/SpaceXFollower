package io.github.omisie11.spacexfollower.util

import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import io.github.omisie11.spacexfollower.data.local.model.launch.LaunchSite
import io.github.omisie11.spacexfollower.data.local.model.launch.Rocket

val testLaunch1 = Launch(
    75, "Nusantara Satu (PSN-6) / GTO-1 / Beresheet", null,
    1550799900, false,
    LaunchSite(
        "ccafs_slc_40", "ccafs_slc_40", "Cape Canaveral Air Force Station Space " +
                "Launch Complex 40"
    ), Rocket(
        "falcon9",
        "Falcon 9",
        "FT",
        Rocket.FirstStage(
            listOf(
                Rocket.Core(
                    "B1048",
                    3,
                    4,
                    gridfins = true,
                    legs = true,
                    reused = true,
                    land_success = null,
                    landing_intent = true,
                    landing_type = "ASDS",
                    landing_vehicle = "OCISLY"
                )
            )
        ),
        Rocket.SecondStage(
            5, listOf(
                Rocket.Payload(
                    "Nusantara Satu (PSN-6)",
                    false, listOf("Pasifik Satelit Nusantara"), "Indonesia", "SSL",
                    "Satellite", 5000.0, 11023.11, "GTO"
                )
            )
        )
    ), Launch.Links(
        "", "", "", "",
        "", "", "", "", "", ""
    ),
    "No details"
)

val testLaunch2 = Launch(
    76, "CCtCap Demo Mission 1", mutableListOf("EE86F74"),
    1551512700, false,
    LaunchSite(
        "ksc_lc_39a", "KSC LC 39A", "Kennedy Space Center Historic Launch Complex 39A"
    ), Rocket(
        "falcon9",
        "Falcon 9",
        "FT",
        Rocket.FirstStage(
            listOf(
                Rocket.Core(
                    "B1051",
                    1,
                    5,
                    gridfins = false,
                    legs = true,
                    reused = false,
                    land_success = null,
                    landing_intent = true,
                    landing_type = "ASDS",
                    landing_vehicle = "OCISLY"
                )
            )
        ),
        Rocket.SecondStage(
            5, listOf(
                Rocket.Payload(
                    "CCtCap Demo Mission 1", false,
                    listOf("NASA (CCtCap)"), "United States", "SpaceX", "Crew Dragon",
                    null, null, "ISS"
                )
            )
        )
    ), Launch.Links(
        "", "", "", "",
        "", "", "", "", "", ""
    ), "Demonstration mission to ISS for NASA with an uncrewed Dragon 2 capsule."
)