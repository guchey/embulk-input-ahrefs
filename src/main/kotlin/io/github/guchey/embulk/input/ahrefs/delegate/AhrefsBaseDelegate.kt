package io.github.guchey.embulk.input.ahrefs.delegate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.CONFIG_MAPPER_FACTORY
import io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin.Companion.OBJECT_MAPPER
import io.github.guchey.embulk.input.ahrefs.okhttp.RetryInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.embulk.base.restclient.*
import org.embulk.base.restclient.jackson.JacksonServiceRecord
import org.embulk.base.restclient.jackson.JacksonServiceResponseMapper
import org.embulk.base.restclient.record.RecordImporter
import org.embulk.base.restclient.record.ValueLocator
import org.embulk.config.ConfigDiff
import org.embulk.config.ConfigException
import org.embulk.config.TaskReport
import org.embulk.spi.PageBuilder
import org.embulk.spi.Schema
import org.embulk.util.config.Config
import org.embulk.util.config.ConfigDefault
import org.embulk.util.config.Task
import org.slf4j.LoggerFactory
import java.util.*


abstract class AhrefsBaseDelegate<T : AhrefsBaseDelegate.PluginTask> : RestClientInputPluginDelegate<T> {
    interface PluginTask : RestClientInputTaskBase {

        @get:ConfigDefault("\"https://api.ahrefs.com\"")
        @get:Config("base_url")
        val baseUrl: String

        @get:Config("api_key")
        val apiKey: String

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
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun buildConfigDiff(
        task: T, schema: Schema, taskCount: Int, taskReports: MutableList<TaskReport>
    ): ConfigDiff {
        taskReports.forEach { report -> logger.info(report.toString()) }
        return CONFIG_MAPPER_FACTORY.newConfigDiff();
    }

    override fun validateInputTask(task: T) {
        task.validate()
    }

    override fun buildServiceDataSplitter(task: T): ServiceDataSplitter<T> {
        return DefaultServiceDataSplitter();
    }

    override fun buildServiceResponseMapper(task: T): ServiceResponseMapper<out ValueLocator> {
        return JacksonServiceResponseMapper.builder().build();
    }

    override fun ingestServiceData(
        task: T, recordImporter: RecordImporter, taskIndex: Int, pageBuilder: PageBuilder
    ): TaskReport = runBlocking {
        val retryInterceptor = RetryInterceptor(task)
        val client = OkHttpClient.Builder().addInterceptor(retryInterceptor).build()
        val response = fetch(client, task)
        logger.debug("response: {}", response)
        ingestTransformedJsonRecord(task, recordImporter, pageBuilder, transformJsonRecord(task, response))
        return@runBlocking CONFIG_MAPPER_FACTORY.newTaskReport()
    }

    fun buildUrl(url: String, queryParam: Map<String, String?>): String {
        val query =
            queryParam.entries.filterNot { it.value.isNullOrEmpty() }.joinToString("&") { "${it.key}=${it.value}" }
        return "${url}?${query}"
    }


    /**
     * Builds a {@code Request} object with the specified task's parameters.
     *
     * @param task  a {@code PluginTask} object that contains necessary parameters for the request
     * @return a {@code Request} object with the specified task's parameters
     *
     * This method creates a request for the Ahrefs API, setting the necessary parameters and headers.
     */
    abstract fun buildRequest(task: T): okhttp3.Request

    /**
     * Transforms a JSON record by extracting the "metrics" node.
     *
     * @param task   a {@code PluginTask} object that contains necessary parameters for the transformation
     * @param record a {@code JsonNode} object that represents the JSON record to be transformed
     * @return a {@code JsonNode} object representing the node to which the original node was transformed.
     */
    abstract fun transformJsonRecord(task: T, record: JsonNode): JsonNode

    private suspend fun fetch(client: OkHttpClient, task: T): JsonNode {
        val request = buildRequest(task)
        return withContext(Dispatchers.IO) {
            logger.info("try to fetch {}", request.url)
            client.newCall(request).execute().use { response ->
                logger.debug("response code: {}", response.code)
                val responseData = response.body?.string()
                OBJECT_MAPPER.readTree(responseData)
            }
        }
    }

    private fun ingestTransformedJsonRecord(
        task: T, recordImporter: RecordImporter, pageBuilder: PageBuilder, transformedJsonRecord: JsonNode
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

    fun <T> validateAndResolveFiled(field: Optional<T>, fieldName: String): T {
        if (!field.isPresent) {
            throw ConfigException("Field '${fieldName}' is required but not set");
        }
        return field.get()
    }
}