package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

enum class Protocol {
    BOTH,
    HTTP,
    HTTPS;

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun of(value: String): Protocol {
            return Protocol.valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}