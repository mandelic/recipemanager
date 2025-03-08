package apuw.recipemanager.controller

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.IngredientDTO
import apuw.recipemanager.controller.dto.StepDTO
import apuw.recipemanager.service.ComponentService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping("/api/components")
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
        return ResponseEntity(ComponentDTO(updatedComponent), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteComponent(@PathVariable id: UUID): ResponseEntity<Void> {
        componentService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/{id}/ingredients")
    fun getComponentIngredients(@PathVariable id: UUID): ResponseEntity<List<IngredientDTO>> {
        val component = componentService.getComponentById(id)
        return ResponseEntity(component.ingredients.map { IngredientDTO(it) }, HttpStatus.OK)
    }

    @PostMapping("/{id}/ingredients")
    fun addComponentIngredient(
        @PathVariable id: UUID,
        @RequestBody ingredientDTO: IngredientDTO
    ): ResponseEntity<List<IngredientDTO>> {
        val component = componentService.addComponentIngredient(id, ingredientDTO)
        return ResponseEntity(component.ingredients.map { IngredientDTO(it) }, HttpStatus.CREATED)
    }

    @GetMapping("/{id}/steps")
    fun getComponentSteps(@PathVariable id: UUID): ResponseEntity<List<StepDTO>> {
        val component = componentService.getComponentById(id)
        return ResponseEntity(component.steps.map { StepDTO(it) }, HttpStatus.OK)
    }

    @PostMapping("/{id}/steps")
    fun addComponentStep(
        @PathVariable id: UUID,
        @RequestBody stepDTO: StepDTO
    ): ResponseEntity<List<StepDTO>> {
        val component = componentService.addComponentStep(id, stepDTO)
        return ResponseEntity(component.steps.map { StepDTO(it) }, HttpStatus.CREATED)
    }

}