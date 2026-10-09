package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.UserDao
import com.aalmoghalis.muhasibsoft.data.model.User
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
            _uiState.update { it.copy(isLoading = true) }

            try {
                // التحقق من عدم تكرار اسم المستخدم
                val existing = userDao.getUserByUsername(username)
                if (existing != null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "اسم المستخدم موجود مسبقاً")
                    }
                    return@launch
                }

                userDao.insertUser(
                    User(
                        name = name,
                        username = username,
                        password = password,
                        role = role
                    )
                )

                _uiState.update {
                    it.copy(isLoading = false, successMessage = "تم إضافة المستخدم بنجاح")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userDao.updateUser(user)
                _uiState.update { it.copy(successMessage = "تم تحديث المستخدم") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleUserActive(user: User) {
        viewModelScope.launch {
            userDao.updateUser(user.copy(isActive = !user.isActive))
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            // منع حذف المستخدم الإداري الأساسي
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