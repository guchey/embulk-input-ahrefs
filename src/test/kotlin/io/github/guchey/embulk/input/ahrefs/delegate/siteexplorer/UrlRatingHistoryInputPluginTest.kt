package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test

class UrlRatingHistoryInputPluginTest {
    @Test
    fun checkDefaultConfig() {
        val config = AhrefsInputPlugin.CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "site_explorer_url_rating_history")
            .set("date_from", "2023-01-01")
            .set("target", "example.com")
        val task = AhrefsInputPlugin.CONFIG_MAPPER.map(config, UrlRatingHistoryInputPlugin.PluginTask::class.java)

        assertEquals(task.dateFrom.getOrNull(), "2023-01-01")
        assertEquals(task.target.getOrNull(), "example.com")
    }
}