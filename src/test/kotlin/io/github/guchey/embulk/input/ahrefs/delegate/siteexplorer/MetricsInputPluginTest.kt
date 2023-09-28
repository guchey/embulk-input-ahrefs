package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test

class MetricsInputPluginTest {
    @Test
    fun checkDefaultConfig() {
        val config = AhrefsInputPlugin.CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "site_explorer_metrics")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = AhrefsInputPlugin.CONFIG_MAPPER.map(config, MetricsInputPlugin.PluginTask::class.java)

        assertEquals(task.date.getOrNull(), "2023-01-01")
        assertEquals(task.target.getOrNull(), "example.com")
    }
}