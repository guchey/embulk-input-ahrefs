package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

enum class VolumeMode {
    MONTHLY,
    AVERAGE;

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun of(value: String): VolumeMode {
            return VolumeMode.valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}