package apuw.recipemanager.repository

import apuw.recipemanager.entity.Component
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ComponentRepository : JpaRepository<Component, UUID>
