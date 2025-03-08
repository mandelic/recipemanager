package apuw.recipemanager.controller

import apuw.recipemanager.service.StepIngredientService
import apuw.recipemanager.util.Paths.INGREDIENTS_PATH
import apuw.recipemanager.util.Paths.STEPS_PATH
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping
@RestController
@Tag(
    name = "Steps and Ingredients Management",
    description =
        "Endpoints for managing the steps and ingredients of a recipe." +
            "This includes operations to delete specific step or ingredient by their ID.",
)
class StepIngredientController(
    val stepIngredientService: StepIngredientService,
) {
    @Operation(
        summary = "Delete an Ingredient by ID",
        description =
            "Deletes the ingredient with the specified ID from the system. " +
                "If the ingredient exists, it will be permanently removed.",
    )
    @DeleteMapping("$INGREDIENTS_PATH/{id}")
    fun deleteIngredient(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        stepIngredientService.deleteIngredientById(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Delete a Step by ID",
        description = "Deletes the step with the specified ID from the system. If the step exists, it will be permanently removed.",
    )
    @DeleteMapping("$STEPS_PATH/{id}")
    fun deleteStep(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        stepIngredientService.deleteStepById(id)
        return ResponseEntity.noContent().build()
    }
}
