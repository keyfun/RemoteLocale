package keyfun.app.remotelocale

import android.os.AsyncTask
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL

object FileCache {

    const val TAG = "FileCache"

//    interface OnEventListener<T> {
//        fun onSuccess(`object`: T)
//        fun onFailure(e: Exception)
//    }

    class DownloadTask(private val callback: ((data: String?) -> Unit)) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            Log.d(TAG, params.contentToString())
            val result = downloadFile(params[0].toString(), params[1].toString())
            if (result == null) {
                Log.d(TAG, "task cancelled")
                this.cancel(true)
                return null
            } else {
                return result
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "onPreExecute")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
//            Log.d(LocaleManager.TAG, "onPostExecute = $result")
            callback.invoke(result)
        }
    }

    fun initConfig(cacheFolder: String) {
        val folder = File(cacheFolder)
        Log.d(TAG, folder.absolutePath)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdir()
        }
        Log.d(TAG, "${folder.absolutePath} $success")
    }

    fun downloadFile(fromUrl: String, toLocalFilePath: String): String? {
        val obj = URL(fromUrl)
        val connection = obj.openConnection() as HttpURLConnection
        try {
            val data = connection.inputStream.bufferedReader().readText()
            writeFile(toLocalFilePath, data)
            Log.d(TAG, data)
//            return data
        } catch (ex: Exception) {
            Log.d(LocaleManager.TAG, ex.toString())
        } finally {
            connection.disconnect()
        }
        return null
    }

    fun loadFile(file: String): String? {
        val dest = File(file)
        if (!dest.exists()) {
            return null
        }

        try {
            val inputStream = dest.inputStream()
            return inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            Log.d(LocaleManager.TAG, ex.toString())
        }

        return null
    }

    private fun writeFile(filePath: String, data: String) {
        val dest = File(filePath)
        Log.d(LocaleManager.TAG, dest.absolutePath)
        try {
            PrintWriter(dest).use { out -> out.println(data) }
        } catch (e: Exception) {
            // handle the exception
            Log.d(TAG, e.toString())
        }
    }

}