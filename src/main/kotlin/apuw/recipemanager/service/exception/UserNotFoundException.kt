package apuw.recipemanager.service.exception

import java.util.UUID

class UserNotFoundException(
    id: UUID,
) : RuntimeException("User with id $id was not found")
