package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.Recipe
import java.time.LocalDateTime
import java.util.UUID

data class RecipeDTO(
    var id: UUID?,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val createdBy: String,
) {
    constructor(recipe: Recipe) : this(
        id = recipe.id,
        name = recipe.name,
        description = recipe.description,
        createdAt = recipe.createdAt,
        updatedAt = recipe.updatedAt,
        createdBy = recipe.createdBy.username,
    )
}
