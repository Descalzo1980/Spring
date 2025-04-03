package com.stasleonov.security

import com.stasleonov.database.model.RefreshToken
import com.stasleonov.database.model.User
import com.stasleonov.database.repository.RefreshTokenRepository
import com.stasleonov.database.repository.UserRepository
import org.apache.tomcat.util.net.openssl.ciphers.MessageDigest
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String,
    )

    fun register(email: String, password: String): User {
        return userRepository.save(
           User(
               email = email,
               hashedPassword = hashEncoder.encode(password)
           )
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("Invalid credentials")
        if(!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expireMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expireMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed,
            )
        )

    }

    private fun hashToken(token: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}