package su.arq.arqviewer.webcomunication.loaders

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import android.util.Log
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild
import su.arq.arqviewer.webcomunication.response.BuildListResponse
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ARQVBuildListLoader (context: Context, token: String?) : AsyncTaskLoader<Array<ARQBuild>>(context){
    private val mBaseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val mBuildsUrl: String = context.getString(R.string.arqv_connection_bulds)

    private val mToken: String = token ?: ""
    private var builds: Array<ARQBuild>? = null

    override fun loadInBackground(): Array<ARQBuild>? {
        return loadBuilds();
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


    private fun loadBuilds() : Array<ARQBuild>?{
        val cn: HttpURLConnection = URL(mBaseUrl + mBuildsUrl).openConnection()
                as HttpURLConnection
        cn.requestMethod = "GET"
        cn.addRequestProperty("Content-Type", "application/json")
        cn.addRequestProperty("Authorization", "Bearer $mToken")

        cn.connect()

        Log.d(this.javaClass.simpleName, mToken)

        return readInput(cn)
    }

    private fun readInput(cn: HttpURLConnection): Array<ARQBuild>?{
        return try{
            BuildListResponse(cn.inputStream).builds
        }catch (ex: IOException){
            null
        }
        finally {
            cn.inputStream.close()
        }
    }

}