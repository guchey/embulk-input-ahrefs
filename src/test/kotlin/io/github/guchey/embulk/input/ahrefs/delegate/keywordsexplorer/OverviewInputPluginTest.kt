package io.github.guchey.embulk.input.ahrefs.delegate.keywordsexplorer

import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test

class OverviewInputPluginTest {
    @Test
    fun checkDefaultConfig() {
        val config = AhrefsInputPlugin.CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "keyword_explorer_overview")
            .set("country", "jp")
            .set("select", "a,b,c")
        val task = AhrefsInputPlugin.CONFIG_MAPPER.map(config, OverviewInputPlugin.PluginTask::class.java)

        assertEquals(task.country.getOrNull()?.name?.lowercase(), "jp")
        assertEquals(task.select.getOrNull(), "a,b,c")
    }
}