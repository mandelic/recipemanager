package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.User

data class LoginRequest(
    val username: String,
    val password: String,
) {
    fun toUser(password: String): User = User(username, password, "ROLE_USER")
}
