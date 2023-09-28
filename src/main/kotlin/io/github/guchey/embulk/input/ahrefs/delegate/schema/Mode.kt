package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

enum class Mode {
    EXACT,
    PREFIX,
    DOMAIN,
    SUBDOMAINS;

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun of(value: String): Mode {
            return Mode.valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}