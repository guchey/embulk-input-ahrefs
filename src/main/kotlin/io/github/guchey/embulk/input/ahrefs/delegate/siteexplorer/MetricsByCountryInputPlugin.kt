package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Mode
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Protocol
import io.github.guchey.embulk.input.ahrefs.delegate.schema.VolumeMode
import io.github.guchey.embulk.input.ahrefs.delegate.schema.getNameOrNull
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*
import kotlin.jvm.optionals.getOrNull


class MetricsByCountryInputPlugin<T : MetricsByCountryInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("limit")
        val limit: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("mode")
        val mode: Optional<Mode>

        @get:ConfigDefault("null")
        @get:Config("offset")
        val offset: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("protocol")
        val protocol: Optional<Protocol>

        @get:ConfigDefault("null")
        @get:Config("volume_mode")
        val volumeMode: Optional<VolumeMode>

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
            "limit" to task.limit.getOrNull(),
            "mode" to task.mode.getNameOrNull(),
            "offset" to task.offset.getOrNull(),
            "protocol" to task.protocol.getNameOrNull(),
            "volume_mode" to task.volumeMode.getNameOrNull(),
            "date" to task.date.get(),
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.resolveAhrefsUrl()}/v3/site-explorer/metrics-by-country", queryParam))
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
            .add("country", Types.STRING)
            .add("country", Types.LONG)
            .add("org_traffic", Types.LONG)
            .add("org_cost", Types.LONG)
            .add("org_keywords", Types.LONG)
            .add("org_keywords_1_3", Types.LONG)
            .add("paid_traffic", Types.LONG)
            .add("paid_cost", Types.LONG)
            .add("paid_keywords", Types.LONG)
            .add("paid_pages", Types.LONG)
        return builder.build()
    }
}