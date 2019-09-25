package su.arq.arqviewer.webcomunication

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.loader.ARQVTokenLoader
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL


class WebAsyncConnector(context: Context, login: String?, password: String?) {

    private val mBaseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val mSignInUrl: String = context.getString(R.string.arqv_connection_sign_in)
    private val mBuildsUrl: String = context.getString(R.string.arqv_connection_bulds)

    private var mLogin: String? = login
    private var mPassword: String? = password
    private var mAuthToken: String? = null

    init {

    }

    fun signIn(){

        val cn: HttpURLConnection = URL(mBaseUrl + mSignInUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = "POST"
        cn.addRequestProperty("Content-Type", "application/json")

        sendBody(cn)

        if(cn.responseCode in 100..300){
            val token = readResponce(cn)
        }
    }

    private fun sendBody(cn: HttpURLConnection){
        val body = JSONObject()
        try {
            body.put("login", mLogin)
            body.put("password", mPassword)

            Log.i(this.javaClass.simpleName, body.toString())

            val data = body.toString().toByteArray()
            cn.doOutput = true
            cn.setFixedLengthStreamingMode(data.size)
            val out = BufferedOutputStream(cn.outputStream)

            out.use { it.write(data) }

        } catch (e: JSONException) {
            Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
        }
    }

    private fun readResponce(cn: HttpURLConnection) : String?{

        var inp: BufferedInputStream? = null
        try {
            inp = BufferedInputStream(cn.inputStream)
            val inpString = inp.readBytes().toString(Charsets.UTF_8)

            Log.d(this.javaClass.simpleName, "Input: $inpString")
            val json = JSONObject(inpString)
            if(json.has("success") && json.getBoolean("success")){
                if (json.has("token")) {
                    return json.getString("token")
                }
            }
        } catch (e: JSONException) {
            Log.e(this.javaClass.simpleName, e.message, e)
        } catch (e: FileNotFoundException){
            Log.e(this.javaClass.simpleName, e.message, e)
        } finally {
            inp?.close()
        }

        return null
    }

}