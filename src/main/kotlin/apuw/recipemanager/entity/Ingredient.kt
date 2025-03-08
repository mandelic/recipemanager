package apuw.recipemanager.entity

import apuw.recipemanager.controller.dto.IngredientDTO
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class Ingredient(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val quantity: Float,
    val unit: String,
    @ManyToOne
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    val component: Component,
) {
    constructor(ingredientDTO: IngredientDTO, component: Component) : this(
        id = UUID.randomUUID(),
        name = ingredientDTO.name,
        quantity = ingredientDTO.quantity,
        unit = ingredientDTO.unit,
        component = component,
    )
}
