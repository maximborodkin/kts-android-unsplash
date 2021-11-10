package ru.maxim.unsplash.repository

import kotlinx.coroutines.flow.Flow
import ru.maxim.unsplash.domain.model.User
import ru.maxim.unsplash.util.Result

interface UserRepository {

    fun getByUsername(username: String): Flow<Result<User?>>
    fun getCurrentUser(): Flow<Result<User?>>
}