package apuw.recipemanager.controller.dto

import java.util.*

data class Token(
    val role: String,
    var token: String,
    val userId: UUID,
    )
