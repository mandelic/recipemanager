package apuw.recipemanager.apuw.recipemanager

import apuw.recipemanager.controller.dto.LoginRequest
import apuw.recipemanager.controller.dto.Token
import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.UserRepository
import apuw.recipemanager.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional
import java.util.UUID
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userService: UserService
    var userRepository: UserRepository = mockk()

    final val uuid: UUID = UUID.fromString("ba051709-2c33-4d05-a935-4ec0e841c28e")
    val mockAdminToken = Token("ROLE_ADMIN", "token", uuid)
    val mockLoginRequest = LoginRequest("admin", "admin")

    private final val mockUserList: List<User> =
        listOf(
            User(uuid, "admin", "admin", "ROLE_ADMIN"),
            User(uuid, "user1", "pass1", "ROLE_USER"),
            User(uuid, "user2", "pass2", "ROLE_USER"),
        )

    fun getMockAdminTokenJson() = objectMapper.writeValueAsString(mockAdminToken)

    fun getMockLoginRequestJson() = objectMapper.writeValueAsString(mockLoginRequest)

    companion object {
        val objectMapper: ObjectMapper =
            ObjectMapper()
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun `when login verified return valid token`() {
        every { userService.verifyLogin(mockLoginRequest) } returns mockAdminToken
        every { userService.getJwtToken(mockAdminToken.userId, mockAdminToken.role) } returns "token"
        every { userRepository.findByUsername("admin") } returns Optional.of(mockUserList[0])
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockLoginRequestJson()),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(getMockAdminTokenJson()))
    }

    @Test
    fun `when login not verified return access denied`() {
        every { userService.verifyLogin(mockLoginRequest) } returns null
        every { userRepository.findByUsername("admin") } returns Optional.empty()
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockLoginRequestJson()),
            ).andExpect(status().isUnauthorized)
            .andExpect(content().string(""))
    }

    @Test
    fun `when register successful return newly created user id`() {
        every { userService.addUser(mockLoginRequest) } returns mockUserList[0]
        every { userService.getJwtToken(mockAdminToken.userId, mockAdminToken.role) } returns "token"
        every { userRepository.save(mockUserList[0]) } returns mockUserList[0]
        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockLoginRequestJson()),
            ).andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
            .andExpect(content().string(mockUserList[0].id.toString()))
    }
}
