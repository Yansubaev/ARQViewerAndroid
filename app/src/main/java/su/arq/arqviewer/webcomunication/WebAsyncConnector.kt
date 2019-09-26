package su.arq.arqviewer.webcomunication

import android.content.Context
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import su.arq.arqviewer.R
import su.arq.arqviewer.webcomunication.callbacks.AuthDataReceivable
import su.arq.arqviewer.webcomunication.callbacks.BuildsReceivable
import su.arq.arqviewer.webcomunication.response.BuildsResponse
import su.arq.arqviewer.webcomunication.response.SignInResponse
import java.io.BufferedOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class WebAsyncConnector(context: Context?, login: String?, password: String?) {

    private val mBaseUrl: String = context?.getString(R.string.arqv_connection_base_url) ?: ""
    private val mSignInUrl: String = context?.getString(R.string.arqv_connection_sign_in) ?: ""
    private val mBuildsUrl: String = context?.getString(R.string.arqv_connection_bulds) ?: ""

    private var mLogin: String? = login
    private var mPassword: String? = password
    private var mAuthToken: String? = null

    private var authDataReceivers: MutableList<AuthDataReceivable> = mutableListOf()
    private var buildsReceivers: MutableList<BuildsReceivable> = mutableListOf()

    fun addAuthDataReceiveListener(listener: AuthDataReceivable) = authDataReceivers.add(listener)
    fun addBuildsReceiveListener(listener: BuildsReceivable) = buildsReceivers.add(listener)

    fun signIn() = runBlocking {
        val sign = launch { signInCoroutine() }
    }

    fun loadBuilds(token: String) = runBlocking {
        val builds = launch { loadBuildsCoroutine(token) }
    }

    private suspend fun signInCoroutine(){
        val cn = prepareConnection(mSignInUrl, "POST")

        val body = JSONObject()

        try {
            body.put("login", mLogin)
            body.put("password", mPassword)
        } catch (e: JSONException) {
            Log.e(this.javaClass.simpleName, e.message, e)
        }

        val data = body.toString().toByteArray()
        cn.doOutput = true
        cn.setFixedLengthStreamingMode(data.size)
        val out = BufferedOutputStream(cn.outputStream)

        out.use { it.write(data) }

        if(responseGood(cn)){
            mAuthToken = readToken(cn)
        }
    }

    private suspend fun loadBuildsCoroutine(token: String){
        val cn = prepareConnection(mBuildsUrl, "GET")
        cn.addRequestProperty("Authorization", "Bearer $token")

        cn.doOutput = true

        readBuilds(cn)
    }

    private fun prepareConnection(addUrl: String, method: String) : HttpURLConnection{
        val cn: HttpURLConnection = URL(mBaseUrl + addUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = method
        cn.addRequestProperty("Content-Type", "application/json")

        return cn
    }

    private fun sendAuthBody(cn: HttpURLConnection) {
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

    private fun responseGood(cn: HttpURLConnection) : Boolean = cn.responseCode in 100 until 300

    private fun readToken(cn: HttpURLConnection) : String? {
        try{
            val tr = SignInResponse(cn.inputStream)

            if(tr.success){
                for(receiver in authDataReceivers){
                    receiver.receiveToken(tr.token, tr.name, tr.email)
                }
            }else{
                for(receiver in authDataReceivers){
                    receiver.receiveError()
                }
            }
        }
        catch (ex: IOException){
        }

        return null
    }

    private fun readBuilds(cn: HttpURLConnection){
        try{
            val tr = BuildsResponse(cn.inputStream)

            if(tr.success){
                for(receiver in buildsReceivers){
                    receiver.receiveBuilds(tr.builds)
                }
            }else{
                for(receiver in authDataReceivers){
                    receiver.receiveError()
                }
            }
        }
        catch (ex: IOException){
        }

        return
    }
}