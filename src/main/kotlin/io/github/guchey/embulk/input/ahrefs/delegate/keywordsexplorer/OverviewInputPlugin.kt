package io.github.guchey.embulk.input.ahrefs.delegate.keywordsexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Country
import io.github.guchey.embulk.input.ahrefs.delegate.schema.SearchEngine
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.config.ConfigException
import org.embulk.spi.type.Type
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*
import kotlin.jvm.optionals.getOrNull


class OverviewInputPlugin<T : OverviewInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("keyword_list_id")
        val keywordListId: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("keywords")
        val keywords: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("limit")
        var limit: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("offset")
        var offset: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("order_by")
        var orderBy: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("search_engine")
        var searchEngine: Optional<SearchEngine>

        @get:ConfigDefault("null")
        @get:Config("timeout")
        var timeout: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("where")
        val where: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("country")
        val country: Optional<Country>

        @get:ConfigDefault("null")
        @get:Config("select")
        val select: Optional<String>
    }

    companion object {
        val COLUMNS: Map<String, Type> = mapOf(
            "clicks" to Types.LONG,
            "cpc" to Types.LONG,
            "cps" to Types.DOUBLE,
            "difficulty" to Types.LONG,
            "first_seen" to Types.TIMESTAMP,
            "global_volume" to Types.LONG,
            "keyword" to Types.STRING,
            "parent_topic" to Types.STRING,
            "parent_volume" to Types.LONG,
            "searches_pct_clicks_organic_and_paid" to Types.DOUBLE,
            "searches_pct_clicks_organic_only" to Types.DOUBLE,
            "searches_pct_clicks_paid_only" to Types.DOUBLE,
            "serp_features" to Types.JSON,
            "serp_last_update" to Types.TIMESTAMP,
            "traffic_potential" to Types.LONG,
            "volume" to Types.LONG
        )
    }

    override fun validateInputTask(task: T) {
        validateAndResolveFiled(task.country, "country")
        validateAndResolveFiled(task.select, "select")
        if (!task.keywordListId.isPresent && !task.keywords.isPresent) {
            throw ConfigException("Either Field 'keywordListId' or Field 'keywords' is required but not set");
        }
        super.validateInputTask(task)
    }

    override fun buildRequest(task: T): Request {
        val queryParam = mapOf(
            "output" to "json",
            "keyword_list_id" to task.keywordListId.getOrNull(),
            "keywords" to task.keywords.getOrNull(),
            "limit" to task.limit.getOrNull(),
            "offset" to task.offset.getOrNull(),
            "order_by" to task.orderBy.getOrNull(),
            "search_engine" to task.searchEngine.getOrNull()?.name?.lowercase(Locale.getDefault()),
            "timeout" to task.timeout.getOrNull(),
            "where" to task.where.getOrNull(),
            "country" to task.country.get().name.lowercase(Locale.getDefault()),
            "select" to task.select.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.getAhrefsUrl()}/v3/keywords-explorer/overview", queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", task.getAuthHeader())
            .build()
    }

    override fun transformJsonRecord(
        task: T,
        record: JsonNode
    ): JsonNode {
        return record.get("keywords")
    }

    override fun buildServiceResponseMapper(task: T): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        val select = task.select.get()
        lookupField(select).forEach {
            builder.add(it.first, it.second)
        }
        return builder.build()
    }

    private fun lookupField(select: String): List<Pair<String, Type>> {
        return select.split(",").map {
            it to COLUMNS[it]
        }.filterNot { it.second == null }.map { it.first to it.second!! }
    }
}