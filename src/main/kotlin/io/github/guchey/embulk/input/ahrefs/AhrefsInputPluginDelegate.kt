package io.github.guchey.embulk.input.ahrefs

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.guchey.embulk.input.ahrefs.delegate.BackLinkStatsInputPlugin
import io.github.guchey.embulk.input.ahrefs.delegate.DomainRatingInputPlugin
import io.github.guchey.embulk.input.ahrefs.delegate.RefDomainsHistoryInputPlugin
import org.embulk.base.restclient.DispatchingRestClientInputPluginDelegate
import org.embulk.base.restclient.RestClientInputPluginDelegate
import org.embulk.base.restclient.RestClientInputTaskBase
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import org.embulk.util.config.Task
import org.slf4j.LoggerFactory
import java.util.*


class AhrefsInputPluginDelegate : DispatchingRestClientInputPluginDelegate<AhrefsInputPluginDelegate.PluginTask>() {
    interface PluginTask : RestClientInputTaskBase, BackLinkStatsInputPlugin.PluginTask,
        DomainRatingInputPlugin.PluginTask, RefDomainsHistoryInputPlugin.PluginTask {

        @get:Config("api_key")
        val apiKey: String

        @get:Config("resource")
        val resource: Resource

        @get:ConfigDefault("null")
        @get:Config("limit")
        var limit: Optional<Int>

        interface PagerOption : Task {
            @get:ConfigDefault("[]")
            @get:Config("initial_params")
            val initialParams: List<Map<String?, Any?>?>?

            @get:ConfigDefault("[]")
            @get:Config("next_params")
            val nextParams: List<Map<String?, Any?>?>?

            @get:ConfigDefault("\".request_body\"")
            @get:Config("next_body_transformer")
            val nextBodyTransformer: String

            @get:ConfigDefault("\"false\"")
            @get:Config("while")
            val `while`: String

            @get:ConfigDefault("100")
            @get:Config("interval_millis")
            val intervalMillis: Long
        }


        @get:Config("pager")
        @get:ConfigDefault("{}")
        val pager: PagerOption

        interface RetryOption : Task {
            @get:ConfigDefault("\"true\"")
            @get:Config("condition")
            val condition: String

            @get:ConfigDefault("7")
            @get:Config("max_retries")
            val maxRetries: Int

            @get:ConfigDefault("1000")
            @get:Config("initial_interval_millis")
            val initialIntervalMillis: Int

            @get:ConfigDefault("60000")
            @get:Config("max_interval_millis")
            val maxIntervalMillis: Int
        }

        @get:ConfigDefault("{}")
        @get:Config("retry")
        val retry: RetryOption

        enum class Resource(private val restClientInputPluginDelegate: RestClientInputPluginDelegate<PluginTask>) {
            DOMAIN_RATING(DomainRatingInputPlugin()),
            BACKLINK_STATS(BackLinkStatsInputPlugin()),
            REF_DOMAINS_HISTORY(RefDomainsHistoryInputPlugin());

            companion object {
                @JvmStatic
                @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
                fun of(value: String): Resource {
                    return Resource.valueOf(value.uppercase(Locale.getDefault()))
                }
            }

            @JsonIgnore
            fun getRestClientInputPluginDelegate(): RestClientInputPluginDelegate<PluginTask> {
                return restClientInputPluginDelegate
            }
        }
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun dispatchPerTask(task: PluginTask): RestClientInputPluginDelegate<PluginTask> {
        val resource = task.resource
        logger.debug("dispatch {} plugin", resource)
        return resource.getRestClientInputPluginDelegate()
    }
}