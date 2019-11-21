package su.arq.arqviewer.build.tasks

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import su.arq.arqviewer.build.BuildListProvider
import su.arq.arqviewer.build.RomBuildList
import su.arq.arqviewer.build.entities.ARQBuild
import su.arq.arqviewer.build.entities.BuildMetaData
import su.arq.arqviewer.build.BuildListData
import java.io.File
import java.lang.Exception

class RomBuildListLoader(
    private var buildMeta: BuildMetaData
) : AsyncTask<String, Long, BuildListData?>(), BuildListProvider {
    override var onBuildListLoaded: ((builds: BuildListData) -> Unit)? = null
    override var onBuildListLoadingError: ((message: String) -> Unit)? = null

    private var builds: Array<ARQBuild>? = null


    override fun onPostExecute(result: BuildListData?) {
        if(result != null){
            onBuildListLoaded?.invoke(result)
        }else{
            onBuildListLoadingError?.invoke("Error when loading project from ROM")
        }
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg params: String?): BuildListData? {
        return try {
            val dir = File(buildMeta.buildDirectory)
            val dirs = dir.listFiles()
            builds = Array(dirs?.size ?: 0){
                val metaFile = File("${dirs[it].absolutePath}/${dirs[it].name}.meta")
                val metaData = metaFile.readText()
                Log.d(this.javaClass.simpleName, metaData)
                ARQBuild(JSONObject(metaData), dir.absolutePath)
            }
            RomBuildList(builds!!)
        }catch (ex: Exception){
            null
        }
    }

    override fun startLoadingList() {
        this.execute()
    }


}