package su.arq.arqviewer.webcomunication.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import su.arq.arqviewer.BuildListProvider
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.BuildMetaData
import su.arq.arqviewer.webcomunication.exceptions.ResponseSuccessFalseException
import su.arq.arqviewer.webcomunication.response.BuildListData
import su.arq.arqviewer.webcomunication.response.BuildListResponse
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class UrlBuildListLoader(
    context: Context,
    private var buildMeta: BuildMetaData
) : AsyncTask<String, Long, BuildListData?>(), BuildListProvider {

    override var onBuildListLoaded: ((builds: BuildListData) -> Unit)? = null
    override var onBuildListLoadingError: ((message: String) -> Unit)? = null

    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)

    override fun startLoading() {
        this.execute()
    }
    override fun onPostExecute(result: BuildListData?) {
        if(result != null){
            onBuildListLoaded?.invoke(result)
        }else{
            onBuildListLoadingError?.invoke("Error loading build list")
        }
        super.onPostExecute(result)
    }
    override fun onCancelled(result: BuildListData?) {
        onBuildListLoadingError?.invoke("Error loading build list")
        super.onCancelled(result)
    }
    override fun onPreExecute() {
        super.onPreExecute()
    }
    override fun doInBackground(vararg params: String?): BuildListData? {
        try{
            val cn: HttpURLConnection = URL(baseUrl + buildsUrl).openConnection()
                    as HttpURLConnection
            cn.requestMethod = "GET"
            cn.addRequestProperty("Content-Type", "application/json")
            cn.addRequestProperty("Authorization", "Bearer ${buildMeta.token}")
            cn.connect()

            return if(responseCodeSuccess(cn.responseCode)){
                readInput(cn)
            }else
                null
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
            ex.printStackTrace()
            return null
        }
    }

    private fun readInput(cn: HttpURLConnection): BuildListData?{
        return try{
            Log.d(this.javaClass.simpleName, "READ INPUT")
            BuildListResponse(cn, buildMeta.buildDirectory)
        }catch (ex: IOException){
            Log.e(this.javaClass.simpleName, ex.message, ex)
            null
        }catch (ex: ResponseSuccessFalseException){
            null
        }finally {
            cn.inputStream.close()
        }
    }

    private fun responseCodeSuccess(code: Int) = code in 200..299
}