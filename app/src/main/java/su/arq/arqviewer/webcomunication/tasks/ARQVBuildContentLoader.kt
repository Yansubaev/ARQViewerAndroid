package su.arq.arqviewer.webcomunication.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import su.arq.arqviewer.R
import su.arq.arqviewer.entities.ARQBuild
import java.io.File
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.io.FileOutputStream

class ARQVBuildContentLoader(
    context: Context,
    authToken: String?,
    build: ARQBuild?
) : AsyncTask<String, Long, ByteArray>(){

    private val build = build
    private val authToken = authToken
    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)
    private var outputFile: File? = null

    private var onProgressUpdateListener: ((build: ARQBuild?, progress: Int) -> Unit)? = null
    private var onPreExecuteListener: ((build: ARQBuild?) -> Unit)? = null
    private var onPostExecuteListener: ((build: ARQBuild?) -> Unit)? = null

    fun setOnProgressUpdateListener(m: (build: ARQBuild?, progress: Int) -> Unit){
        onProgressUpdateListener = m
    }

    fun setOnPreExecuteListener(m: (build: ARQBuild?) -> Unit){
        onPreExecuteListener = m
    }

    fun setOnPostExecuteListener(m: (build: ARQBuild?) -> Unit){
        onPostExecuteListener = m
    }

    override fun onPreExecute() {
        outputFile = build?.file
        if (outputFile?.parentFile?.exists() == false) { outputFile?.parentFile?.mkdir() }

        if (outputFile?.exists() == false) {
            outputFile?.createNewFile()
            Log.i(this.javaClass.simpleName, "File Created")
        }
        super.onPreExecute()

        onPreExecuteListener?.invoke(build)
    }

    override fun doInBackground(vararg params: String?): ByteArray? {
        try {
            val cn: HttpURLConnection = URL("$baseUrl$buildsUrl/${build?.guid}")
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

        onProgressUpdateListener?.invoke(build, values.first()?.toInt() ?: 0)
    }

    override fun onPostExecute(result: ByteArray?) {
        try {
            val fos = FileOutputStream(outputFile)
            fos.use { fos.write(result) }
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }
        super.onPostExecute(result)

        onPostExecuteListener?.invoke(build)
    }
}