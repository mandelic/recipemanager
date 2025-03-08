package apuw.recipemanager.service

import apuw.recipemanager.repository.IngredientRepository
import apuw.recipemanager.repository.StepRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.exception.AccessDeniedCustomException
import org.springframework.stereotype.Service
import java.util.*

@Service
class StepIngredientService(
    val stepRepository: StepRepository,
    val ingredientRepository: IngredientRepository,
    val securityUtils: SecurityUtils
) {
    fun deleteIngredientById(id: UUID) {
        val ingredient = ingredientRepository.findById(id).orElseThrow {
            Exception("No ingredients found for id $id")
        }
        if (!securityUtils.isUserAllowed(ingredient.component.recipe.createdBy)) {
            throw AccessDeniedCustomException()
        }
        ingredientRepository.delete(ingredient)
    }

    fun deleteStepById(id: UUID) {
        val step = stepRepository.findById(id).orElseThrow {
            Exception("No steps found for id $id")
        }
        if (!securityUtils.isUserAllowed(step.component.recipe.createdBy)) {
            throw AccessDeniedCustomException()
        }
        stepRepository.delete(step)
    }
}