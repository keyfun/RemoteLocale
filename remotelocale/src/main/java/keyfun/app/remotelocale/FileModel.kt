package keyfun.app.remotelocale

import org.json.JSONObject

data class FileModel(val json: String) : JSONObject(json) {

    private val TAG = "FileModel"
    var locale: String = ""
    var filePath: String = ""
    var data: HashMap<String, String> = HashMap()

    init {
        locale = this.getString("locale")
        filePath = this.getString("file_path")
    }

    fun setData(jsonString: String) {
        val json = JSONObject(jsonString)
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            if (json.get(key) is String) {
                val value = json.get(key).toString()
                data[key] = value
            }
        }
//        Log.d(TAG, data.toString())
    }

}