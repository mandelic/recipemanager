package apuw.recipemanager.security

import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.UserRepository
import apuw.recipemanager.service.exception.AccessDeniedCustomException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityUtils(private val userRepository: UserRepository) {

    fun getCurrentUser() : User {
        val authentication = SecurityContextHolder.getContext().authentication
        return userRepository.findById(UUID.fromString(authentication.name)).orElseThrow {
            Exception("User not found.")
        }
    }

    private fun isUserAllowed(user: User): Boolean {
        val currentUser = getCurrentUser()
        return currentUser.role == "ROLE_ADMIN" || currentUser == user
    }

    fun checkUserPermission(user: User) {
        if (!isUserAllowed(user)) {
            throw AccessDeniedCustomException()
        }
    }

}