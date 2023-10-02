package io.github.guchey.embulk.input.ahrefs

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.guchey.embulk.input.ahrefs.delegate.AhrefsBaseDelegate
import io.github.guchey.embulk.input.ahrefs.delegate.keywordsexplorer.OverviewInputPlugin
import io.github.guchey.embulk.input.ahrefs.delegate.siteexplorer.*
import org.embulk.base.restclient.DispatchingRestClientInputPluginDelegate
import org.embulk.base.restclient.RestClientInputPluginDelegate
import org.embulk.util.config.Config
import org.slf4j.LoggerFactory
import java.util.*


class AhrefsInputPluginDelegate : DispatchingRestClientInputPluginDelegate<AhrefsInputPluginDelegate.PluginTask>() {
    interface PluginTask : AhrefsBaseDelegate.PluginTask,
        OverviewInputPlugin.PluginTask,
        BackLinkStatsInputPlugin.PluginTask,
        DomainRatingInputPlugin.PluginTask,
        MetricsInputPlugin.PluginTask,
        RefDomainsHistoryInputPlugin.PluginTask,
        UrlRatingHistoryInputPlugin.PluginTask,
        MetricsHistoryPlugin.PluginTask,
        PagesHistoryInputPlugin.PluginTask {

        @get:Config("resource")
        val resource: Resource

        enum class Resource(private val restClientInputPluginDelegate: RestClientInputPluginDelegate<PluginTask>) {
            SITE_EXPLORER_DOMAIN_RATING(DomainRatingInputPlugin()),
            SITE_EXPLORER_BACKLINK_STATS(BackLinkStatsInputPlugin()),
            SITE_EXPLORER_METRICS(MetricsInputPlugin()),
            SITE_EXPLORER_REF_DOMAINS_HISTORY(RefDomainsHistoryInputPlugin()),
            SITE_EXPLORER_URL_RATING_HISTORY(UrlRatingHistoryInputPlugin()),
            SITE_EXPLORER_METRICS_HISTORY(MetricsHistoryPlugin()),
            SITE_EXPLORER_PAGES_HISTORY(PagesHistoryInputPlugin()),
            KEYWORD_EXPLORER_OVERVIEW(OverviewInputPlugin());

            companion object {
                @JvmStatic
                @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
                fun of(value: String): Resource {
                    return Resource.valueOf(value.uppercase(Locale.getDefault()))
                }
            }

            @JsonIgnore
            fun getRestClientInputPluginDelegate(): RestClientInputPluginDelegate<PluginTask> {
                return this.restClientInputPluginDelegate
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