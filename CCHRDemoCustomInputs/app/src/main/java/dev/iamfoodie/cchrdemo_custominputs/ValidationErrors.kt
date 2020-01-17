package dev.iamfoodie.cchrdemo_custominputs

object ValidationErrors {

    fun getError(idx: Int, min: Int = 8): String = when(idx) {
        EMAIL -> "Doesn't look like an email"
        PASSWORD, TEXT -> "Not up to $min characters"
        else -> "Wrong Input"
    }

}