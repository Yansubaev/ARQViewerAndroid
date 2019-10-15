package su.arq.arqviewer.webcomunication.response

import java.net.HttpURLConnection

class AuthDataResponse(cn: HttpURLConnection): WebResponseBase(cn), AuthenticationDataProvider {
    override val token: String = json.getString("token")
    override val email: String = json.getJSONObject("personal").getString("email")
    val name: String = json.getJSONObject("personal").getString("name")
}