package io.muhwyndham.temanbusreloaded.ui.auth

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esafirm.imagepicker.model.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import io.muhwyndham.temanbusreloaded.models.data.RegisterRequest
import io.muhwyndham.temanbusreloaded.models.ui.State
import io.muhwyndham.temanbusreloaded.utils.Constants.ERROR
import io.muhwyndham.temanbusreloaded.utils.Constants.LOADING
import io.muhwyndham.temanbusreloaded.utils.Constants.LOGIN_FAILED
import io.muhwyndham.temanbusreloaded.utils.Constants.LOGIN_SUCCESS
import io.muhwyndham.temanbusreloaded.utils.Constants.REGISTER_FAILED
import io.muhwyndham.temanbusreloaded.utils.Constants.REGISTER_SUCCESS
import io.muhwyndham.temanbusreloaded.utils.Constants.UPDATE_DATA_FAILED
import io.muhwyndham.temanbusreloaded.utils.Constants.UPLOAD_PROFILE_FAILED
import java.io.File

class AuthViewModel @ViewModelInject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ViewModel() {
    val currentUser = MutableLiveData(auth.currentUser)

    val appState = MutableLiveData(State())

    fun authWithEmail(email: String, password: String) {
        appState.postValue(
            State(
                LOADING,
                "Mohon tunggu proses masuk..."
            )
        )
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
        if (registerRequest.displayProfileImage != null) {
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
                    is Uri -> {
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
        } else
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
    ) {
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
                            displayName =
                                registerRequest.displayName + "-" + registerRequest.phoneNumber
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





