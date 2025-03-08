package apuw.recipemanager.entity

import apuw.recipemanager.controller.dto.StepDTO
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class Step(
    @Id
    val id: UUID = UUID.randomUUID(),
    val stepNumber: Int,
    val description: String,
    @ManyToOne
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    val component: Component,
) {
    constructor(stepDTO: StepDTO, component: Component) : this(
        id = UUID.randomUUID(),
        stepNumber = stepDTO.stepNumber,
        description = stepDTO.description,
        component = component,
    )
}
