package io.muhwyndham.temanbusreloaded.auth

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esafirm.imagepicker.model.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.ERROR
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.LOADING
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.LOGIN_FAILED
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.LOGIN_SUCCESS
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.NOTHING
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.REGISTER_FAILED
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.REGISTER_SUCCESS
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.UPDATE_DATA_FAILED
import io.muhwyndham.temanbusreloaded.auth.ErrorConstants.UPLOAD_PROFILE_FAILED
import java.io.File

class AuthViewModel @ViewModelInject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ViewModel() {
    val currentUser = MutableLiveData(auth.currentUser)

    val appState = MutableLiveData(State())

    fun authWithEmail(email: String, password: String) {
        appState.postValue(State(
            LOADING,
            "Mohon tunggu proses masuk..."
        ))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    appState.postValue(
                        State(
                            LOGIN_SUCCESS,
                            "Berhasil masuk!"
                        )
                    )
                    currentUser.postValue(auth.currentUser)
                } else {
                    appState.postValue(
                        State(
                            LOGIN_FAILED,
                            "Email atau password salah!",
                            task.exception?.message ?: ERROR
                        )
                    )
                }
            }
    }

    fun registerWithEmail(registerRequest: RegisterRequest) {
        if(registerRequest.displayProfileImage != null)
        {
            appState.postValue(
                State(
                    LOADING,
                    "Mengunggah foto profil..."
                )
            )
            uploadProfileImage(
                registerRequest.displayProfileImage,
                registerRequest.email
            ) { uploadTask, url ->
                when (uploadTask) {
                    is UploadTask.TaskSnapshot -> {
                        doRegisterAction(registerRequest, url)
                    }
                    is Exception -> {
                        appState.postValue(
                            State(
                                UPLOAD_PROFILE_FAILED,
                                "Gagal mengunggah data pengguna, silahkan coba lagi",
                                uploadTask.message ?: ERROR
                            )
                        )
                    }
                }
            }
        }
        else
            doRegisterAction(registerRequest, null)
    }

    private fun uploadProfileImage(
        image: Image,
        email: String,
        eventListener: (Any, Uri?) -> Unit
    ) {
        val storageRef = storage.reference
        val profileRef =
            storageRef.child("/profile/${System.currentTimeMillis()}_${email.substringBefore("@")}")

        val file = Uri.fromFile(File(image.path))
        var url: Uri? = null
        profileRef.putFile(file)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                profileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    url = task.result
                } else {
                    appState.postValue(
                        State(
                            UPLOAD_PROFILE_FAILED,
                            "Gagal mengunggah data pengguna, silahkan coba lagi",
                            task.exception?.message ?: ERROR
                        )
                    )
                }
            }
            .addOnSuccessListener {
                eventListener.invoke(it, url)
            }
            .addOnFailureListener {
                eventListener.invoke(it, url)
            }
    }

    private fun updateProfileData(updateProfileRequest: UserProfileChangeRequest) {
        appState.postValue(
            State(
                LOADING,
                "Memperbaharui profil pengguna..."
            )
        )
        auth.currentUser!!.updateProfile(updateProfileRequest)
            .addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    appState.postValue(
                        State(
                            REGISTER_SUCCESS,
                            "Berhasil mendaftarkan pengguna!"
                        )
                    )
                    currentUser.postValue(auth.currentUser)
                } else {
                    appState.postValue(
                        State(
                            UPDATE_DATA_FAILED,
                            "Gagal mengunggah data pengguna, silahkan coba lagi",
                            updateTask.exception?.message ?: ERROR
                        )
                    )
                }
            }
    }

    private fun doRegisterAction(
        registerRequest: RegisterRequest,
        url: Uri?
    )
    {
        appState.postValue(
            State(
                LOADING,
                "Mendaftarkan pengguna baru..."
            )
        )
        auth.createUserWithEmailAndPassword(registerRequest.email, registerRequest.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateProfileData(
                        userProfileChangeRequest {
                            displayName = registerRequest.displayName + "-" + registerRequest.phoneNumber
                            photoUri = url
                        }
                    )

                } else {
                    appState.postValue(
                        State(
                            REGISTER_FAILED,
                            "Gagal mendaftarkan pengguna, silahkan coba lagi",
                            task.exception?.message ?: ERROR
                        )
                    )
                }
            }
    }
}

object ErrorConstants {
    const val LOADING = "loading"
    const val LOGIN_FAILED = "login_failed"
    const val LOGIN_SUCCESS = "login_success"
    const val REGISTER_FAILED = "register_failed"
    const val REGISTER_SUCCESS = "register_success"
    const val UPDATE_DATA_FAILED = "update_data_failed"
    const val UPLOAD_PROFILE_FAILED = "upload_profile_failed"
    const val GENERAL_ERROR = "general_error"
    const val ERROR = "Error"
    const val NOTHING = "None"
}

data class RegisterRequest(
    val displayName: String,
    val displayProfileImage: Image?,
    val email: String,
    val password: String,
    val phoneNumber: String
)

data class State(
    val stateCode: String? = NOTHING,
    val message: String? = "",
    val loggerMessage: String? = NOTHING
)