@file:JvmName("ClientApp")

package ru.meetup.client
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Duration

fun main() = runBlocking {
    val config: Config = ConfigFactory.load().extract()
    val client = HttpClient(CIO) {
        install(Logging)
    }

    for (worker in 1..config.workers) {
        launch { process(client, worker, config) }
        delay(500)
    }
}

internal suspend fun process(client: HttpClient, worker: Int, config: Config) = withContext(Dispatchers.IO){
    for (i in 1..config.iterations) {
        try {
            client.request<String>("${config.url}?name=user$i-worker$worker") {
                method = HttpMethod.Get
            }
        } catch (e: Exception) { }
        delay(config.delay.toMillis())
    }
}

data class Config(
    val url: String,
    val iterations: Int,
    val workers: Int,
    val delay: Duration,
)
