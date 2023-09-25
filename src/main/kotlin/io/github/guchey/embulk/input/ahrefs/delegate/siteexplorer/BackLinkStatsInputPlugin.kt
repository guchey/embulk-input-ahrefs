package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*


class BackLinkStatsInputPlugin : AhrefsBaseDelegate<BackLinkStatsInputPlugin.PluginTask>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("\"subdomains\"")
        @get:Config("mode")
        val mode: String

        @get:ConfigDefault("\"both\"")
        @get:Config("protocol")
        val protocol: String

        @get:ConfigDefault("null")
        @get:Config("date")
        val date: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("target")
        val target: Optional<String>
    }

    override fun buildRequest(task: PluginTask): Request {
        val queryParam = mapOf(
            "output" to "json",
            "mode" to task.mode,
            "protocol" to task.protocol,
            "date" to task.date,
            "target" to task.target
        )
        val query = queryParam.entries.joinToString("&") { "${it.key}=${it.value}" }
        return Request.Builder()
            .url("https://api.ahrefs.com/v3/site-explorer/backlinks-stats?${query}")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${task.apiKey}")
            .build()
    }

    override fun transformJsonRecord(
        task: PluginTask,
        record: JsonNode
    ): JsonNode {
        return record.get("metrics")
    }

    override fun buildServiceResponseMapper(task: PluginTask): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("live", Types.LONG)
            .add("all_time", Types.LONG)
            .add("live_refdomains", Types.LONG)
            .add("all_time_refdomains", Types.LONG)
        return builder.build()
    }
}