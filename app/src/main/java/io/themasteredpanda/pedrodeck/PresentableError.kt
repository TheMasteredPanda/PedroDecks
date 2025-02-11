package io.themasteredpanda.pedrodeck

/**
 * An error entry. Keeping information relevant to an error, to be
 * used in the error queue.
 */
data class PresentableError(
    var title: String,
    var message: String,
    var duration: Long = 10
)
