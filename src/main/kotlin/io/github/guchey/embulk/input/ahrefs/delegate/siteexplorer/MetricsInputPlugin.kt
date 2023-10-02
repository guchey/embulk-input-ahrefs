package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.*
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*
import kotlin.jvm.optionals.getOrNull


class MetricsInputPlugin<T : MetricsInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("country")
        val country: Optional<Country>

        @get:ConfigDefault("null")
        @get:Config("mode")
        val mode: Optional<Mode>

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
            "country" to task.country.getNameOrNull(),
            "mode" to task.mode.getNameOrNull(),
            "protocol" to task.protocol.getNameOrNull(),
            "volume_mode" to task.volumeMode.getNameOrNull(),
            "date" to task.date.get(),
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.getAhrefsUrl()}/v3/site-explorer/metrics",queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", task.getAuthHeader())
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
            .add("org_keywords", Types.LONG)
            .add("paid_keywords", Types.LONG)
            .add("org_keywords_1_3", Types.LONG)
            .add("org_traffic", Types.LONG)
            .add("org_cost", Types.LONG)
            .add("paid_traffic", Types.LONG)
            .add("paid_cost", Types.LONG)
            .add("paid_pages", Types.LONG)
        return builder.build()
    }
}