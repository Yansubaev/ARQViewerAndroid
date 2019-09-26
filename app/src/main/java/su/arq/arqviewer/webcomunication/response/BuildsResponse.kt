package su.arq.arqviewer.webcomunication.response

import org.json.JSONArray
import su.arq.arqviewer.entities.ARQBuild
import java.io.InputStream

class BuildsResponse(inputStream: InputStream): WebResponseBase(inputStream) {

    val jsonBuilds: JSONArray = json.getJSONArray("builds")
    val builds: Array<ARQBuild>

    init {
        builds = Array(jsonBuilds.length()) {ARQBuild(jsonBuilds.getJSONObject(it))}
    }
}