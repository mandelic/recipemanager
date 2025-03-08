package apuw.recipemanager.service

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.RecipeDetailsDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Recipe
import apuw.recipemanager.repository.RecipeRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.exception.RecipeNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class RecipeService(
    val recipeRepository: RecipeRepository,
    val securityUtils: SecurityUtils,
) {
    fun getAllRecipes(): List<Recipe> = recipeRepository.findAll()

    fun getRecipeById(id: UUID): Recipe = recipeRepository.findById(id).orElseThrow { RecipeNotFoundException(id) }

    fun save(recipeDetailsDTO: RecipeDetailsDTO): Recipe =
        recipeRepository.save(Recipe(recipeDetailsDTO, securityUtils.getCurrentUser()))


    fun update(id: UUID, recipeDetailsDTO: RecipeDetailsDTO): Recipe {
        val recipe: Recipe = getRecipeById(id)
        securityUtils.checkUserPermission(recipe.createdBy)
        recipe.updateData(recipeDetailsDTO)
        recipe.updatedAt = LocalDateTime.now()
        return recipeRepository.save(recipe)
    }

    fun delete(id: UUID) {
        val recipe = getRecipeById(id)
        securityUtils.checkUserPermission(recipe.createdBy)
        recipeRepository.deleteById(id)
    }

    fun addRecipeComponent(id: UUID, componentDTO: ComponentDTO): Recipe {
        val recipe: Recipe = getRecipeById(id)
        securityUtils.checkUserPermission(recipe.createdBy)
        val component = Component(componentDTO, recipe)
        recipe.components.add(component)
        recipe.updatedAt = LocalDateTime.now()
        return recipeRepository.save(recipe)
    }

    fun updateDates(id: UUID) {
        val recipe: Recipe = getRecipeById(id)
        recipe.updatedAt = LocalDateTime.now()
        recipeRepository.save(recipe)
    }
}