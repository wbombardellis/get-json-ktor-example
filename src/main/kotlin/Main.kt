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
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        try {
            val response = client.get("https://jsonplaceholder.typicode.com/users")
            val users = response.body<List<User>>()

            users.filter { it.address.city == "Gwenborough" }
                .takeIf { it.isNotEmpty() }
                ?.forEach {
                    println("Name: ${it.name}, Username: ${it.username}, Email: ${it.email}, City: ${it.address.city}")
                } ?: println("No users found")
        } catch(e: Exception) {
            println("Error: ${e.message}")
        }
    }
}