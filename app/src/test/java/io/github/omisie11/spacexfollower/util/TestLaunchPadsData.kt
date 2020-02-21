package io.github.omisie11.spacexfollower.util

import io.github.omisie11.spacexfollower.data.local.model.LaunchPad

val testLaunchPad1 = LaunchPad(
    6, "active",
    LaunchPad.Location(
        "Vandenberg Air Force Base", "California", 34.632093,
        -120.610829
    ),
    listOf("Falcon 9"), 12, 12,
    "https://en.wikipedia.org/wiki/Vandenberg_AFB_Space_Launch_Complex_4",
    "SpaceX primary west coast launch pad for polar orbits and sun synchronous orbits.",
    "vafb_slc_4e", "Vandenberg Air Force Base Space Launch Complex 4E"
)

val testLaunchPad2 = LaunchPad(
    1, "retired",
    LaunchPad.Location(
        "Omelek Island", "Marshall Islands", 9.0477206,
        167.7431292
    ),
    listOf("Falcon 1"), 5, 2,
    "https://en.wikipedia.org/wiki/Omelek_Island",
    "SpaceX original launch site, where all of the Falcon 1 launches occured.",
    "kwajalein_atoll", "Kwajalein Atoll Omelek Island"
)