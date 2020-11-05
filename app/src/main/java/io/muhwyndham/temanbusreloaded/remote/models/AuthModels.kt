package io.muhwyndham.temanbusreloaded.remote.models

import com.google.gson.annotations.SerializedName

object AuthModels {

    data class LoginRequest(
        @SerializedName("email")
        val email : String? = "",
        @SerializedName("password")
        val password : String? = ""
    )
    data class RegisterRequest(
        @SerializedName("first_name")
        val firstName : String? = "",
        @SerializedName("last_name")
        val lastName : String? = "",
        @SerializedName("email")
        val email : String? = "",
        @SerializedName("phone")
        val phone : String? = "",
        @SerializedName("password")
        val password : String? = "",
        @SerializedName("repeat_password")
        val repeatPassword : String? = "",
        @SerializedName("fcm_token")
        val fcmToken : String? = ""
    )

    data class VerifyRegisterRequest(
        @SerializedName("code")
        val verificationCode : String? = "",
        @SerializedName("token")
        val token : String? = ""
    )

}