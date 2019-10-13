package su.arq.arqviewer.webcomunication.loaders

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.webcomunication.exceptions.ResponseSuccessFalseException
import su.arq.arqviewer.webcomunication.response.BuildListResponse
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ARQVBuildListLoader (
    context: Context,
    token: String?,
    accountName: String
) : AsyncTaskLoader<Array<ARQBuild>>(context){

    private val mBaseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val mBuildsUrl: String = context.getString(R.string.arqv_connection_bulds)

    private val mToken: String = token ?: ""
    private var builds: Array<ARQBuild>? = null
    private val accountName: String = accountName

    override fun loadInBackground(): Array<ARQBuild>? {
        return loadBuildList()
    }

    override fun onStartLoading() {
        if (builds.isNullOrEmpty()) {
            forceLoad()
        } else {
            deliverResult(builds)
        }
    }

    override fun deliverResult(data: Array<ARQBuild>?) {
        builds = data
        super.deliverResult(data)
    }



    private fun loadBuildList() : Array<ARQBuild>?{
        val cn: HttpURLConnection = URL(mBaseUrl + mBuildsUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = "GET"
        cn.addRequestProperty("Content-Type", "application/json")
        cn.addRequestProperty("Authorization", "Bearer $mToken")

        cn.connect()

        if(responseCodeSuccess(cn.responseCode)){
            return readInput(cn)
        }

        onCancelLoad()

        return null
    }

    private fun readInput(cn: HttpURLConnection): Array<ARQBuild>?{
        return try{
            BuildListResponse(cn.inputStream, context, accountName).builds
        }catch (ex: IOException){
            null
        }catch (ex: ResponseSuccessFalseException){
            //errorCallbackListeners.forEach { it.error(ex.message, null) }
            onCancelLoad()
            null
        }finally {
            cn.inputStream.close()
        }
    }

    private fun responseCodeSuccess(code: Int) = code in 200..299
}