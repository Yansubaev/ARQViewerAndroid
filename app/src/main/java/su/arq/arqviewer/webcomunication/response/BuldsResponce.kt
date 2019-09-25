package su.arq.arqviewer.webcomunication.response

import org.json.JSONArray
import java.io.InputStream

class BuldsResponce(inputStream: InputStream): WebResponse(inputStream) {
    var jsonBuilds: JSONArray? = null

    var builds: Array<ARQBuild>? = null

    init {
        jsonBuilds = json?.getJSONArray("builds")

        builds = Array(jsonBuilds!!.length()) {
            ARQBuild(jsonBuilds?.getJSONObject(it)?.getString("guid")?: "ERROR",
            jsonBuilds?.getJSONObject(it)?.getString("name")?: "ERROR")
        }


    }
}