package apuw.recipemanager.apuw.recipemanager

import apuw.recipemanager.controller.dto.ComponentDTO
import apuw.recipemanager.controller.dto.RecipeDetailsDTO
import apuw.recipemanager.entity.Component
import apuw.recipemanager.entity.Recipe
import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.RecipeRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.RecipeService
import apuw.recipemanager.service.exception.AccessDeniedCustomException
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
class RecipeServiceTest {
    private lateinit var recipeService: RecipeService

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @MockK
    private lateinit var securityUtils: SecurityUtils

    private lateinit var mockRecipe: Recipe
    private lateinit var mockComponent: Component
    private lateinit var otherMockComponent: Component
    private lateinit var mockRecipeList: MutableList<Recipe>
    private lateinit var mockRecipeDetailsDTO: RecipeDetailsDTO
    private val uuid: UUID = UUID.fromString("27277c0c-242b-46de-8193-98c56776c639")
    private val uuidOther: UUID = UUID.fromString("b925ea39-61ca-4bc2-aac0-f0d2b43238c8")
    private final val mockUser: User = User(uuid, "user1", "pass1", "ROLE_USER")

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        recipeService = RecipeService(recipeRepository, securityUtils)
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
    fun `getAllRecipes() returns list of all recipes`() {
        every { recipeRepository.findAll() } returns mockRecipeList
        val result = recipeService.getAllRecipes()
        assertEquals(mockRecipeList, result)
    }

    @Test
    fun `getRecipeById() returns correct recipe`() {
        every { recipeRepository.findById(uuid) } returns Optional.of(mockRecipe)
        val result = recipeService.getRecipeById(uuid)
        assertEquals(mockRecipe, result)
    }

    @Test
    fun `save() saves new recipe in the repository`() {
        every { recipeRepository.save(any()) } returns mockRecipe
        every { securityUtils.getCurrentUser() } returns mockUser
        val result = recipeService.save(mockRecipeDetailsDTO)
        assertEquals(mockRecipe, result)
        verify { recipeRepository.save(any()) }
    }

    @Test
    fun `update() with valid user updates recipe in repository`() {
        every { recipeRepository.findById(uuid) } returns Optional.of(mockRecipe)
        every { recipeRepository.save(any()) } returns mockRecipe
        every { securityUtils.checkUserPermission(any()) } returns Unit
        val result = recipeService.update(uuid, mockRecipeDetailsDTO)
        assertEquals(mockRecipe, result)
        verify { recipeRepository.findById(uuid) }
        verify { recipeRepository.save(any()) }
    }

    @Test
    fun `update() with invalid user throws access denied exception`() {
        every { recipeRepository.findById(uuid) } returns Optional.of(mockRecipe)
        every { recipeRepository.save(any()) } returns mockRecipe
        every { securityUtils.checkUserPermission(any()) } throws AccessDeniedCustomException()
        assertThrows(AccessDeniedCustomException::class.java) {
            recipeService.update(uuid, mockRecipeDetailsDTO)
        }
    }

    @Test
    fun `delete() with valid user deletes recipe in repository`() {
        every { recipeRepository.findById(uuid) } returns Optional.of(mockRecipe)
        every { recipeRepository.deleteById(any()) } just Runs
        every { securityUtils.checkUserPermission(any()) } returns Unit
        recipeService.delete(uuid)
        verify { recipeRepository.deleteById(uuid) }
    }

    @Test
    fun `addRecipeComponent() with valid adds component to recipe`() {
        every { recipeRepository.findById(uuid) } returns Optional.of(mockRecipe)
        every { recipeRepository.save(any()) } returns mockRecipe
        every { securityUtils.checkUserPermission(any()) } returns Unit
        val recipe = recipeService.addRecipeComponent(uuid, ComponentDTO(mockComponent))
        assert(recipe == mockRecipe)
        verify { recipeRepository.findById(uuid) }
        verify { recipeRepository.save(any()) }
    }
}
