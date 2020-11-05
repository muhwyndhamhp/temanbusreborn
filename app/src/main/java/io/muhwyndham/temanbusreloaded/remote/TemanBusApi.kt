package io.muhwyndham.temanbusreloaded.remote

import io.muhwyndham.temanbusreloaded.remote.models.GeneralModels
import io.muhwyndham.temanbusreloaded.remote.models.AuthModels
import retrofit2.http.Body
import retrofit2.http.POST

interface TemanBusApi {

    @POST("user/login")
    fun userLogin(@Body loginRequest: AuthModels.LoginRequest): GeneralModels.GeneralResponse

    @POST("user/registration")
    fun userRegistration(@Body registerRequest: AuthModels.RegisterRequest): GeneralModels.GeneralResponse

    @POST("user/verify_auth")
    fun verifyAuth(@Body verifyRegisterRequest: AuthModels.VerifyRegisterRequest) : GeneralModels.GeneralResponse

    @POST("user/resend_auth")
    fun resendVerifyAuth(@Body body : Any = Object()) : GeneralModels.GeneralResponse
}