package io.github.guchey.embulk.input.ahrefs

import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.CONFIG_MAPPER
import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.CONFIG_MAPPER_FACTORY
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test


class AhrefsInputPluginTest {

    @Test
    fun checkDefaultValues() {
        val config = CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "domain_rating")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = CONFIG_MAPPER.map(config, AhrefsInputPluginDelegate.PluginTask::class.java)

        assertEquals(task.retry.condition, "true")
        assertEquals(task.retry.maxRetries, 7)
        assertEquals(task.retry.initialIntervalMillis, 1000)
        assertEquals(task.retry.maxIntervalMillis, 60000)
    }

    @Test
    fun checkDomainRating() {
        val config = CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "domain_rating")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = CONFIG_MAPPER.map(config, AhrefsInputPluginDelegate.PluginTask::class.java)

        assertEquals(task.protocol, "both")
    }

    @Test
    fun checkBackLinkStats() {
        val config = CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", "dummy")
            .set("resource", "backlink_stats")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = CONFIG_MAPPER.map(config, AhrefsInputPluginDelegate.PluginTask::class.java)

        assertEquals(task.mode, "subdomains")
        assertEquals(task.protocol, "both")
    }
}