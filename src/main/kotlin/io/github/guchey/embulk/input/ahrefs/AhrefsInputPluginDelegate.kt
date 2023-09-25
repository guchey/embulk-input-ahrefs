package io.github.guchey.embulk.input.ahrefs

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer.BackLinkStatsInputPlugin
import io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer.DomainRatingInputPlugin
import io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer.RefDomainsHistoryInputPlugin
import org.embulk.base.restclient.DispatchingRestClientInputPluginDelegate
import org.embulk.base.restclient.RestClientInputPluginDelegate
import org.embulk.base.restclient.RestClientInputTaskBase
import org.embulk.util.config.Config
import org.slf4j.LoggerFactory
import java.util.*


class AhrefsInputPluginDelegate : DispatchingRestClientInputPluginDelegate<AhrefsInputPluginDelegate.PluginTask>() {
    interface PluginTask : RestClientInputTaskBase {

        @get:Config("resource")
        val resource: Resource

        enum class Resource(private val restClientInputPluginDelegate: RestClientInputPluginDelegate<*>) {
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

            @Suppress("UNCHECKED_CAST")
            @JsonIgnore
            fun getRestClientInputPluginDelegate(): RestClientInputPluginDelegate<PluginTask> {
                return this.restClientInputPluginDelegate as RestClientInputPluginDelegate<PluginTask>
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