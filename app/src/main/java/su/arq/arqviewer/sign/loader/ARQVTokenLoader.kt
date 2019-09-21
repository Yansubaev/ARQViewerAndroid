package su.arq.arqviewer.sign.loader

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

import org.json.JSONException
import org.json.JSONObject

import android.text.TextUtils
import android.util.Log
import su.arq.arqviewer.R
import su.arq.arqviewer.utils.IOUtils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.cert.CertificateExpiredException
import java.security.cert.X509Certificate
import javax.net.ssl.*


class ARQVTokenLoader(context: Context, login: String?, password: String?) : AsyncTaskLoader<String>(context) {

    private var mObtainTokenUrl: String = context.getString(R.string.arqv_connection_base_url)
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
                Log.e(ARQVTokenLoader::class.java.simpleName, e.message, e)
            }
            return null
        }

        class TempTrustManager : X509TrustManager {
            private val origTrustManager: X509TrustManager

            init {
                val tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm()
                )
                tmf.init(KeyStore.getInstance(KeyStore.getDefaultType()))
                val trustManagers = tmf.trustManagers

                origTrustManager = trustManagers[0] as X509TrustManager
            }

            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
                //origTrustManager.checkClientTrusted(chain, authType)
            }

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return origTrustManager.acceptedIssuers
            }
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
        //someZalupa()
        val cn: HttpURLConnection = URL("https://handshake.arq.su/signin").openConnection()
                as HttpURLConnection
        cn.requestMethod = "POST"
        //cn.addRequestProperty("Content-Type", "application/json")
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

            val data = body.toString()//.toByteArray()
            cn.doOutput = true
            //cn.setFixedLengthStreamingMode(data.size)
            //val out = BufferedOutputStream(cn.outputStream)
            val writer = OutputStreamWriter(cn.outputStream)

            writer.use { w ->
                w.write(data)
            }

            Log.i(this.javaClass.simpleName, "response code is ${cn.responseCode}")
            Log.i(this.javaClass.simpleName, "response message is ${cn.responseMessage}")

        } catch (e: JSONException) {
            Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
        }
    }

    @Throws(IOException::class)
    private fun readToken(cn: HttpURLConnection): String? {
        //cn.doInput = true
        val reader = BufferedReader(cn.inputStream.reader())
        //val inp = BufferedInputStream(cn.inputStream)
        try {
            val json = JSONObject(reader.readLine())
            if (json.has("token")) {
                return json.getString("token")
            }
        } catch (e: JSONException) {
            Log.e(ARQVTokenLoader.javaClass.simpleName, e.message, e)
        } finally {
            reader.close()
        }
        return null
    }

    fun someZalupa(){

        val wrappedTrustManagers = Array<TrustManager>(1){
            TempTrustManager()
        }

        val sc: SSLContext = SSLContext.getInstance("TLS")
        sc.init(null, wrappedTrustManagers, null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
    }

}
