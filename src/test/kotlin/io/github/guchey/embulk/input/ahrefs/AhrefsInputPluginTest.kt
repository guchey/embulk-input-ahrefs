package io.github.guchey.embulk.input.ahrefs

import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.CONFIG_MAPPER
import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.CONFIG_MAPPER_FACTORY
import io.github.guchey.embulk.input.ahrefs.config.PluginTask
import org.embulk.EmbulkSystemProperties
import org.junit.Assume.assumeNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import java.util.*
import kotlin.test.Test


class AhrefsInputPluginTest {

    companion object {
        private var API_KEY: String? = null
        private var EMBULK_SYSTEM_PROPERTIES: EmbulkSystemProperties? = null

        init {
            val properties = Properties();
            properties.setProperty("default_guess_plugins", "gzip,bzip2,json,csv");
            EMBULK_SYSTEM_PROPERTIES = EmbulkSystemProperties.of(properties);
        }

        @JvmStatic
        @BeforeAll
        fun initializeConstant(): Unit {
            val apiKey = System.getenv("AHREFS_API_KEY")

            // skip test cases, if environment variables are not set.
            assumeNotNull(apiKey)

            API_KEY = apiKey
        }
    }

    @Test
    fun checkDefaultValues() {
        val config = CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", API_KEY)
            .set("resource", "domain_rating")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = CONFIG_MAPPER.map(config, PluginTask::class.java)

        assertEquals(task.retry.condition, "true")
        assertEquals(task.retry.maxRetries, 7)
        assertEquals(task.retry.initialIntervalMillis, 1000)
        assertEquals(task.retry.maxIntervalMillis, 60000)
    }

    @Test
    fun checkDomainRating() {
        val config = CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", API_KEY)
            .set("resource", "domain_rating")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = CONFIG_MAPPER.map(config, PluginTask::class.java)

        assertEquals(task.protocol, "both")
    }

    @Test
    fun checkBackLinkStats() {
        val config = CONFIG_MAPPER_FACTORY.newConfigSource()
            .set("api_key", API_KEY)
            .set("resource", "backlink_stats")
            .set("date", "2023-01-01")
            .set("target", "example.com")
        val task = CONFIG_MAPPER.map(config, PluginTask::class.java)

        assertEquals(task.mode, "subdomains")
        assertEquals(task.protocol, "both")
    }
}