package com.stasleonov.security

import com.stasleonov.database.model.RefreshToken
import com.stasleonov.database.model.User
import com.stasleonov.database.repository.RefreshTokenRepository
import com.stasleonov.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

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

        storeRefreshToken(user.id,newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refreshToken(refreshToken: String): TokenPair {
        if(!jwtService.validateAccessToken(refreshToken)){
            throw IllegalArgumentException("Invalid refresh token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            IllegalArgumentException("Invalid refresh token")
        }
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id,hashed)
            ?: throw IllegalArgumentException("Refresh token not recognized(maybe used or expired?)")
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id,hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id,newRefreshToken)

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