package su.arq.arqviewer.webcomunication.response

import java.io.InputStream

class SignInResponse(inputStream: InputStream): WebResponse(inputStream) {

    var token: String? = null
    var name: String? = null
    var email: String? = null

    init {
        token = json?.getString("token")
        name = json?.getJSONObject("personal")?.getString("name")
        email = json?.getJSONObject("personal")?.getString("email")
    }
}