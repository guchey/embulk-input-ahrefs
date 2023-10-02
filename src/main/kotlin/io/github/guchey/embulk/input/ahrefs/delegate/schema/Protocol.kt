package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*
import kotlin.jvm.optionals.getOrNull

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

fun Optional<Protocol>.getNameOrNull(): String? = this.getOrNull()?.name?.lowercase(Locale.getDefault())