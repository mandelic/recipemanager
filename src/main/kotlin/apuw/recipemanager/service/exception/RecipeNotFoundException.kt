package apuw.recipemanager.service.exception

import java.util.UUID

class RecipeNotFoundException(
    id: UUID,
) : RuntimeException("Recipe with id $id was not found")
