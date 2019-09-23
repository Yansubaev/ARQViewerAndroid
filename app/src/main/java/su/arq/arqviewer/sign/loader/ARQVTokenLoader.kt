package su.arq.arqviewer.sign.loader

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

import org.json.JSONException
import org.json.JSONObject

import android.text.TextUtils
import android.util.Log
import su.arq.arqviewer.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.*


class ARQVTokenLoader(context: Context, login: String?, password: String?) : AsyncTaskLoader<String>(context) {

    private var mBaseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private var mSignInUrl: String = context.getString(R.string.arqv_connection_sign_in)
    private var mLogin: String? = login
    private var mPassword: String? = password
    private var mAuthToken: String? = null

    companion object {
        @JvmStatic
        fun signIn(context: Context, login: String?, password: String?): String? {
            try {
                return ARQVTokenLoader(
                    context,
                    login,
                    password
                ).signIn()
            } catch (e: IOException) {
                Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
            }
            return null
        }
    }

    override fun onStartLoading() {

        if (TextUtils.isEmpty(mAuthToken)) {
            forceLoad()
        } else {
            deliverResult(mAuthToken)
        }
    }

    override fun deliverResult(data: String?) {

        mAuthToken = data
        super.deliverResult(data)
    }

    override fun loadInBackground(): String? {

        try {
            return signIn()
        } catch (e: IOException) {
            Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
        }

        return null
    }

    @Throws(IOException::class)
    private fun signIn(): String? {

        val cn: HttpURLConnection = URL(mBaseUrl + mSignInUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = "POST"
        cn.addRequestProperty("Content-Type", "application/json")
        sendBody(cn)

        return readToken(cn)
    }


    @Throws(IOException::class)
    private fun sendBody(cn: HttpURLConnection) {

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

    @Throws(IOException::class)
    private fun readToken(cn: HttpURLConnection): String? {
        var inp: BufferedInputStream? = null
        try {
            inp = BufferedInputStream(cn.inputStream)
            val inpString = inp.readBytes().toString(Charsets.UTF_8)

            Log.i(this.javaClass.simpleName, "Input: $inpString")
            val json = JSONObject(inpString)
            if(json.has("success") && json.getBoolean("success")){
                if (json.has("token")) {
                    return json.getString("token")
                }
            }
        } catch (e: JSONException) {
            Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
        } catch (e: FileNotFoundException){
            Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
        } finally {
            inp?.close()
        }

        return null
    }

}
