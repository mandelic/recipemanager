package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.Ingredient
import java.util.*

data class IngredientDTO(
    val id: UUID?,
    val name: String,
    val quantity: Float,
    val unit: String,
) {
    constructor(ingredient: Ingredient) : this(
        id = ingredient.id,
        name = ingredient.name,
        quantity = ingredient.quantity,
        unit = ingredient.unit,
    )
}