package apuw.recipemanager.entity

import apuw.recipemanager.controller.dto.ComponentDTO
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class Component(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String,
    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    val recipe: Recipe,
    @OneToMany(mappedBy = "component", cascade = [CascadeType.ALL], orphanRemoval = true)
    var ingredients: MutableList<Ingredient> = mutableListOf(),
    @OneToMany(mappedBy = "component", cascade = [CascadeType.ALL], orphanRemoval = true)
    var steps: MutableList<Step> = mutableListOf(),
) {
    constructor(componentDTO: ComponentDTO, recipe: Recipe) : this(
        id = UUID.randomUUID(),
        recipe = recipe,
        name = componentDTO.name,
    ) {
        ingredients = componentDTO.ingredients?.map { Ingredient(it, this) }?.toMutableList() ?: mutableListOf()
        steps = componentDTO.steps?.map { Step(it, this) }?.toMutableList() ?: mutableListOf()
    }

    fun updateData(componentDTO: ComponentDTO) {
        name = componentDTO.name
        ingredients.clear()
        componentDTO.ingredients?.forEach {
            ingredients.add(Ingredient(it, this))
        }
        steps.clear()
        componentDTO.steps?.forEach {
            steps.add(Step(it, this))
        }
    }
}
