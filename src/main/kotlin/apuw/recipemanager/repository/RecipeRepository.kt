package apuw.recipemanager.repository

import apuw.recipemanager.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RecipeRepository : JpaRepository<Recipe, UUID>
