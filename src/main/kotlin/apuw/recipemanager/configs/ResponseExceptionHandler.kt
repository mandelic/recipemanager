package apuw.recipemanager.configs

import apuw.recipemanager.service.exception.AccessDeniedCustomException
import apuw.recipemanager.service.exception.ComponentNotFoundException
import apuw.recipemanager.service.exception.RecipeNotFoundException
import apuw.recipemanager.service.exception.UserExistsException
import apuw.recipemanager.service.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ResponseExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.message ?: "User not found.",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RecipeNotFoundException::class)
    fun handleRecipeNotFoundException(e: RecipeNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.message ?: "Recipe not found.",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ComponentNotFoundException::class)
    fun handleComponentNotFoundException(e: ComponentNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.message ?: "Recipe not found.",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserExistsException::class)
    fun handleUserExistsException(e: UserExistsException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.message ?: "User already exists.",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(AccessDeniedCustomException::class)
    fun handleAccessDeniedCustomException(e: AccessDeniedCustomException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                e.message ?: "Access denied.",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                e.message ?: "Access denied.",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.message ?: ".",
                System.currentTimeMillis(),
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }
}

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: Long,
)
