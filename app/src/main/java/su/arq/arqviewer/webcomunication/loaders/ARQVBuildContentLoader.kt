package su.arq.arqviewer.webcomunication.loaders

import android.content.Context
import android.os.AsyncTask
import android.support.v4.content.AsyncTaskLoader
import android.util.Log
import android.widget.ProgressBar
import kotlinx.coroutines.processNextEventInCurrentThread
import su.arq.arqviewer.R
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

/*
class ARQVBuildContentLoader(
    context: Context,
    authToken: String?,
    guid: String?
) : AsyncTaskLoader<String>(context){
    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)
    private val authToken: String = authToken ?: ""
    private val guid: String = guid ?: ""
    private var content: String? = null

    override fun loadInBackground(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun loadBuildContent(){
        val cn: HttpURLConnection = URL("$baseUrl$buildsUrl/$guid").openConnection()
                as HttpURLConnection
        cn.requestMethod = "GET"
        cn.addRequestProperty("Content-Type", "application/json")
        cn.addRequestProperty("Authorization", "Bearer $authToken")
        cn.connect()
    }
}*/

class ARQVBuildContentLoader(
    context: Context,
    authToken: String?,
    guid: String?,
    progressBar: ProgressBar?
) : AsyncTask<String, Long, String>(){

    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)
    private val authToken: String = authToken ?: ""
    private val guid: String = guid ?: ""
    private var content: String? = null
    private val progressBar: ProgressBar? = progressBar

    override fun doInBackground(vararg params: String?): String? {
        try {
            val cn: HttpURLConnection = URL("$baseUrl$buildsUrl/$guid")
                .openConnection() as HttpURLConnection
            cn.requestMethod = "GET"
            cn.addRequestProperty("Content-Type", "application/json")
            cn.addRequestProperty("Authorization", "Bearer $authToken")
            cn.connect()

            val cl =  cn.contentLength

            val inputStream = BufferedInputStream(cn.inputStream)
            val data = ByteArray(cl)

            var total = 0L
            var count = 0

            publishProgress(0)

            do{
                count = inputStream.read(data)
                total += count
                publishProgress( ((total * 100) / cl))
            }while (count != 0)

            return dataToString(data)
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Long?) {
        values[0]?.toInt()?.let {
            if(it ==0 ){
                progressBar?.isIndeterminate = false
            }
            progressBar?.setProgress(it, true)
        }
    }

    private fun loadBuildContent(){

    }

    private fun dataToString(data: ByteArray) : String{
        return try {
            data.toString(Charsets.UTF_8)
        } catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
            "NO DATA"
        }
    }
}