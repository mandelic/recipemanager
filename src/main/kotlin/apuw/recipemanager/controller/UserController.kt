package apuw.recipemanager.controller

import apuw.recipemanager.controller.dto.UserDTO
import apuw.recipemanager.service.UserService
import apuw.recipemanager.util.Paths.USERS_PATH
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(USERS_PATH)
@RestController
@Tag(
    name = "User Management",
    description = "Endpoints for managing user accounts, including retrieving, updating," +
            "and deleting user information."
)
class UserController(private val userService: UserService) {

    @Operation(
        summary = "Get All Users",
        description = "Retrieves a list of all users in the system. Only available to users with ROLE_ADMIN."
    )
    @GetMapping
    fun listUsers(): ResponseEntity<List<UserDTO>> {
        val userList: List<UserDTO> = userService.getAllUsers().map{ UserDTO(it) }
        return ResponseEntity.ok(userList)
    }

    @Operation(
        summary = "Get User by ID",
        description = "Fetches a user by their unique ID. The user must be authenticated with either the 'ADMIN' or 'USER'" +
                " role to access this endpoint."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    fun findUserById(@PathVariable id: UUID): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(UserDTO(userService.getUserById(id)))
    }

    @Operation(
        summary = "Update User by ID",
        description = "Updates the user with the given ID. The user must be authenticated with either the 'ADMIN' or " +
                "'USER' role to access this endpoint. The request body should contain the user data to be updated."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody userDTO: UserDTO
    ): ResponseEntity<UserDTO> {
        val updatedUser = userService.updateUser(id, userDTO)
        return ResponseEntity.ok(UserDTO(updatedUser))
    }

    @Operation(
        summary = "Delete User by ID",
        description = "Deletes the user with the specified ID. Only users with the 'ADMIN' role can delete an account."
    )
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}