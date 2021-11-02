package ru.maxim.unsplash.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.User
import ru.maxim.unsplash.network.model.UserDto
import ru.maxim.unsplash.network.service.UserService
import ru.maxim.unsplash.persistence.PreferencesManager
import ru.maxim.unsplash.persistence.dao.UserDao
import ru.maxim.unsplash.persistence.model.UserEntity
import ru.maxim.unsplash.util.networkBoundResource
import ru.maxim.unsplash.util.Result

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userService: UserService,
    private val userEntityMapper: DomainMapper<UserEntity, User>,
    private val userDtoMapper: DomainMapper<UserDto, User>,
    private val preferencesManager: PreferencesManager
) : UserRepository {

    override fun getByUsername(username: String): Flow<Result<User?>> =
        networkBoundResource(
            query = {
                userDao.getByUsername(username).map { userEntity ->
                    userEntity?.let { userEntityMapper.toDomainModel(it) }
                }
            },
            fetch = {
                userService.getByUsername(username)
            },
            cacheFetchResult = { response ->
                val domainModel = userDtoMapper.toDomainModel(response)
                userDao.insert(userEntityMapper.fromDomainModel(domainModel))
            },
            shouldFetch = { true }
        )

    override fun getCurrentUser(): Flow<Result<User?>> =
        networkBoundResource(
            query = {
                val currentUserUsername = preferencesManager.currentUserUsername
                if (currentUserUsername.isNullOrBlank()) {
                    flowOf(null)
                } else {
                    userDao.getByUsername(currentUserUsername).map { userEntity ->
                        userEntity?.let { userEntityMapper.toDomainModel(it) }
                    }
                }
            },
            fetch = {
                userService.getCurrentUser()
            },
            cacheFetchResult = { response ->
                val domainModel = userDtoMapper.toDomainModel(response)
                userDao.insert(userEntityMapper.fromDomainModel(domainModel))
                preferencesManager.currentUserUsername = domainModel.username
            },
            shouldFetch = { true }
        )
}