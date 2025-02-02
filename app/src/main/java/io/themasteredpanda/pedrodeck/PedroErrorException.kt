package io.themasteredpanda.pedrodeck

class PedroErrorException(title: String, message: String) : Exception(message) {
    val title = title
}