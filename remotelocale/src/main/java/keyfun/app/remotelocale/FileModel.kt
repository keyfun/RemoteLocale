package keyfun.app.remotelocale

import android.util.Log
import org.json.JSONObject

data class FileModel(val json: String): JSONObject(json) {

    private val TAG = "FileModel"
    var locale: String = ""
    var filePath: String = ""
    var data: Map<String, String> = HashMap()

    init {
        locale = this.getString("locale")
        filePath = this.getString("file_path")
    }

    fun setData(data: HashMap<String, String>) {
        this.data = data
    }

    fun printString() {
        Log.d(TAG, this.toString())
    }

}