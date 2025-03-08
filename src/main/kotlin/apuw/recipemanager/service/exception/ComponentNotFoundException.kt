package apuw.recipemanager.service.exception

import java.util.UUID

class ComponentNotFoundException(
    id: UUID,
) : RuntimeException("Component with id $id was not found")
