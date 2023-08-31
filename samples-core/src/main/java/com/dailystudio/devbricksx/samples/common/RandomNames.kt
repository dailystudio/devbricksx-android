package com.dailystudio.devbricksx.samples.common

import kotlin.random.Random

object RandomNames {
    private val LAST_NAMES = arrayOf(
        "Kelley", "Carter", "Salazar", "Osborne", "Santiago",
        "Luna", "Wilson", "Craig", "Willis", "Abeln"
    )

    private val FIRST_NAMES = arrayOf(
        "Tanya", "Miriam", "Julie", "Leona", "Mabel",
        "Emelia", "Sofia", "Haleema", "Francis", "Elin"
    )

    private val RANDOM = Random(System.currentTimeMillis())

    fun nextLastName(): String {
        val lIndex = RANDOM.nextInt(LAST_NAMES.size)

        return LAST_NAMES[lIndex]
    }

    fun nextFirstName(): String {
        val fIndex = RANDOM.nextInt(FIRST_NAMES.size)

        return FIRST_NAMES[fIndex]
    }

    fun nextName(upperLastName: Boolean = true): String {
        return buildString {
            val last = nextLastName()
            val first = nextFirstName()

            append(first)
            append(' ')
            append(if (upperLastName) {
                last.uppercase()
            } else {
                last
            })
        }
    }
}