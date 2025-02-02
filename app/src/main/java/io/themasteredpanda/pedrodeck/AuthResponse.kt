package io.themasteredpanda.pedrodeck

/**
 * A simple process for handing authentication response so I can
 * Handle all necessary exceptions.
 */
data class AuthResponse(
    var state: AuthState,
    var processType: AuthProcessType,
    var exception: Exception? = null,
    var exceptionMessage: String? = null,
)

/**
 * Enum to define the stages of an auth process, and the result of such a state.
 */
enum class AuthState {
    SUCCESS,
    PROCESSING,
    FAILURE,
    COMPLETE
}

enum class AuthProcessType {
    REGISTERING,
    SIGNING_IN,
    SIGNED_IN
}