package su.arq.arqviewer.webcomunication.loaders

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import su.arq.arqviewer.entities.BuildMetaData
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.webcomunication.exceptions.ResponseSuccessFalseException
import su.arq.arqviewer.webcomunication.response.BuildListData
import su.arq.arqviewer.webcomunication.response.BuildListResponse
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ARQVBuildListLoader (
    context: Context,
    private var buildMeta: BuildMetaData
) : AsyncTaskLoader<BuildListData>(context){

    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)
    private var builds: BuildListData? = null

    override fun loadInBackground(): BuildListData? { return loadBuildList() }

    override fun onStartLoading() {
        if (builds == null) {
            forceLoad()
        } else {
            deliverResult(builds)
        }
    }

    override fun deliverResult(data: BuildListData?) {
        builds = data
        super.deliverResult(data)
    }

    private fun loadBuildList() : BuildListData?{
        val cn: HttpURLConnection = URL(baseUrl + buildsUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = "GET"
        cn.addRequestProperty("Content-Type", "application/json")
        cn.addRequestProperty("Authorization", "Bearer ${buildMeta.token}")
        cn.connect()

        if(responseCodeSuccess(cn.responseCode)){
            return readInput(cn)
        }
        onCancelLoad()
        return null
    }

    private fun readInput(cn: HttpURLConnection): BuildListData?{
        return try{
            BuildListResponse(cn, buildMeta.buildDirectory)
        }catch (ex: IOException){
            null
        }catch (ex: ResponseSuccessFalseException){
            onCancelLoad()
            null
        }finally {
            cn.inputStream.close()
        }
    }

    private fun responseCodeSuccess(code: Int) = code in 200..299
}