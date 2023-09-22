package io.github.guchey.embulk.input.ahrefs.okhttp

import io.github.guchey.embulk.input.ahrefs.config.PluginTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.slf4j.LoggerFactory
import kotlin.math.pow

class RetryInterceptor(private val task: PluginTask) : Interceptor {
    private val logger = LoggerFactory.getLogger(RetryInterceptor::class.java)

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val request = chain.request()
        var response = chain.proceed(request)
        var retryCount = 0
        while (!response.isSuccessful && retryCount < task.retry.maxRetries) {
            val jitter = Math.random()
            val waitTime = jitter * minOf(
                2.0.pow(retryCount) * task.retry.initialIntervalMillis,
                task.retry.maxIntervalMillis.toDouble()
            )
            logger.info("retry: $retryCount, wait: $waitTime seconds")
            delay(waitTime.toLong())
            retryCount++
            response.close()
            response = chain.proceed(request)
        }
        return@runBlocking response
    }
}