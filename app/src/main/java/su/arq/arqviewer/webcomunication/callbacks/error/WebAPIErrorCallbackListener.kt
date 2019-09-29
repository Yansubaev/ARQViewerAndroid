package su.arq.arqviewer.webcomunication.callbacks.error

interface WebAPIErrorCallbackListener {
    fun error(message: String?, httpResponseCode: Int?)
}