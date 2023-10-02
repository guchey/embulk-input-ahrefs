package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*
import kotlin.jvm.optionals.getOrNull

enum class SearchEngine {
    GOOGLE,
    YOUTUBE,
    AMAZON,
    BING,
    YAHOO,
    YANDEX,
    BAIDU,
    DAUM,
    NAVER,
    SEZNAM;

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun of(value: String): SearchEngine {
            return SearchEngine.valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}

fun Optional<SearchEngine>.getNameOrNull(): String? = this.getOrNull()?.name?.lowercase(Locale.getDefault())