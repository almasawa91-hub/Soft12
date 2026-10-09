package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.UserDao
import com.aalmoghalis.muhasibsoft.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var currentUser: User? = null

    /**
     * تسجيل الدخول بالتحقق من قاعدة البيانات
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val user = userDao.getUserByUsername(username)

                when {
                    user == null -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "اسم المستخدم غير موجود"
                            )
                        }
                    }
                    user.password != password -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "كلمة المرور غير صحيحة"
                            )
                        }
                    }
                    !user.isActive -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "الحساب غير مفعل، راجع مدير النظام"
                            )
                        }
                    }
                    else -> {
                        currentUser = user
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                currentUser = user
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "حدث خطأ غير متوقع")
                }
            }
        }
    }

    /**
     * تسجيل الدخول السريع (تخطي)
     */
    fun skipLogin() {
        _uiState.update { it.copy(isLoggedIn = true) }
    }

    fun logout() {
        currentUser = null
        _uiState.update { AuthUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null
)