package io.github.guchey.embulk.input.ahrefs.delegate.keywordsexplorer

import com.fasterxml.jackson.databind.JsonNode
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.schema.Country
import io.github.guchey.embulk.input.ahrefs.delegate.schema.SearchEngine
import okhttp3.Request
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.spi.type.Types
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import java.util.*
import kotlin.jvm.optionals.getOrNull


class OverviewInputPlugin : AhrefsBaseDelegate<OverviewInputPlugin.PluginTask>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask {
        @get:ConfigDefault("null")
        @get:Config("keyword_list_id")
        val keywordListId: Optional<String>

        @get:ConfigDefault("null")
        @get:Config("keywords")
        val keywords: Optional<List<String>>

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

    override fun validateInputTask(task: PluginTask) {
        require(task.country.isPresent)
        require(task.select.isPresent)
        require(task.keywordListId.isPresent || task.keywords.isPresent)
        super.validateInputTask(task)
    }

    override fun buildRequest(task: PluginTask): Request {
        val queryParam = mapOf(
            "output" to "json",
            "keyword_list_id" to task.keywordListId.getOrNull(),
            "keywords" to task.keywords.getOrNull()?.joinToString(","),
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
            .url(buildUrl("https://api.ahrefs.com/v3/site-explorer/overview",queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${task.apiKey}")
            .build()
    }

    override fun transformJsonRecord(
        task: PluginTask,
        record: JsonNode
    ): JsonNode {
        return record.get("metrics")
    }

    override fun buildServiceResponseMapper(task: PluginTask): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("live", Types.LONG)
            .add("all_time", Types.LONG)
            .add("live_refdomains", Types.LONG)
            .add("all_time_refdomains", Types.LONG)
        return builder.build()
    }
}