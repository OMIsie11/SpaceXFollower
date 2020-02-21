package io.github.omisie11.spacexfollower.util

import io.github.omisie11.spacexfollower.data.local.model.Capsule

val testCapsule1 = Capsule(
    1L, "C101", "dragon1", "retired",
    "2010-12-08T15:43:00.000Z", 1291822980,
    mutableListOf(
        Capsule.Mission(
            "COTS 1",
            7
        )
    ), 0, "Dragon 1.0",
    "Reentered after three weeks in orbit", 0
)

val testCapsule2 = Capsule(
    2L, "C102", "dragon1", "retired",
    "2012-05-02T07:44:00.000Z", 1335944640,
    mutableListOf(
        Capsule.Mission(
            "COTS 2",
            8
        )
    ), 1, "Dragon 1.0",
    "First Dragon spacecraft", 0
)

val testCapsule3 = Capsule(
    3L, "C103", "dragon1", "unknown",
    "2012-10-08T00:35:00.000Z", 1349656500,
    mutableListOf(
        Capsule.Mission(
            "CRS-1",
            9
        )
    ), 1, "Dragon 1.0",
    "First of twenty missions flown under the CRS1 contract", 0
)
