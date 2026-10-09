package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.UserDao
import com.aalmoghalis.muhasibsoft.data.model.User
import com.aalmoghalis.muhasibsoft.utils.PasswordUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()
    val users: StateFlow<List<User>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addUser(name: String, username: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val cleanUsername = username.trim()
                if (cleanUsername.isEmpty() || password.isEmpty()) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "اسم المستخدم وكلمة المرور مطلوبان")
                    }
                    return@launch
                }
                if (userDao.getUserByUsername(cleanUsername) != null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "اسم المستخدم موجود مسبقاً")
                    }
                    return@launch
                }
                userDao.insertUser(
                    User(
                        name = name.trim(),
                        username = cleanUsername,
                        password = PasswordUtils.hash(password),
                        role = role
                    )
                )
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "تم إضافة المستخدم بنجاح")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "تعذر إضافة المستخدم")
                }
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                val secureUser = if (PasswordUtils.isHashed(user.password)) user
                else user.copy(password = PasswordUtils.hash(user.password))
                userDao.updateUser(secureUser)
                _uiState.update { it.copy(successMessage = "تم تحديث المستخدم") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "تعذر تحديث المستخدم") }
            }
        }
    }

    fun toggleUserActive(user: User) {
        viewModelScope.launch { userDao.updateUser(user.copy(isActive = !user.isActive)) }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            val user = userDao.getUserById(userId)
            if (user?.role == "admin" && user.id == 1L) {
                _uiState.update { it.copy(error = "لا يمكن حذف مدير النظام الأساسي") }
                return@launch
            }
            userDao.deleteUser(userId)
            _uiState.update { it.copy(successMessage = "تم حذف المستخدم") }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}

data class UsersUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)
