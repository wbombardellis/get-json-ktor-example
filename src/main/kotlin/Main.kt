import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserAddress(
    val city: String
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: UserAddress
)

fun main() {
    runBlocking {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
        httpClient.use { client ->
            val getResponse = runCatching {
                client.get("https://jsonplaceholder.typicode.com/users")
            }.onFailure {
                println("An error occurred when trying to get list. Message: ${it.message}")
            }.getOrNull()

            if (getResponse != null) {
                val userList = runCatching {
                    getResponse.body<List<User>>()
                }.onFailure {
                    println("An error occurred when trying to parse the list. Message: ${it.message}")
                }.getOrNull()

                if (userList != null) {
                    println("Which city do you want?")
                    val desiredCity = readlnOrNull()
                    val desiredUsers = userList.filter { it.address.city == desiredCity }

                    desiredUsers.ifEmpty {
                        println("No users found")
                        emptyList()
                    }.forEach {
                        println("Name: ${it.name}")
                        println("Username: ${it.username}")
                        println("Email: ${it.email}")
                        println("City: ${it.address.city}")
                    }
                }
            }
        }
    }
}