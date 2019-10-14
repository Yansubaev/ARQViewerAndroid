package su.arq.arqviewer.webcomunication.response

import java.net.HttpURLConnection

class AuthDataResponse(cn: HttpURLConnection): WebResponseBase(cn) {

    val token: String = json.getString("token")
    val name: String = json.getJSONObject("personal").getString("name")
    val email: String = json.getJSONObject("personal").getString("email")
}