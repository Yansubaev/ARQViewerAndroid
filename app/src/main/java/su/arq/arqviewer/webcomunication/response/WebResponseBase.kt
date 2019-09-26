package su.arq.arqviewer.webcomunication.response

import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream

open class WebResponseBase (inputStream: InputStream){
    val success: Boolean
    val message: String

    protected val json: JSONObject

    init {
        val inp = BufferedInputStream(inputStream)
        val inpString = inp.readBytes().toString(Charsets.UTF_8)

        Log.d(this.javaClass.simpleName, "Input: $inpString")

        json = JSONObject(inpString)

        success = json.getBoolean("success")
        message = json.getString("message")
        inp.close()
    }
}