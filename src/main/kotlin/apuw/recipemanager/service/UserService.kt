package apuw.recipemanager.service

import apuw.recipemanager.controller.dto.LoginRequest
import apuw.recipemanager.controller.dto.Token
import apuw.recipemanager.controller.dto.UserDTO
import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.UserRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.exception.UserExistsException
import apuw.recipemanager.service.exception.UserNotFoundException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date
import java.util.Optional
import java.util.UUID

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val securityUtils: SecurityUtils,
) {
    @Value("\${myapp.jwtSecret}")
    lateinit var jwtSecret: String

    fun addUser(loginRequest: LoginRequest): User {
        if (userRepository.existsByUsername(loginRequest.username)) {
            throw UserExistsException(loginRequest.username)
        }
        return userRepository.save(loginRequest.toUser(passwordEncoder.encode(loginRequest.password)))
    }

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: UUID): User {
        val user = userRepository.findById(id).orElseThrow { UserNotFoundException(id) }
        securityUtils.checkUserPermission(user)
        return user
    }

    fun verifyLogin(loginRequest: LoginRequest): Token? {
        val user: Optional<User> = userRepository.findByUsername(loginRequest.username)
        if (user.isPresent) {
            val userData = user.get()
            if (passwordEncoder.matches(loginRequest.password, userData.password)) {
                return Token(userData.role, "", userData.id)
            }
        }
        return null
    }

    fun updateUser(
        id: UUID,
        userDTO: UserDTO,
    ): User {
        val user: User =
            userRepository.findById(id).orElseThrow {
                UserNotFoundException(id)
            }
        securityUtils.checkUserPermission(user)
        user.updateData(userDTO)
        return userRepository.save(user)
    }

    fun deleteUser(id: UUID) = userRepository.deleteById(id)

    fun getJwtToken(
        userId: UUID,
        role: String,
    ): String {
        val grantedAuthorities: List<GrantedAuthority> = AuthorityUtils.commaSeparatedStringToAuthorityList(role)
        return Jwts
            .builder()
            .id("recipesManagerJWT")
            .subject(userId.toString())
            .claim(
                "authorities",
                grantedAuthorities
                    .map { it.authority },
            ).issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .compact()
    }
}
