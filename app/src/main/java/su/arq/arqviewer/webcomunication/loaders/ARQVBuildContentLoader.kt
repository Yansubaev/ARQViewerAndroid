package su.arq.arqviewer.webcomunication.loaders

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import su.arq.arqviewer.R
import su.arq.arqviewer.projects.projectcard.model.ProjectCardModel
import java.io.File
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.io.FileOutputStream


class ARQVBuildContentLoader(
    context: Context,
    private val authToken: String?,
    private val outputFile: File,
    private val cardModel: ProjectCardModel?
) : AsyncTask<String, Long, ByteArray>(){

    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)

    override fun onPreExecute() {
        cardModel?.holder?.startDownloading()
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): ByteArray? {
        try {
            val cn: HttpURLConnection = URL("$baseUrl$buildsUrl/${cardModel?.build?.guid}")
                .openConnection() as HttpURLConnection
            cn.requestMethod = "GET"
            cn.addRequestProperty("Content-Type", "application/json")
            cn.addRequestProperty("Authorization", "Bearer $authToken")
            cn.connect()

            val cl = cn.contentLength
            Log.d(this.javaClass.simpleName, "content length = $cl")

            val inputStream = cn.inputStream
            val data = ByteArray(cl)

            var total = 0L
            var count: Int

            publishProgress(0)
            var perc = 0L
            do {
                count = inputStream.read(data)
                total += count

                if((perc + 1) == (total * 100) / cl){
                    publishProgress((total * 100) / cl)
                }

                perc = (total * 100) / cl
            } while (count != -1)

            return data
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Long?) {
        super.onProgressUpdate(*values)
        Log.d(this.javaClass.simpleName, "Loading progress: ${values[0]}")

        if(values[0]?.toInt() == 0){
            cardModel?.holder?.progressBar?.visibility = View.VISIBLE
            cardModel?.holder?.progressBar?.isIndeterminate = false
        }
        cardModel?.holder?.progressBar?.setProgress(values[0]?.toInt() ?: 0, true)
    }

    override fun onPostExecute(result: ByteArray?) {
        try {
            val fos = FileOutputStream(outputFile)
            fos.write(result)

            cardModel?.holder?.progressBar?.setProgress(100, true)
            cardModel?.holder?.downloadedAnimate()

        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }

        super.onPostExecute(result)
    }
}