package su.arq.arqviewer.webcomunication.response

interface AuthenticationDataProvider {
    val token: String
    val email: String
}