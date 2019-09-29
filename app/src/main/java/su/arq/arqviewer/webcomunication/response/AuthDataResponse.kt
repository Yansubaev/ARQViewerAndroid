package su.arq.arqviewer.webcomunication.response

import java.io.InputStream

class AuthDataResponse(inputStream: InputStream): WebResponseBase(inputStream) {

    val token: String = json.getString("token")
    val name: String = json.getJSONObject("personal").getString("name")
    val email: String = json.getJSONObject("personal").getString("email")
}