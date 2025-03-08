package apuw.recipemanager

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.RecipeDTO
import apuw.recipemanager.controller.dto.RecipeDetailsDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Recipe
import apuw.recipemanager.entity.User
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.RecipeService
import apuw.recipemanager.service.exception.AccessDeniedCustomException
import apuw.recipemanager.service.exception.RecipeNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RecipeControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var recipeService: RecipeService

    @MockkBean
    private lateinit var securityUtils: SecurityUtils

    companion object {
        val objectMapper: ObjectMapper =
            ObjectMapper()
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    final val uuid: UUID = UUID.fromString("27277c0c-242b-46de-8193-98c56776c639")
    final val uuidUser: UUID = UUID.fromString("b925ea39-61ca-4bc2-aac0-f0d2b43238c8")
    final val uuidOther: UUID = UUID.fromString("7bbf7ae3-deb6-4bee-b5ab-c778e98b534e")
    final val uuidAdmin: UUID = UUID.fromString("850a2ad4-a969-4923-930f-fff610f69f60")

    private final val mockUser: User = User(uuidUser, "user1", "pass1", "ROLE_USER")
    private final val mockUserOther: User = User(uuidOther, "user2", "pass1", "ROLE_USER")
    private final val mockAdminUser: User = User(uuidAdmin, "admin", "pass1", "ROLE_ADMIN")

    private lateinit var mockRecipe: Recipe
    private lateinit var mockRecipeDetailsDTO: RecipeDetailsDTO
    private lateinit var mockComponent: Component
    private lateinit var mockRecipeList: MutableList<Recipe>
    private lateinit var otherMockComponent: Component

    fun getMockRecipeDetailsJson(): String = objectMapper.writeValueAsString(mockRecipeDetailsDTO)

    fun getMockRecipeJson(): String = objectMapper.writeValueAsString(listOf(RecipeDTO(mockRecipe)))

    fun getMockComponentListJson(): String = objectMapper.writeValueAsString(mockRecipe.components.map { ComponentDTO(it) })

    fun getMockComponentAddedListJson(): String =
        objectMapper.writeValueAsString(
            listOf(ComponentDTO(mockComponent), ComponentDTO(otherMockComponent)),
        )

    fun getMockComponentJson(): String = objectMapper.writeValueAsString(ComponentDTO(otherMockComponent))

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        val authentication =
            UsernamePasswordAuthenticationToken(
                uuid.toString(),
                "password",
                listOf(SimpleGrantedAuthority("ROLE_USER")),
            )

        SecurityContextHolder.getContext().authentication = authentication

        mockRecipe =
            Recipe(
                uuid,
                "",
                "",
                LocalDateTime.of(2024, 12, 18, 22, 0, 4),
                LocalDateTime.of(2024, 12, 18, 22, 0, 4),
                mutableListOf(),
                mockUser,
            )

        mockComponent = Component(uuid, "", mockRecipe, mutableListOf(), mutableListOf())
        otherMockComponent = Component(uuidOther, "", mockRecipe, mutableListOf(), mutableListOf())
        mockRecipe.components.add(mockComponent)
        mockRecipeList =
            mutableListOf(
                mockRecipe,
            )

        mockRecipeDetailsDTO = RecipeDetailsDTO(mockRecipe)
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when get all recipes return all recipes`() {
        every { recipeService.getAllRecipes() } returns mockRecipeList
        mockMvc
            .perform(get("/api/recipes"))
            .andExpect(status().isOk)
            .andExpect(content().json(getMockRecipeJson()))
    }

    @Test
    fun `when add new recipe return 201`() {
        every { recipeService.save(mockRecipeDetailsDTO) } returns mockRecipe
        mockMvc
            .perform(
                post("/api/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockRecipeDetailsJson()),
            ).andExpect(status().isCreated)
            .andExpect(content().string(uuid.toString()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when get recipe by valid id, return it`() {
        every { recipeService.getRecipeById(uuid) } returns mockRecipe
        mockMvc
            .perform(get("/api/recipes/$uuid"))
            .andExpect(status().isOk)
            .andExpect(content().json(getMockRecipeDetailsJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when get recipe by invalid id, throw RecipeNotFoundException`() {
        every { recipeService.getRecipeById(uuid) } throws RecipeNotFoundException(uuid)
        mockMvc
            .perform(get("/api/recipes/$uuid"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Recipe with id $uuid was not found"))
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `when delete recipe with role admin, return 204`() {
        every { recipeService.getRecipeById(uuid) } returns mockRecipe
        every { recipeService.delete(uuid) } just Runs
        mockMvc
            .perform(delete("/api/recipes/$uuid"))
            .andExpect(status().isNoContent)
            .andExpect(content().string(""))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when update recipe by valid user, return 200`() {
        every { recipeService.getRecipeById(uuid) } returns mockRecipe
        every { recipeService.update(uuid, mockRecipeDetailsDTO) } returns mockRecipe
        mockMvc
            .perform(
                put("/api/recipes/$uuid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockRecipeDetailsJson()),
            ).andExpect(status().isOk)
            .andExpect(content().json(getMockRecipeDetailsJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when update recipe by invalid user, return 403`() {
        every { recipeService.getRecipeById(uuid) } returns mockRecipe
        every { recipeService.update(uuid, mockRecipeDetailsDTO) } throws AccessDeniedCustomException()
        mockMvc
            .perform(
                put("/api/recipes/$uuid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockRecipeDetailsJson()),
            ).andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message").value("Access denied"))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when get recipe components, return them`() {
        every { recipeService.getRecipeById(uuid) } returns mockRecipe
        mockMvc
            .perform(get("/api/recipes/$uuid/components"))
            .andExpect(status().isOk)
            .andExpect(content().json(getMockComponentListJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when add recipe components, return 201`() {
        val mockRecipeWithAddedComponent = mockRecipe
        mockRecipeWithAddedComponent.components.add(otherMockComponent)
        every { recipeService.getRecipeById(uuid) } returns mockRecipe
        every { recipeService.addRecipeComponent(uuid, ComponentDTO(otherMockComponent)) } returns mockRecipeWithAddedComponent
        mockMvc
            .perform(
                post("/api/recipes/$uuid/components")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockComponentJson()),
            ).andExpect(status().isCreated)
            .andExpect(content().json(getMockComponentAddedListJson()))
    }
}
