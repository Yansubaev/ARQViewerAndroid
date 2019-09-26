package su.arq.arqviewer.entities

import org.json.JSONObject

class ARQBuild(json: JSONObject){
    var guid: String = json.getString("guid")
    var name: String = json.getString("name")
    var description: String = json.getString("description")
    var icon: Int = json.getInt("icon")
}