package apuw.recipemanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.SecureRandom

@SpringBootApplication
class RecipeManagerApplication {
	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder(11, SecureRandom())
}

fun main(args: Array<String>) {
	runApplication<RecipeManagerApplication>(*args)
}
