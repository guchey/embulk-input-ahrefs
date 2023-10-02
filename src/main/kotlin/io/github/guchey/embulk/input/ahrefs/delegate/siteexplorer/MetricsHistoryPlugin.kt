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


class MetricsHistoryPlugin<T : MetricsHistoryPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("country")
        val country: Optional<Country>

        @get:ConfigDefault("null")
        @get:Config("date_to")
        val dateTo: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("history_grouping")
        val historyGrouping: Optional<HistoryGrouping>

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
        @get:Config("date_from")
        val dateFrom: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("target")
        val target: Optional<String>
    }

    override fun validateInputTask(task: T) {
        validateAndResolveFiled(task.dateFrom, "date_from")
        validateAndResolveFiled(task.target, "target")
        super.validateInputTask(task)
    }

    override fun buildRequest(task: T): Request {
        val queryParam = mapOf(
            "output" to "json",
            "country" to task.country.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "output" to "json",
            "date_to" to task.dateTo.getOrNull(),
            "history_grouping" to task.historyGrouping.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "mode" to task.mode.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "protocol" to task.protocol.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "volume_mode" to task.volumeMode.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "date_from" to task.dateFrom.get(),
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.baseUrl}/v3/site-explorer/metrics-history", queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${task.apiKey}")
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
            .add("date", Types.STRING)
            .add("org_traffic", Types.LONG)
            .add("paid_traffic", Types.LONG)
            .add("org_cost", Types.LONG)
            .add("paid_cost", Types.LONG)
        return builder.build()
    }
}