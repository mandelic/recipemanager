package apuw.recipemanager.controller.dto

import apuw.recipemanager.entity.Step
import java.util.*

data class StepDTO(
    val id: UUID?,
    val stepNumber: Int,
    val description: String
) {
    constructor(step: Step) : this(
        id = step.id,
        stepNumber = step.stepNumber,
        description = step.description,
    )
}