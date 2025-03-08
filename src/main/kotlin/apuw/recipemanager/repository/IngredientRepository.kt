package apuw.recipemanager.repository

import apuw.recipemanager.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface IngredientRepository: JpaRepository<Ingredient, UUID> {
}