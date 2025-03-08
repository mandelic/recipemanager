package apuw.recipemanager

import apuw.recipemanager.service.StepIngredientService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class StepIngredientController {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stepIngredientService: StepIngredientService

    val uuid: UUID = UUID.randomUUID()

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when delete ingredient by valid user, return 204`() {
        every { stepIngredientService.deleteIngredientById(uuid) } just Runs
        mockMvc.perform(
            delete("/api/ingredients/$uuid"),
        )
            .andExpect(status().isNoContent)
            .andExpect(content().string(""))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when delete step by valid user, return 204`() {
        every { stepIngredientService.deleteStepById(uuid) } just Runs
        mockMvc.perform(
            delete("/api/steps/$uuid"),
        )
            .andExpect(status().isNoContent)
            .andExpect(content().string(""))
    }
}
