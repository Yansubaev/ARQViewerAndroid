package su.arq.arqviewer.webcomunication.loaders

import android.content.Context

import org.json.JSONException
import org.json.JSONObject

import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import su.arq.arqviewer.R
import su.arq.arqviewer.webcomunication.exceptions.ResponseSuccessFalseException
import su.arq.arqviewer.webcomunication.response.AuthDataResponse
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ARQVAuthDataLoader(
    context: Context,
    login: String?,
    password: String?
) : AsyncTaskLoader<AuthDataResponse>(context) {

    private val mBaseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val mSignInUrl: String = context.getString(R.string.arqv_connection_sign_in)
    private val mLogin: String? = login
    private val mPassword: String? = password
    private var authData: AuthDataResponse? = null

    companion object {
        @JvmStatic
        fun signIn(context: Context, login: String?, password: String?): AuthDataResponse? {
            try {
                return ARQVAuthDataLoader(
                    context,
                    login,
                    password
                ).signIn()
            } catch (e: IOException) {
                Log.e(ARQVAuthDataLoader::class.java.simpleName, e.message, e)
            }
            return null
        }
    }

    override fun onStartLoading() {
        if (authData == null) {
            forceLoad()
        } else {
            deliverResult(authData)
        }
    }

    override fun deliverResult(data: AuthDataResponse?) {
        authData = data
        super.deliverResult(data)
    }

    override fun loadInBackground(): AuthDataResponse? {
        try {
            return signIn()
        } catch (e: IOException) {
            Log.e(this.javaClass.simpleName, e.message, e)
        }

        return null
    }

    @Throws(IOException::class)
    private fun signIn(): AuthDataResponse? {
        val cn: HttpURLConnection = URL(mBaseUrl + mSignInUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = "POST"
        cn.addRequestProperty("Content-Type", "application/json")
        sendBody(cn)

        if(responseCodeSuccess(cn.responseCode)){
            return readToken(cn)
        }

        onCancelLoad()

        return null
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
            Log.e(this.javaClass.simpleName, e.message, e)
        }
    }

    @Throws(IOException::class)
    private fun readToken(cn: HttpURLConnection): AuthDataResponse? {
        return try {
            AuthDataResponse(cn.inputStream)
        } catch (ex: ResponseSuccessFalseException){
            cancelLoad()
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCodeSuccess(code: Int) = code in 200..299
}
