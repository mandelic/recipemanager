package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.Recipe
import java.time.LocalDateTime
import java.util.*

data class RecipeDetailsDTO(
    var id: UUID?,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val createdBy: UserDTO?,
    val components: MutableList<ComponentDTO>?,
) {
    constructor(recipe: Recipe) : this(
        id = recipe.id,
        name = recipe.name,
        description = recipe.description,
        createdAt = recipe.createdAt,
        updatedAt = recipe.updatedAt,
        components = recipe.components.map { ComponentDTO(it) }.toMutableList(),
        createdBy = UserDTO(recipe.createdBy),
    )
}
