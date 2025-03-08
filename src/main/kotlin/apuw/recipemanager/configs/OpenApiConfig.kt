package apuw.recipemanager.configs

import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI().info(
            Info().title("Recipe Manager API")
                .version("v1")
                .description("This is a sample API documentation")
        ).servers(
            listOf(
                Server().url("http://localhost:8080").description("Local Development Server"),
            )
        )
    }
}