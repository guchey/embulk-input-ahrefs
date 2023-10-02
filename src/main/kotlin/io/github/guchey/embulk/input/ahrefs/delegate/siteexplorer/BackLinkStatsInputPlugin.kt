package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Mode
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Protocol
import io.github.guchey.embulk.input.ahrefs.delegate.schema.getNameOrNull
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*


class BackLinkStatsInputPlugin<T : BackLinkStatsInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("mode")
        val mode: Optional<Mode>

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
        validateAndResolveFiled(task.date, "date")
        validateAndResolveFiled(task.target, "target")
        super.validateInputTask(task)
    }

    override fun buildRequest(task: T): Request {
        val queryParam = mapOf(
            "output" to "json",
            "mode" to task.mode.getNameOrNull(),
            "protocol" to task.protocol.getNameOrNull(),
            "date" to task.date.get(),
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.resolveAhrefsUrl()}/v3/site-explorer/backlinks-stats", queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", task.resolveAuthHeader())
            .build()
    }

    override fun transformJsonRecord(
        task: T,
        record: JsonNode
    ): JsonNode {
        return record.get("metrics")
    }

    override fun buildServiceResponseMapper(task: T): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("live", Types.LONG)
            .add("all_time", Types.LONG)
            .add("live_refdomains", Types.LONG)
            .add("all_time_refdomains", Types.LONG)
        return builder.build()
    }
}