package su.arq.arqviewer.webcomunication.response

import org.json.JSONArray
import su.arq.arqviewer.entities.ARQBuild
import java.net.HttpURLConnection

class BuildListResponse(
    cn: HttpURLConnection,
    buildDir: String
): WebResponseBase(cn), BuildListData {

    private val jsonBuilds: JSONArray = json.getJSONArray("builds")
    override val builds: Array<ARQBuild>

    init {
        builds = Array(jsonBuilds.length()) {
            ARQBuild(
                jsonBuilds.getJSONObject(it),
                buildDir
            )
        }
    }
}