package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Protocol
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*
import kotlin.jvm.optionals.getOrNull


class DomainRatingInputPlugin<T: DomainRatingInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("protocol")
        val protocol: Optional<Protocol>

        @get:ConfigDefault("null")
        @get:Config("date")
        val date: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("target")
        val target: Optional<String>
    }


    override fun validateInputTask(task: T) {
        require(task.date.isPresent)
        require(task.target.isPresent)
        super.validateInputTask(task)
    }
    override fun buildRequest(task: T): Request {
        val queryParam = mapOf(
            "output" to "json",
            "protocol" to task.protocol.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "date" to task.date.get(),
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("https://api.ahrefs.com/v3/site-explorer/domain-rating", queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${task.apiKey}")
            .build()
    }

    override fun transformJsonRecord(
        task: T,
        record: JsonNode
    ): JsonNode {
        return record.get("domain_rating")
    }

    override fun buildServiceResponseMapper(task: T): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("domain_rating", Types.DOUBLE)
            .add("ahrefs_rank", Types.LONG)
        return builder.build()
    }
}