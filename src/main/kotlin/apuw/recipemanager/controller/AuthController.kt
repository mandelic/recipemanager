package apuw.recipemanager.controller

import apuw.recipemanager.controller.dto.LoginRequest
import apuw.recipemanager.controller.dto.Token
import apuw.recipemanager.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
@Tag(name = "Authentication", description = "User authentication")
class AuthController(private val userService: UserService) {

    @Operation(
        summary = "Login",
        description = "Validates the username and password, and returns a valid JWT token if they match.",
        responses = [
            ApiResponse(responseCode = "200", description = "OK",
                content = [Content(schema = Schema(type="object",
                    example="""
                            {
                              "role": "ROLE_USER",
                              "token": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJyZWNpcGVzTWFuYWdlckpXVCIsInN1YiI6Im1hcmlqYS5hbmRlbGljMDJAZ21haWwuY29tIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTczNDQ1NzkxNn0.bhw4yKbC3zKxgKhYOXLrV5l9jVJRgr_7j2xxOqdbtOIglzwsHrWU93dAzVJMM52bcSKlwl_ngXLbOT08L0v9mw",
                              "userId": "4d4969c5-7751-47a8-957d-5578b315d0cb"
                            }
                    """))]),
            ApiResponse(responseCode = "400", description = "Bad request",
                content = [Content(schema = Schema(type="object", example=""))]),
            ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = [Content(schema = Schema(type="object", example=""))]),
            ApiResponse(responseCode = "500", description = "Internal server error",
                content = [Content(schema = Schema(type="object", example=""))]),
        ]
    )
    @PostMapping("/auth/login")
    fun login(@Validated @RequestBody loginRequest: LoginRequest): ResponseEntity<Token?> {
        val tokenDTO: Token = userService.verifyLogin(loginRequest)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        tokenDTO.token = userService.getJwtToken(tokenDTO.userId, tokenDTO.role)
        return ResponseEntity.ok(tokenDTO)
    }

    @Operation(
        summary = "Register",
        description = "Creates a new user.",
        responses = [
            ApiResponse(responseCode = "201", description = "Created"),
            ApiResponse(responseCode = "400", description = "Bad request",
                content = [Content(schema = Schema(type="object", example=""))]),
            ApiResponse(responseCode = "409", description = "User already exists",
                content = [Content(schema = Schema(type="object", example=""))]),
            ApiResponse(responseCode = "500", description = "Internal server error",
                content = [Content(schema = Schema(type="object", example=""))]),
        ]
    )
    @PostMapping("/auth/register")
    fun register(@Validated @RequestBody loginRequest: LoginRequest): ResponseEntity<String> {
        val user = userService.addUser(loginRequest)
        return ResponseEntity(user.id.toString(), HttpStatus.CREATED)
    }

}