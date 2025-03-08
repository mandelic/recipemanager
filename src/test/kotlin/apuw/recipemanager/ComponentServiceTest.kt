package apuw.recipemanager.apuw.recipemanager

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.IngredientDTO
import apuw.recipemanager.controller.dto.StepDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Ingredient
import apuw.recipemanager.entity.Recipe
import apuw.recipemanager.entity.Step
import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.ComponentRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.ComponentService
import apuw.recipemanager.service.RecipeService
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ComponentServiceTest {
    private lateinit var componentService: ComponentService

    @MockK
    private lateinit var componentRepository: ComponentRepository

    @MockK
    private lateinit var recipeService: RecipeService

    @MockK
    private lateinit var securityUtils: SecurityUtils

    final val uuid: UUID = UUID.fromString("27277c0c-242b-46de-8193-98c56776c639")
    final val uuidUser: UUID = UUID.fromString("b925ea39-61ca-4bc2-aac0-f0d2b43238c8")

    private final val mockUser: User = User(uuidUser, "user1", "pass1", "ROLE_USER")

    private lateinit var mockComponent: Component
    private lateinit var mockIngredient: Ingredient
    private lateinit var mockStep: Step

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        componentService = ComponentService(componentRepository, recipeService, securityUtils)
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
    fun `getComponentById() returns correct component`() {
        every { componentRepository.findById(uuid) } returns Optional.of(mockComponent)
        val result = componentService.getComponentById(uuid)
        assertEquals(mockComponent, result)
    }

    @Test
    fun `update() with valid user updates component in repository`() {
        every { componentRepository.findById(uuid) } returns Optional.of(mockComponent)
        every { componentRepository.save(any()) } returns mockComponent
        every { securityUtils.checkUserPermission(any()) } returns Unit
        every { recipeService.updateDates(any()) } just Runs
        val result = componentService.update(uuid, ComponentDTO(mockComponent))
        assertEquals(mockComponent, result)
        verify { componentRepository.findById(uuid) }
        verify { componentRepository.save(any()) }
    }

    @Test
    fun `delete() with valid user deletes component in repository`() {
        every { componentRepository.findById(uuid) } returns Optional.of(mockComponent)
        every { componentRepository.deleteById(any()) } just Runs
        every { securityUtils.checkUserPermission(any()) } returns Unit
        every { recipeService.updateDates(any()) } just Runs
        componentService.delete(uuid)
        verify { componentRepository.deleteById(uuid) }
    }

    @Test
    fun `addComponentStep() with valid user adds step to component`() {
        every { componentRepository.findById(uuid) } returns Optional.of(mockComponent)
        every { componentRepository.save(any()) } returns mockComponent
        every { securityUtils.checkUserPermission(any()) } returns Unit
        every { recipeService.updateDates(any()) } just Runs
        val component = componentService.addComponentStep(uuid, StepDTO(mockStep))
        val mockComponentAdded = mockComponent
        mockComponentAdded.steps.add(mockStep)
        assertEquals(mockComponentAdded, component)
        verify { componentRepository.save(any()) }
    }

    @Test
    fun `addComponentIngredient() with valid user adds ingredient to component`() {
        every { componentRepository.findById(uuid) } returns Optional.of(mockComponent)
        every { componentRepository.save(any()) } returns mockComponent
        every { securityUtils.checkUserPermission(any()) } returns Unit
        every { recipeService.updateDates(any()) } just Runs
        val component = componentService.addComponentIngredient(uuid, IngredientDTO(mockIngredient))
        val mockComponentAdded = mockComponent
        mockComponentAdded.ingredients.add(mockIngredient)
        assertEquals(mockComponentAdded, component)
        verify { componentRepository.save(any()) }
    }
}
