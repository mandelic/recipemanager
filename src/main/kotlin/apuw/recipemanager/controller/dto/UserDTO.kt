package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.User
import java.util.UUID

data class UserDTO(
    val id: UUID?,
    val username: String,
) {
    constructor(user: User) : this (
        id = user.id,
        username = user.username,
    )
}
