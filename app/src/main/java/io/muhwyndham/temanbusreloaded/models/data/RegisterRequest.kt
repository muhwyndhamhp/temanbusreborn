package io.muhwyndham.temanbusreloaded.models.data

import com.esafirm.imagepicker.model.Image

data class RegisterRequest(
    val displayName: String,
    val displayProfileImage: Image?,
    val email: String,
    val password: String,
    val phoneNumber: String
)