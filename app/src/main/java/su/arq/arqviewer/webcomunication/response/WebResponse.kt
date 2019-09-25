package su.arq.arqviewer.webcomunication.response

import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream

open class WebResponse (inputStream: InputStream){
    var succecss: Boolean? = null
    var message: String? = null

    protected var json: JSONObject? = null

    init {
        val inp = BufferedInputStream(inputStream)
        val inpString = inp.readBytes().toString(Charsets.UTF_8)

        Log.d(this.javaClass.simpleName, "Input: $inpString")
        json = JSONObject(inpString)

        succecss = json?.getBoolean("success")
        message = json?.getString("message")
    }
}