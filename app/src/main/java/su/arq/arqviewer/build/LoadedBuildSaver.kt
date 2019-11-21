package su.arq.arqviewer.build

import android.util.Log
import su.arq.arqviewer.build.entities.ARQBuild
import java.io.FileOutputStream

class LoadedBuildSaver(private var build: ARQBuild, private var data: ByteArray) {

    fun save(){
        try {
            makeDirs()
            saveData()
            saveMeta()
        }catch (ex: Exception){
            Log.e(this.javaClass.simpleName, ex.message, ex)
        }
    }

    private fun makeDirs(){
        val dir = build.buildDir
        if (dir.parentFile?.exists() == false) {
            dir.parentFile?.mkdir()
        }
        if (!dir.exists()) {
            dir.mkdir()
        }
    }
    private fun saveMeta(){
        val meta = build.metaFile
        if (!meta.exists()) {
            meta.createNewFile()
        }
        val mFos = FileOutputStream(meta)
        val metaBytes = build.json.toString().toByteArray()
        mFos.use { mFos.write(metaBytes) }
    }
    private fun saveData(){
        val file = build.buildFile
        if (!file.exists()) {
            file.createNewFile()
        }
        val dFos = FileOutputStream(file)
        dFos.use { dFos.write(data) }
    }
}