package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

enum class HistoryGrouping {
    DAILY,
    WEEKLY,
    MONTHLY;

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun of(value: String): HistoryGrouping {
            return HistoryGrouping.valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}
