package io.muhwyndham.temanbusreloaded.models.ui

import io.muhwyndham.temanbusreloaded.ui.auth.ErrorConstants

data class State(
    val stateCode: String? = ErrorConstants.NOTHING,
    val message: String? = "",
    val loggerMessage: String? = ErrorConstants.NOTHING
)