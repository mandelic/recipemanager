package apuw.recipemanager.entity

import apuw.recipemanager.controller.dto.RecipeDetailsDTO
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.*

@Entity
class Recipe(
    @Id
    var id: UUID = UUID.randomUUID(),
    @NotBlank
    var name: String,
    @NotBlank
    var description: String,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @OneToMany(mappedBy = "recipe", cascade = [CascadeType.ALL], orphanRemoval = true)
    var components: MutableList<Component> = mutableListOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var createdBy: User,
) {
    constructor(
        recipeDetailsDTO: RecipeDetailsDTO,
        user: User,
    ) : this(
        id = UUID.randomUUID(),
        name = recipeDetailsDTO.name,
        description = recipeDetailsDTO.description,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        createdBy = user,
    ) {
        components = recipeDetailsDTO.components?.map { Component(it, this) }?.toMutableList() ?: mutableListOf()
    }

    fun updateData(recipeDetailsDTO: RecipeDetailsDTO) {
        name = recipeDetailsDTO.name
        description = recipeDetailsDTO.description
        updatedAt = LocalDateTime.now()
        components.clear()
        recipeDetailsDTO.components?.forEach {
            components.add(Component(it, this))
        }
    }
}
