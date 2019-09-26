package su.arq.arqviewer.webcomunication.callbacks

interface AuthDataReceivable {
    fun receiveToken(token: String, name: String, email: String)

    fun receiveError()
}