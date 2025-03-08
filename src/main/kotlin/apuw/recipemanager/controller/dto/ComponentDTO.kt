package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.Component
import java.util.*

data class ComponentDTO(
    val id: UUID?,
    val name: String,
    val ingredients: MutableList<IngredientDTO>?,
    val steps: MutableList<StepDTO>?,
) {
    constructor(component: Component) : this(
        id = component.id,
        name = component.name,
        ingredients = component.ingredients.map { IngredientDTO(it) }.toMutableList(),
        steps = component.steps.map { StepDTO(it) }.toMutableList(),
    )
}
