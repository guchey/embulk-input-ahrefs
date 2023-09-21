package com.guchey.embulk.input.ahrefs

import com.guchey.embulk.input.ahrefs.config.PluginTask
import org.embulk.base.restclient.*
import org.slf4j.LoggerFactory


class AhrefsInputPluginDelegate : DispatchingRestClientInputPluginDelegate<PluginTask>() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun dispatchPerTask(task: PluginTask): RestClientInputPluginDelegate<PluginTask> {
        val resource = task.resource
        logger.debug("dispatch {} plugin", resource)
        return resource.getRestClientInputPluginDelegate()
    }
}