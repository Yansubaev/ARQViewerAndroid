package su.arq.arqviewer.webcomunication.response

import org.json.JSONObject import su.arq.arqviewer.webcomunication.exceptions.ResponseSuccessFalseException
import java.io.BufferedInputStream
import java.net.HttpURLConnection

open class WebResponseBase (cn: HttpURLConnection){
    val success: Boolean
    val message: String

    protected val json: JSONObject

    init {
        val inp = BufferedInputStream(cn.inputStream)
        val inpString = inp.readBytes().toString(Charsets.UTF_8)

        json = JSONObject(inpString)

        success = json.getBoolean("success")
        message = json.getString("message")
        inp.close()

        if(!success){
            throw ResponseSuccessFalseException(message)
        }
    }
}