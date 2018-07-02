package keyfun.app.remotelocale

import android.util.Log
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class VersionModel(val json: String) : JSONObject(json) {
    constructor() : this("{}")

    private val TAG = "VersionModel"
    private var updatedAt: String? = null
    var files: List<FileModel>? = null

    init {
        Log.d(TAG, json)
        updatedAt = this.optString("updated_at")
        files = this.optJSONArray("files")
                ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } }
                ?.map { FileModel(it.toString()) }
    }

    fun getString(locale: String, key: String): String {
        return ""
    }

    fun getDate(): Date {
        if (updatedAt.isNullOrEmpty()) {
            return Date(0)
        } else {
            val formatter: DateFormat
            formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return formatter.parse(updatedAt)
        }
    }

    fun printString() {
        Log.d(TAG, "\nupdatedAt = $updatedAt \nfiles = $files \ndate = ${getDate()}")
    }

}