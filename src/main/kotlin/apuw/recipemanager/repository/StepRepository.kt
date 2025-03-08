package apuw.recipemanager.repository

import apuw.recipemanager.entity.Step
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StepRepository : JpaRepository<Step, UUID>
