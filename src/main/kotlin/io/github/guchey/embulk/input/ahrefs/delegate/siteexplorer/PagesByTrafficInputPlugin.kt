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


class PagesByTrafficInputPlugin<T : PagesByTrafficInputPlugin.PluginTask> : AhrefsBaseDelegate<T>() {
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
        @get:Config("target")
        val target: Optional<String>
    }

    override fun validateInputTask(task: T) {
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
            "target" to task.target.get()
        )
        return Request.Builder()
            .url(buildUrl("${task.resolveAhrefsUrl()}/v3/site-explorer/pages-by-traffic", queryParam))
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", task.resolveAuthHeader())
            .build()
    }

    override fun transformJsonRecord(
        task: T,
        record: JsonNode
    ): JsonNode {
        return record.get("pages")
    }

    override fun buildServiceResponseMapper(task: T): ServiceResponseMapper<out ValueLocator> {
        val builder = JacksonServiceResponseMapper.builder()
        builder
            .add("date", Types.STRING)
            .add("range0_pages", Types.LONG)
            .add("range100_traffic", Types.LONG)
            .add("range100_pages", Types.LONG)
            .add("range1k_traffic", Types.LONG)
            .add("range1k_pages", Types.LONG)
            .add("range5k_traffic", Types.LONG)
            .add("range5k_pages", Types.LONG)
            .add("range10k_traffic", Types.LONG)
            .add("range10k_pages", Types.LONG)
            .add("range10k_plus_traffic", Types.LONG)
            .add("range10k_plus_pages", Types.LONG)
        return builder.build()
    }
}