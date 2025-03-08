package apuw.recipemanager

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.IngredientDTO
import apuw.recipemanager.controller.dto.StepDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Ingredient
import apuw.recipemanager.entity.Recipe
import apuw.recipemanager.entity.Step
import apuw.recipemanager.entity.User
import apuw.recipemanager.service.ComponentService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ComponentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var componentService: ComponentService

    companion object {
        val objectMapper: ObjectMapper =
            ObjectMapper()
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    final val uuid: UUID = UUID.fromString("27277c0c-242b-46de-8193-98c56776c639")
    final val uuidUser: UUID = UUID.fromString("b925ea39-61ca-4bc2-aac0-f0d2b43238c8")

    private final val mockUser: User = User(uuidUser, "user1", "pass1", "ROLE_USER")

    private lateinit var mockComponent: Component
    private lateinit var mockIngredient: Ingredient
    private lateinit var mockStep: Step

    fun getMockComponentJson(): String = objectMapper.writeValueAsString(ComponentDTO(mockComponent))

    fun getMockIngredientJson(): String = objectMapper.writeValueAsString(IngredientDTO(mockIngredient))

    fun getMockStepJson(): String = objectMapper.writeValueAsString(StepDTO(mockStep))

    fun getMockIngredientListJson(): String = objectMapper.writeValueAsString(listOf(IngredientDTO(mockIngredient)))

    fun getMockStepListJson(): String = objectMapper.writeValueAsString(listOf(StepDTO(mockStep)))

    fun getMockIngredientListNewJson(): String =
        objectMapper.writeValueAsString(
            listOf(IngredientDTO(mockIngredient), IngredientDTO(mockIngredient)),
        )

    fun getMockStepListNewJson(): String =
        objectMapper.writeValueAsString(
            listOf(StepDTO(mockStep), StepDTO(mockStep)),
        )

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

        val mockRecipe =
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
        mockIngredient = Ingredient(uuid, "", 1F, "", mockComponent)
        mockStep = Step(uuid, 1, "", mockComponent)
        mockComponent.ingredients.add(mockIngredient)
        mockComponent.steps.add(mockStep)
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when update component by valid user, return 200`() {
        every { componentService.update(uuid, ComponentDTO(mockComponent)) } returns mockComponent
        mockMvc
            .perform(
                put("/api/components/$uuid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockComponentJson()),
            ).andExpect(status().isOk)
            .andExpect(content().json(getMockComponentJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when delete component by valid user, return 204`() {
        every { componentService.delete(uuid) } just Runs
        mockMvc
            .perform(
                delete("/api/components/$uuid"),
            ).andExpect(status().isNoContent)
            .andExpect(content().string(""))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when get all component ingredients, return 200`() {
        every { componentService.getComponentById(uuid) } returns mockComponent
        mockMvc
            .perform(
                get("/api/components/$uuid/ingredients"),
            ).andExpect(status().isOk)
            .andExpect(content().json(getMockIngredientListJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when get all component steps, return 200`() {
        every { componentService.getComponentById(uuid) } returns mockComponent
        mockMvc
            .perform(
                get("/api/components/$uuid/steps"),
            ).andExpect(status().isOk)
            .andExpect(content().json(getMockStepListJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when add component ingredient, return 204`() {
        val mockComponentWithAddedIngr = mockComponent
        mockComponentWithAddedIngr.ingredients.add(mockIngredient)
        every { componentService.getComponentById(uuid) } returns mockComponent
        every { componentService.addComponentIngredient(uuid, IngredientDTO(mockIngredient)) } returns mockComponentWithAddedIngr
        mockMvc
            .perform(
                post("/api/components/$uuid/ingredients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockIngredientJson()),
            ).andExpect(status().isCreated)
            .andExpect(content().json(getMockIngredientListNewJson()))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when add component step, return 204`() {
        val mockComponentWithAddedStep = mockComponent
        mockComponentWithAddedStep.steps.add(mockStep)
        every { componentService.getComponentById(uuid) } returns mockComponent
        every { componentService.addComponentStep(uuid, StepDTO(mockStep)) } returns mockComponentWithAddedStep
        mockMvc
            .perform(
                post("/api/components/$uuid/steps")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getMockStepJson()),
            ).andExpect(status().isCreated)
            .andExpect(content().json(getMockStepListNewJson()))
    }
}
