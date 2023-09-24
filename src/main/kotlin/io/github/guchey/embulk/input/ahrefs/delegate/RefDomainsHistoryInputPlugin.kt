package io.github.guchey.embulk.input.ahrefs.delegate

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.AhrefsInputPluginDelegate
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*


class RefDomainsHistoryInputPlugin : AhrefsBaseDelegate<AhrefsInputPluginDelegate.PluginTask>() {
    interface PluginTask {
        @get:ConfigDefault("null")
        @get:Config("date_to")
        val dateTo: Optional<String>

        @get:ConfigDefault("\"monthly\"")
        @get:Config("history_grouping")
        val historyGrouping: String

        @get:ConfigDefault("\"subdomains\"")
        @get:Config("mode")
        val mode: String

        @get:ConfigDefault("\"both\"")
        @get:Config("protocol")
        val protocol: String

        @get:ConfigDefault("null")
        @get:Config("date_from")
        val dateFrom: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("target")
        val target: Optional<String>
    }

    override fun buildRequest(task: AhrefsInputPluginDelegate.PluginTask): Request {
        val queryParam = mapOf(
            "output" to "json",
            "date_to" to task.dateTo,
            "history_grouping" to task.historyGrouping,
            "mode" to task.mode,
            "protocol" to task.protocol,
            "date_from" to task.dateFrom,
            "target" to task.target
        )
        val query = queryParam.entries.joinToString("&") { "${it.key}=${it.value}" }
        return Request.Builder()
            .url("https://api.ahrefs.com/v3/site-explorer/refdomains-history?${query}")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${task.apiKey}")
            .build()
    }

    override fun transformJsonRecord(
        task: AhrefsInputPluginDelegate.PluginTask,
        record: JsonNode
    ): JsonNode {
        return record.get("refdomains")
    }

    override fun buildServiceResponseMapper(task: AhrefsInputPluginDelegate.PluginTask): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("date", Types.STRING)
            .add("refdomains", Types.LONG)
        return builder.build()
    }
}