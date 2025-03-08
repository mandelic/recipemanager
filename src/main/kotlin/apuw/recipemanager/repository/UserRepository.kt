package apuw.recipemanager.repository

import apuw.recipemanager.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): Optional<User>
}