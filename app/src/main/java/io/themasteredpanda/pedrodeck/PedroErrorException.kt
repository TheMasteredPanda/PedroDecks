package io.themasteredpanda.pedrodeck

class PedroErrorException(title: String, message: String) : Exception(message) {

    init {
        MainActivity.logger("Error. $title / $message")
    }

    val title = title
}