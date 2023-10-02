package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin
import org.junit.jupiter.api.Assertions.*
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test

class PagesByTrafficInputPluginTest {
    @Test
    fun checkDefaultConfig() {
        val config = AhrefsInputPlugin.CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "site_explorer_pages_by_traffic")
            .set("target", "example.com")
        val task = AhrefsInputPlugin.CONFIG_MAPPER.map(config, PagesByTrafficInputPlugin.PluginTask::class.java)

        assertEquals(task.target.getOrNull(), "example.com")
    }
}