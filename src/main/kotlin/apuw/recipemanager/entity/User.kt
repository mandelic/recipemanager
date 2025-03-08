package apuw.recipemanager.entity

import apuw.recipemanager.controller.dto.UserDTO
import jakarta.persistence.Entity
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])],
)
class User(
    @Id
    var id: UUID = UUID.randomUUID(),
    var username: String,
    var password: String,
    var role: String,
) {
    constructor(username: String, password: String, role: String) : this(
        UUID.randomUUID(),
        username,
        password,
        role,
    )

    fun updateData(userDTO: UserDTO) {
        this.username = userDTO.username
    }
}
