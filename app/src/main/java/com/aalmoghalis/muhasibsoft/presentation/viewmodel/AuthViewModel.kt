package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.UserDao
import com.aalmoghalis.muhasibsoft.data.model.User
import com.aalmoghalis.muhasibsoft.utils.PasswordUtils
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

    /** تسجيل الدخول مع ترحيل كلمات المرور القديمة إلى صيغة مجزأة عند نجاح الدخول. */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userDao.getUserByUsername(username.trim())
                when {
                    user == null -> _uiState.update {
                        it.copy(isLoading = false, error = "اسم المستخدم غير موجود")
                    }
                    !PasswordUtils.verify(password, user.password) -> _uiState.update {
                        it.copy(isLoading = false, error = "كلمة المرور غير صحيحة")
                    }
                    !user.isActive -> _uiState.update {
                        it.copy(isLoading = false, error = "الحساب غير مفعل، راجع مدير النظام")
                    }
                    else -> {
                        val secureUser = if (PasswordUtils.isHashed(user.password)) user
                        else user.copy(password = PasswordUtils.hash(password)).also {
                            userDao.updateUser(it)
                        }
                        currentUser = secureUser
                        _uiState.update {
                            it.copy(isLoading = false, isLoggedIn = true, currentUser = secureUser)
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

    /** لا تسمح بتجاوز المصادقة. */
    fun skipLogin() {
        _uiState.update {
            it.copy(isLoggedIn = false, error = "يرجى تسجيل الدخول باستخدام حسابك")
        }
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
