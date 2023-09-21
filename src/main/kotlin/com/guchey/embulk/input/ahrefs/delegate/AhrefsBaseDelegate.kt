package com.guchey.embulk.input.ahrefs.delegate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.guchey.embulk.input.ahrefs.AhrefsInputPlugin
import com.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.CONFIG_MAPPER_FACTORY
import com.guchey.embulk.input.ahrefs.config.PluginTask
import com.guchey.embulk.input.ahrefs.okhttp.RetryInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.embulk.base.restclient.DefaultServiceDataSplitter
import org.embulk.base.restclient.RestClientInputPluginDelegate
import org.embulk.base.restclient.ServiceDataSplitter
import org.embulk.base.restclient.ServiceResponseMapper
import org.embulk.base.restclient.jackson.JacksonServiceRecord
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.RecordImporter
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.config.ConfigDiff
import org.embulk.config.TaskReport
import org.embulk.spi.Exec
import org.embulk.spi.PageBuilder
import org.embulk.spi.Schema
import org.slf4j.LoggerFactory
import java.util.*


abstract class AhrefsBaseDelegate : RestClientInputPluginDelegate<PluginTask> {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val PREVIEW_RECORD_LIMIT = 15

    override fun buildConfigDiff(
        task: PluginTask, schema: Schema, taskCount: Int, taskReports: MutableList<TaskReport>
    ): ConfigDiff {
        taskReports.forEach { report -> logger.info(report.toString()) }
        return CONFIG_MAPPER_FACTORY.newConfigDiff();
    }

    override fun validateInputTask(task: PluginTask) {
        logger.debug("validateInputTask")
        task.validate()
    }

    override fun buildServiceDataSplitter(task: PluginTask): ServiceDataSplitter<PluginTask> {
        return DefaultServiceDataSplitter();
    }

    override fun buildServiceResponseMapper(task: PluginTask): ServiceResponseMapper<out ValueLocator> {
        return JacksonServiceResponseMapper.builder().build();
    }

    override fun ingestServiceData(
        task: PluginTask, recordImporter: RecordImporter, taskIndex: Int, pageBuilder: PageBuilder
    ): TaskReport = runBlocking {
        if (Exec.isPreview()) {
            task.limit = Optional.of(PREVIEW_RECORD_LIMIT)
        }
        val retryInterceptor = RetryInterceptor(task)
        val client = OkHttpClient.Builder().addInterceptor(retryInterceptor).build()
        val response = fetch(client, task)
        logger.debug("response: {}", response)
        ingestTransformedJsonRecord(task, recordImporter, pageBuilder, transformJsonRecord(task, response))
        var imported = 0
        while (paginationRequired(task, response) && (imported < PREVIEW_RECORD_LIMIT || !Exec.isPreview())) {
            ingestTransformedJsonRecord(task, recordImporter, pageBuilder, transformJsonRecord(task, response))
            imported++
        }
        return@runBlocking CONFIG_MAPPER_FACTORY.newTaskReport()
    }


    /**
     * Builds a {@code Request} object with the specified task's parameters.
     *
     * @param task  a {@code PluginTask} object that contains necessary parameters for the request
     * @return a {@code Request} object with the specified task's parameters
     *
     * This method creates a request for the Ahrefs API, setting the necessary parameters and headers.
     */
    abstract fun buildRequest(task: PluginTask): okhttp3.Request

    /**
     * Transforms a JSON record by extracting the "metrics" node.
     *
     * @param task   a {@code PluginTask} object that contains necessary parameters for the transformation
     * @param record a {@code JsonNode} object that represents the JSON record to be transformed
     * @return a {@code JsonNode} object representing the node to which the original node was transformed.
     */
    abstract fun transformJsonRecord(task: PluginTask, record: JsonNode): JsonNode

    private suspend fun fetch(client: OkHttpClient, task: PluginTask): JsonNode {
        val request = buildRequest(task)
        return withContext(Dispatchers.IO) {
            logger.info("try to fetch {}", request.url)
            client.newCall(request).execute().use { response ->
                logger.debug("response code: {}", response.code)
                val responseData = response.body?.string()
                AhrefsInputPlugin.OBJECT_MAPPER.readTree(responseData)
            }
        }
    }

    private fun ingestTransformedJsonRecord(
        task: PluginTask, recordImporter: RecordImporter, pageBuilder: PageBuilder, transformedJsonRecord: JsonNode
    ) {
        if (transformedJsonRecord.isArray) {
            for (record in transformedJsonRecord as ArrayNode) {
                val on = record as ObjectNode
                recordImporter.importRecord(JacksonServiceRecord(on), pageBuilder)
            }
        } else {
            val on = transformedJsonRecord as ObjectNode
            recordImporter.importRecord(JacksonServiceRecord(on), pageBuilder)
        }
    }

    private fun paginationRequired(task: PluginTask, response: JsonNode): Boolean {
        return false
    }
}