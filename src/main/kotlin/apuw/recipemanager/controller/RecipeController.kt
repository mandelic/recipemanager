package apuw.recipemanager.controller

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.RecipeDTO
import apuw.recipemanager.controller.dto.RecipeDetailsDTO
import apuw.recipemanager.service.RecipeService
import apuw.recipemanager.util.Paths.RECIPES_PATH
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping(RECIPES_PATH)
@RestController
@Tag(
    name = "Recipes Management",
    description = "Endpoints for managing recipes, including fetching, creating, updating," +
            "and deleting recipes, as well as managing the components within each recipe."
)
class RecipeController(
    private val recipeService: RecipeService
) {

    @Operation(
        summary = "Get all recipes",
        description = "Retrieve a list of all available recipes. This endpoint returns the basic details of each recipe " +
                "including the name and description."
    )
    @GetMapping
    fun getRecipes(): ResponseEntity<List<RecipeDTO>> {
        val recipeList: List<RecipeDTO> = recipeService.getAllRecipes().map{ RecipeDTO(it) }
        return ResponseEntity.ok(recipeList)
    }

    @Operation(
        summary = "Add a new recipe",
        description = "Create a new recipe by providing the necessary recipe details. " +
                "The request body should include all required information such as the recipe name, description, components, etc. " +
                "The response will return the ID of the newly created recipe."
    )
    @PostMapping
    fun addRecipe(@RequestBody recipeDetailsDTO: RecipeDetailsDTO): ResponseEntity<RecipeDetailsDTO> {
        val recipe = recipeService.save(recipeDetailsDTO)
        return ResponseEntity.created(URI.create("/api/recipes/${recipe.id}")).body(RecipeDetailsDTO(recipe))
    }

    @Operation(
        summary = "Get recipe by ID",
        description = "Retrieve the details of a recipe by its unique ID. " +
                "The response will include all the details of the recipe, such as its name, description, components, and steps."
    )
    @GetMapping("/{id}")
    fun getRecipe(@PathVariable id: UUID): ResponseEntity<RecipeDetailsDTO> {
        val recipe = recipeService.getRecipeById(id)
        return ResponseEntity.ok(RecipeDetailsDTO(recipe))
    }

    @Operation(
        summary = "Update a recipe",
        description = "Update the details of an existing recipe by its unique ID. " +
                "Provide the updated information for the recipe such as its name, description, components, etc. " +
                "The response will return the updated recipe details."
    )
    @PutMapping("/{id}")
    fun updateRecipe(
        @PathVariable id: UUID,
        @RequestBody recipeDetailsDTO: RecipeDetailsDTO
    ): ResponseEntity<RecipeDetailsDTO> {
        val updatedRecipe = recipeService.update(id, recipeDetailsDTO)
        return ResponseEntity.ok(RecipeDetailsDTO(updatedRecipe))
    }

    @Operation(
        summary = "Delete a recipe",
        description = "Delete an existing recipe by its unique ID. " +
                "This endpoint requires the user to have 'ADMIN' role. "
    )
    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: UUID): ResponseEntity<Void> {
        recipeService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Get Components of a Recipe",
        description = "Retrieves the list of components for a recipe by its ID. The components are returned as a list of `ComponentDTO` objects."
    )
    @GetMapping("/{id}/components")
    fun getRecipeComponents(@PathVariable id: UUID): ResponseEntity<List<ComponentDTO>> {
        val recipe = recipeService.getRecipeById(id)
        return ResponseEntity.ok(recipe.components.map { ComponentDTO(it) })
    }

    @Operation(
        summary = "Add a Component to a Recipe",
        description = "Adds a new component to a recipe specified by its ID. The component is passed as a `ComponentDTO` object and will be added to the recipe's component list."
    )
    @PostMapping("/{id}/components")
    fun addRecipeComponent(
        @PathVariable id: UUID,
        @RequestBody componentDTO: ComponentDTO
    ): ResponseEntity<List<ComponentDTO>> {
        val recipe = recipeService.addRecipeComponent(id, componentDTO)
        return ResponseEntity.created(URI.create("/api/recipes/$id/components")).body(recipe.components.map { ComponentDTO(it) })
    }
}