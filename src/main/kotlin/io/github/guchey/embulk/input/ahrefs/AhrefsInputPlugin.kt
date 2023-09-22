package io.github.guchey.embulk.input.ahrefs

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.guchey.embulk.input.ahrefs.config.PluginTask
import org.embulk.base.restclient.RestClientInputPluginBase
import org.embulk.util.config.ConfigMapperFactory
import org.embulk.util.config.modules.ColumnModule
import org.embulk.util.config.modules.SchemaModule
import org.embulk.util.config.modules.TypeModule


open class AhrefsInputPlugin : RestClientInputPluginBase<PluginTask>(
    CONFIG_MAPPER_FACTORY,
    PluginTask::class.java,
    AhrefsInputPluginDelegate()
) {
    companion object {
        val CONFIG_MAPPER_FACTORY: ConfigMapperFactory = ConfigMapperFactory
            .builder()
            .addDefaultModules()
            .addModule(SchemaModule())
            .addModule(ColumnModule())
            .addModule(TypeModule())
            .build()
        val CONFIG_MAPPER = CONFIG_MAPPER_FACTORY.createConfigMapper()
        val TASK_MAPPER = CONFIG_MAPPER_FACTORY.createTaskMapper()
        val OBJECT_MAPPER = ObjectMapper()
    }
}