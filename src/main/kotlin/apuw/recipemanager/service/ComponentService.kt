package apuw.recipemanager.service

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.IngredientDTO
import apuw.recipemanager.controller.dto.StepDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Ingredient
import apuw.recipemanager.entity.Step
import apuw.recipemanager.repository.ComponentRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.exception.ComponentNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class ComponentService(
    val componentRepository: ComponentRepository,
    val recipeService: RecipeService,
    val securityUtils: SecurityUtils
) {

    fun getComponentById(id: UUID): Component = getComponentByIdOrThrow(id)

    fun update(id: UUID, componentDTO: ComponentDTO): Component {
        val component: Component = getComponentById(id)
        securityUtils.checkUserPermission(component.recipe.createdBy)
        component.updateData(componentDTO)
        updateRecipeDates(component)
        return componentRepository.save(component)
    }

    fun delete(id: UUID) {
        val component: Component = getComponentById(id)
        securityUtils.checkUserPermission(component.recipe.createdBy)
        componentRepository.deleteById(id)
        updateRecipeDates(component)
    }

    fun addComponentStep(id: UUID, stepDTO: StepDTO): Component {
        val component: Component = getComponentById(id)
        securityUtils.checkUserPermission(component.recipe.createdBy)
        val step = Step(stepDTO, component)
        component.steps.add(step)
        updateRecipeDates(component)
        return componentRepository.save(component)
    }

    fun addComponentIngredient(id: UUID, ingredientDTO: IngredientDTO): Component {
        val component: Component = getComponentById(id)
        securityUtils.checkUserPermission(component.recipe.createdBy)
        val ingredient = Ingredient(ingredientDTO, component)
        component.ingredients.add(ingredient)
        updateRecipeDates(component)
        return componentRepository.save(component)
    }

    private fun getComponentByIdOrThrow(id: UUID): Component {
        return componentRepository.findById(id)
            .orElseThrow { throw ComponentNotFoundException(id) }
    }


    private fun updateRecipeDates(component: Component) {
        recipeService.updateDates(component.recipe.id)
    }
}