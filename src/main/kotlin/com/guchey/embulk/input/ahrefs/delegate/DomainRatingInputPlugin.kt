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


class DomainRatingInputPlugin : AhrefsBaseDelegate() {
    interface PluginTask : Task {
        @get:ConfigDefault("\"both\"")
        @get:Config("protocol")
        val protocol: String

        @get:Config("date")
        val date: String

        @get:Config("target")
        val target: String
    }

    override fun buildRequest(task: com.guchey.embulk.input.ahrefs.config.PluginTask): Request {
        val queryParam = mapOf(
            "output" to "json",
            "protocol" to task.protocol,
            "date" to task.date,
            "target" to task.target
        )
        val query = queryParam.entries.joinToString("&") { "${it.key}=${it.value}" }
        return Request.Builder()
            .url("https://api.ahrefs.com/v3/site-explorer/domain-rating?${query}")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${task.apiKey}")
            .build()
    }

    override fun transformJsonRecord(
        task: com.guchey.embulk.input.ahrefs.config.PluginTask,
        record: JsonNode
    ): JsonNode {
        return record.get("domain_rating")
    }

    override fun buildServiceResponseMapper(task: com.guchey.embulk.input.ahrefs.config.PluginTask): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("domain_rating", Types.DOUBLE)
            .add("ahrefs_rank", Types.LONG)
        return builder.build()
    }
}