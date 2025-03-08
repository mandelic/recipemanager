package apuw.recipemanager.service

import apuw.recipemanager.repository.IngredientRepository
import apuw.recipemanager.repository.StepRepository
import apuw.recipemanager.security.SecurityUtils
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StepIngredientService(
    val stepRepository: StepRepository,
    val ingredientRepository: IngredientRepository,
    val securityUtils: SecurityUtils,
) {
    fun deleteIngredientById(id: UUID) {
        val ingredient =
            ingredientRepository.findById(id).orElseThrow {
                Exception("No ingredients found for id $id")
            }
        securityUtils.checkUserPermission(ingredient.component.recipe.createdBy)
        ingredientRepository.delete(ingredient)
    }

    fun deleteStepById(id: UUID) {
        val step =
            stepRepository.findById(id).orElseThrow {
                Exception("No steps found for id $id")
            }
        securityUtils.checkUserPermission(step.component.recipe.createdBy)
        stepRepository.delete(step)
    }
}
