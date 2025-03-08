package apuw.recipemanager

import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.UserRepository
import apuw.recipemanager.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    var userService: UserService = mockk()
    var userRepository: UserRepository = mockk()

    private final val mockUserList: List<User> =
        listOf(
            User("admin", "admin", "ROLE_ADMIN"),
            User("user1", "pass1", "ROLE_USER"),
            User("user2", "pass2", "ROLE_USER"),
        )

    val randomUUID: UUID = UUID.randomUUID()

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `when get all users return all users`() {
        every { userService.getAllUsers() } returns mockUserList
        mockMvc.perform(get("/api/users")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when delete user by id with user role return 403`() {
        every { userRepository.findById(randomUUID) } returns Optional.of(mockUserList[1])
        mockMvc.perform(delete("/api/users/$randomUUID")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `when delete user by id with admin role return 204`() {
        every { userRepository.findById(randomUUID) } returns Optional.of(mockUserList[1])
        mockMvc.perform(delete("/api/users/$randomUUID")).andExpect(status().isNoContent)
    }
}
