package su.arq.arqviewer.build.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import su.arq.arqviewer.R
import su.arq.arqviewer.build.entities.ARQBuild
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ARQVBuildContentLoader(
    context: Context,
    authToken: String?,
    build: ARQBuild?
) : AsyncTask<String, Long, Boolean>(){

    private val build = build
    private val authToken = authToken
    private val baseUrl: String = context.getString(R.string.arqv_connection_base_url)
    private val buildsUrl: String = context.getString(R.string.arqv_connection_bulds)

    private var onProgressUpdateListener: ((build: ARQBuild?, progress: Int) -> Unit)? = null
    private var onPreExecuteListener: ((build: ARQBuild?) -> Unit)? = null
    private var onPostExecuteListener: ((build: ARQBuild?, success: Boolean) -> Unit)? = null

    fun setOnProgressUpdateListener(m: (build: ARQBuild?, progress: Int) -> Unit){
        onProgressUpdateListener = m
    }

    fun setOnPreExecuteListener(m: (build: ARQBuild?) -> Unit){
        onPreExecuteListener = m
    }

    fun setOnPostExecuteListener(m: (build: ARQBuild?, success: Boolean) -> Unit){
        onPostExecuteListener = m
    }

    override fun onPreExecute() {
        super.onPreExecute()
        onPreExecuteListener?.invoke(build)
    }

    override fun doInBackground(vararg params: String?): Boolean {
        try {
            val cn: HttpURLConnection = URL("$baseUrl$buildsUrl/${build?.guid}")
                .openConnection() as HttpURLConnection
            cn.requestMethod = "GET"
            cn.addRequestProperty("Content-Type", "application/json")
            cn.addRequestProperty("Authorization", "Bearer $authToken")
            cn.connect()

            val cl = cn.contentLength
            Log.d(this.javaClass.simpleName, "content length = $cl")

            makeDirs()
            saveMeta()

            val file = build!!.buildFile
            if (!file.exists()) {
                file.createNewFile()
            }
            val dFos = FileOutputStream(file)

            val inputStream = BufferedInputStream(cn.inputStream)
            val dataBuffer = ByteArray(8192)
            var bytesRead = 0
            var bytesBuffered = 0
            var total = 0L
            var perc = 0L
            while(bytesRead > -1){
                bytesRead = inputStream.read(dataBuffer, 0, 8192)
                if(bytesRead<0){ break }
                dFos.write(dataBuffer, 0, bytesRead)
                bytesBuffered += bytesRead
                if (bytesBuffered > 1024 * 1024) { //flush after 1MB
                    bytesBuffered = 0
                    dFos.flush()
                }

                total += bytesRead
                if((perc + 1) == (total * 100) / cl){
                    publishProgress((total * 100) / cl)
                }

                perc = (total * 100) / cl
            }

            return true
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
            return false
        }finally {

        }
    }

    private fun makeDirs(){
        val dir = build!!.buildDir
        if (dir.parentFile?.exists() == false) {
            dir.parentFile?.mkdir()
        }
        if (!dir.exists()) {
            dir.mkdir()
        }
    }
    private fun saveMeta(){
        val meta = build!!.metaFile
        if (!meta.exists()) {
            meta.createNewFile()
        }
        val mFos = FileOutputStream(meta)
        val metaBytes = build.json.toString().toByteArray()
        mFos.use { mFos.write(metaBytes) }
    }

    override fun onProgressUpdate(vararg values: Long?) {
        super.onProgressUpdate(*values)
        Log.d(this.javaClass.simpleName, "Loading progress: ${values[0]}")

        onProgressUpdateListener?.invoke(build, values.first()?.toInt() ?: 0)
    }

    override fun onPostExecute(success: Boolean) {
        onPostExecuteListener?.invoke(build, success)
        super.onPostExecute(success)
    }
}