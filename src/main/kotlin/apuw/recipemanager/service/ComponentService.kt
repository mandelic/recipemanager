package apuw.recipemanager.service

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.IngredientDTO
import apuw.recipemanager.controller.dto.StepDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Ingredient
import apuw.recipemanager.entity.Step
import apuw.recipemanager.repository.ComponentRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.exception.AccessDeniedCustomException
import apuw.recipemanager.service.exception.ComponentNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class ComponentService(
    val componentRepository: ComponentRepository,
    val recipeService: RecipeService,
    val securityUtils: SecurityUtils
) {

    fun getComponentById(id: UUID): Component = componentRepository.findById(id).orElseThrow { throw ComponentNotFoundException(id) }

    fun update(id: UUID, componentDTO: ComponentDTO): Component {
        val component: Component = getComponentById(id)
        if (!securityUtils.isUserAllowed(component.recipe.createdBy)) {
            throw AccessDeniedCustomException()
        }
        component.updateData(componentDTO)
        recipeService.updateDates(component.recipe.id)
        return componentRepository.save(component)
    }

    fun delete(id: UUID) {
        val component: Component = getComponentById(id)
        if (!securityUtils.isUserAllowed(component.recipe.createdBy)) {
            throw AccessDeniedCustomException()
        }
        componentRepository.deleteById(id)
        recipeService.updateDates(component.recipe.id)
    }

    fun addComponentStep(id: UUID, stepDTO: StepDTO): Component {
        val component: Component = getComponentById(id)
        if (!securityUtils.isUserAllowed(component.recipe.createdBy)) {
            throw AccessDeniedCustomException()
        }
        val step = Step(stepDTO, component)
        component.steps.add(step)
        recipeService.updateDates(component.recipe.id)
        return componentRepository.save(component)
    }

    fun addComponentIngredient(id: UUID, ingredientDTO: IngredientDTO): Component {
        val component: Component = getComponentById(id)
        if (!securityUtils.isUserAllowed(component.recipe.createdBy)) {
            throw AccessDeniedCustomException()
        }
        val ingredient = Ingredient(ingredientDTO, component)
        component.ingredients.add(ingredient)
        recipeService.updateDates(component.recipe.id)
        return componentRepository.save(component)
    }
}