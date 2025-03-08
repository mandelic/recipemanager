package apuw.recipemanager.controller

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.IngredientDTO
import apuw.recipemanager.controller.dto.StepDTO
import apuw.recipemanager.service.ComponentService
import apuw.recipemanager.util.Paths.COMPONENTS_PATH
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping(COMPONENTS_PATH)
@RestController
@Tag(
    name = "Component Management",
    description = "APIs for managing recipe components, including CRUD operations for components," +
            "as well as handling steps and ingredients associated with each component."
)
class ComponentController(val componentService: ComponentService) {

    @PutMapping("/{id}")
    fun updateComponent(
        @PathVariable id: UUID,
        @RequestBody componentDTO: ComponentDTO
    ):ResponseEntity<ComponentDTO> {
        val updatedComponent = componentService.update(id, componentDTO)
        return ResponseEntity.ok(ComponentDTO(updatedComponent))
    }

    @DeleteMapping("/{id}")
    fun deleteComponent(@PathVariable id: UUID): ResponseEntity<Void> {
        componentService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/ingredients")
    fun getComponentIngredients(@PathVariable id: UUID): ResponseEntity<List<IngredientDTO>> {
        val component = componentService.getComponentById(id)
        return ResponseEntity.ok(component.ingredients.map { IngredientDTO(it) })
    }

    @PostMapping("/{id}/ingredients")
    fun addComponentIngredient(
        @PathVariable id: UUID,
        @RequestBody ingredientDTO: IngredientDTO
    ): ResponseEntity<List<IngredientDTO>> {
        val updatedComponent = componentService.addComponentIngredient(id, ingredientDTO)
        return ResponseEntity.created(URI.create("/api/components/$id/ingredients")).body(updatedComponent.ingredients.map { IngredientDTO(it) })
    }

    @GetMapping("/{id}/steps")
    fun getComponentSteps(@PathVariable id: UUID): ResponseEntity<List<StepDTO>> {
        val component = componentService.getComponentById(id)
        return ResponseEntity.ok(component.steps.map { StepDTO(it) })
    }

    @PostMapping("/{id}/steps")
    fun addComponentStep(
        @PathVariable id: UUID,
        @RequestBody stepDTO: StepDTO
    ): ResponseEntity<List<StepDTO>> {
        val updatedComponent = componentService.addComponentStep(id, stepDTO)
        return ResponseEntity.created(URI.create("/api/components/$id/steps")).body(updatedComponent.steps.map { StepDTO(it) })
    }

}