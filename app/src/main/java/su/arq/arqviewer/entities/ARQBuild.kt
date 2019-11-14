package su.arq.arqviewer.entities

import org.json.JSONObject
import su.arq.arqviewer.BuildIcon
import java.io.File

data class ARQBuild(val json: JSONObject, private val directory: String){
    var guid: String = json.getString("guid")
    var name: String = json.getString("name")
    var description: String = json.getString("description")
    var icon: BuildIcon = when(json.getInt("icon")) {
        2 -> BuildIcon.TRIANGLE3D
        3 -> BuildIcon.CUBE
        4 -> BuildIcon.SPHERE
        5 -> BuildIcon.BUILDING
        6 -> BuildIcon.MANUFACTURE
        else -> BuildIcon.DEFAULT
    }
    var buildDir: File = File("$directory/$guid")
    var buildFile: File = File("$directory/$guid/$guid.arq")
    var metaFile: File = File("$directory/$guid/$guid.meta")
    val downloaded: Boolean
        get() = buildFile.exists()
}