package apuw.recipemanager.service

import apuw.recipemanager.controller.dto.LoginRequest
import apuw.recipemanager.controller.dto.Token
import apuw.recipemanager.controller.dto.UserDTO
import apuw.recipemanager.entity.User
import apuw.recipemanager.repository.UserRepository
import apuw.recipemanager.security.SecurityUtils
import apuw.recipemanager.service.exception.AccessDeniedCustomException
import apuw.recipemanager.service.exception.UserExistsException
import apuw.recipemanager.service.exception.UserNotFoundException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val securityUtils: SecurityUtils,
) {
    fun addUser(loginRequest: LoginRequest): User {
        if (userRepository.existsByUsername(loginRequest.username))
            throw UserExistsException(loginRequest.username)
        return userRepository.save(loginRequest.toUser(passwordEncoder.encode(loginRequest.password)))
    }

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: UUID): User {
        val user = userRepository.findById(id).orElseThrow{ UserNotFoundException(id) }
        if (!securityUtils.isUserAllowed(user)) {
            throw AccessDeniedCustomException()
        }
        return user
    }

    fun verifyLogin(loginRequest: LoginRequest): Token? {
        val user: Optional<User> = userRepository.findByUsername(loginRequest.username)
        if (user.isPresent) {
            val userData = user.get();
            if (passwordEncoder.matches(loginRequest.password, userData.password))
                return Token(userData.role, "", userData.id);
        }
        return null
    }

    fun updateUser(id: UUID, userDTO: UserDTO): User {
        val user: User = userRepository.findById(id).orElseThrow{
            UserNotFoundException(id)
        }
        if (!securityUtils.isUserAllowed(user)) {
            throw AccessDeniedCustomException()
        }
        user.updateData(userDTO)
        return userRepository.save(user)
    }

    fun deleteUser(id: UUID) = userRepository.deleteById(id)

    fun getJwtToken(
        userId: UUID,
        role: String
    ): String {
        val grantedAuthorities: List<GrantedAuthority> = AuthorityUtils.commaSeparatedStringToAuthorityList(role)
        return Jwts.builder()
            .id("recipesManagerJWT")
            .subject(userId.toString())
            .claim("authorities", grantedAuthorities
                .map { it.authority })
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(Keys.hmacShaKeyFor("EpYawjHNtAFTSdyfbjl6HsANukbEn7JATt5D6H3xaHboXqBke9O+6muAuA6CKOxC".toByteArray()))
            .compact()
    }
}