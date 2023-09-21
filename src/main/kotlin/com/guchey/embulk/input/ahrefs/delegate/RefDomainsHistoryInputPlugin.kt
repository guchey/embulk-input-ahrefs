package com.guchey.embulk.input.ahrefs.delegate

import com.fasterxml.jackson.databind.JsonNode
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import org.embulk.util.config.Task


class RefDomainsHistoryInputPlugin : AhrefsBaseDelegate() {
    interface PluginTask : Task {
        @get:Config("date_to")
        val dateTo: String

        @get:ConfigDefault("\"monthly\"")
        @get:Config("history_grouping")
        val historyGrouping: String

        @get:ConfigDefault("\"subdomains\"")
        @get:Config("mode")
        val mode: String

        @get:ConfigDefault("\"both\"")
        @get:Config("protocol")
        val protocol: String

        @get:Config("date_from")
        val dateFrom: String

        @get:Config("target")
        val target: String
    }

    override fun buildRequest(task: com.guchey.embulk.input.ahrefs.config.PluginTask): Request {
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
        task: com.guchey.embulk.input.ahrefs.config.PluginTask,
        record: JsonNode
    ): JsonNode {
        return record.get("refdomains")
    }

    override fun buildServiceResponseMapper(task: com.guchey.embulk.input.ahrefs.config.PluginTask): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("date", Types.STRING)
            .add("refdomains", Types.LONG)
        return builder.build()
    }
}