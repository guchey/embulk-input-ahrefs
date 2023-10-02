package io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.HistoryGrouping
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
import kotlin.jvm.optionals.getOrNull


class UrlRatingHistoryInputPlugin<T : UrlRatingHistoryInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("date_to")
        val dateTo: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("history_grouping")
        val historyGrouping: Optional<HistoryGrouping>

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
            "date_to" to task.dateTo.getOrNull(),
            "history_grouping" to task.historyGrouping.getNameOrNull(),
            "date_from" to task.dateFrom.get(),
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.getAhrefsUrl()}/v3/site-explorer/url-rating-history", queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", task.getAuthHeader())
            .build()
    }

    override fun transformJsonRecord(
        task: T,
        record: JsonNode
    ): JsonNode {
        return record.get("url_ratings")
    }

    override fun buildServiceResponseMapper(task: T): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("date", Types.STRING)
            .add("url_rating", Types.DOUBLE)
        return builder.build()
    }
}