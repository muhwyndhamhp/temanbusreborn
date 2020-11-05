package io.muhwyndham.temanbusreloaded.remote.models

import com.google.gson.annotations.SerializedName

object GeneralModels {
    data class TokenData(
        @SerializedName("token")
        val token : String? = ""
    )

    data class ErrorData(
        @SerializedName("error_code")
        val ErrorCode : String? = "",
        @SerializedName("error_message")
        val ErrorMessage : String? = "",
        @SerializedName("error_case")
        val ErrorLocalizedMessage : String? = "",
    )

    data class GeneralResponse(
        @SerializedName("error")
        val errorData: ErrorData?,
        @SerializedName("data")
        val tokenData : TokenData?
    )
}