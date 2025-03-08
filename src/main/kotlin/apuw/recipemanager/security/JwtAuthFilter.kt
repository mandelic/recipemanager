package apuw.recipemanager.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthFilter: OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (!authorizationHeader.isNullOrEmpty() && authorizationHeader.startsWith("Bearer ")) {
            try {
                val claims = getAllClaimsFromToken(authorizationHeader)
                val authorities = (claims["authorities"] as? List<String>)?.map { SimpleGrantedAuthority(it) } ?: emptyList()

                val auth = UsernamePasswordAuthenticationToken(
                    claims.subject,
                    null,
                    authorities
                )
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: Exception) {
                logger.warn("JWT validation failed: ${e.message}")
                SecurityContextHolder.clearContext()
            }
        } else {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun getAllClaimsFromToken(authorizationHeader: String): Claims {
        val jwtToken = authorizationHeader.replace("Bearer ", "")
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor("EpYawjHNtAFTSdyfbjl6HsANukbEn7JATt5D6H3xaHboXqBke9O+6muAuA6CKOxC".toByteArray()))
            .build()
            .parseSignedClaims(jwtToken)
            .payload
    }


}