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


class DomainRatingInputPlugin : AhrefsBaseDelegate<AhrefsInputPluginDelegate.PluginTask>() {
    interface PluginTask {
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

    override fun buildRequest(task: AhrefsInputPluginDelegate.PluginTask): Request {
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
        task: AhrefsInputPluginDelegate.PluginTask,
        record: JsonNode
    ): JsonNode {
        return record.get("domain_rating")
    }

    override fun buildServiceResponseMapper(task: AhrefsInputPluginDelegate.PluginTask): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("domain_rating", Types.DOUBLE)
            .add("ahrefs_rank", Types.LONG)
        return builder.build()
    }
}