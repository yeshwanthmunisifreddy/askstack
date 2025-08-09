package com.thesubgraph.askstack.base.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


object DateUtils {
    const val PATTERN_FULL_DATE_TIME = "dd-MM-yyyy HH:mm:ss"
    const val PATTERN_DATE_ONLY = "dd-MM-yyyy"
    const val PATTERN_TIME_ONLY = "HH:mm:ss"
    const val PATTERN_SHORT_DATE_TIME = "dd MMM, yyyy HH:mm"
    const val PATTERN_READABLE_DATE = "dd MMMM, yyyy"
    const val PATTERN_READABLE_DATE_TIME = "dd MMMM, yyyy 'at' HH:mm"

    @OptIn(ExperimentalTime::class)
    fun convertTimestampToLocalDateTime(timestamp: Long): LocalDateTime {
        return Instant.fromEpochSeconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    }


    fun formatDateTime(dateTime: LocalDateTime?, pattern: String = PATTERN_SHORT_DATE_TIME): String {
        return dateTime?.let {
            when (pattern) {
                PATTERN_FULL_DATE_TIME -> {
                    val format = LocalDateTime.Format {
                        day(); char('-'); monthNumber(); char('-'); year(); char(' ');
                        hour(); char(':'); minute(); char(':'); second()
                    }
                    it.format(format)
                }
                PATTERN_DATE_ONLY -> {
                    val format = LocalDateTime.Format {
                        day(); char('-'); monthNumber(); char('-'); year()
                    }
                    it.format(format)
                }
                PATTERN_TIME_ONLY -> {
                    val format = LocalDateTime.Format {
                        hour(); char(':'); minute(); char(':'); second()
                    }
                    it.format(format)
                }
                PATTERN_SHORT_DATE_TIME -> {
                    val format = LocalDateTime.Format {
                        day(); char(' '); monthName(MonthNames.ENGLISH_ABBREVIATED);
                        chars(", "); year(); char(' '); hour(); char(':'); minute()
                    }
                    it.format(format)
                }
                PATTERN_READABLE_DATE -> {
                    val format = LocalDateTime.Format {
                        day(); char(' '); monthName(MonthNames.ENGLISH_FULL);
                        chars(", "); year()
                    }
                    it.format(format)
                }
                PATTERN_READABLE_DATE_TIME -> {
                    val format = LocalDateTime.Format {
                        day(); char(' '); monthName(MonthNames.ENGLISH_FULL);
                        chars(", "); year(); chars(" at "); hour(); char(':'); minute()
                    }
                    it.format(format)
                }
                else -> it.toString()
            }
        } ?: ""
    }
}