package keyfun.app.remotelocale

import android.os.AsyncTask
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL

object FileCache {

    const val TAG = "FileCache"

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

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
//            Log.d(TAG, "onPostExecute = $result")
            callback.invoke(result)
        }
    }

    fun initConfig(targetFolder: String) {
        val folder = File(targetFolder)
        Log.d(TAG, folder.absolutePath)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        Log.d(TAG, "${folder.absolutePath} $success")
    }

    fun downloadFile(fromUrl: String, toLocalFilePath: String): String? {
        val obj = URL(fromUrl)
        val connection = obj.openConnection() as HttpURLConnection
        try {
            val data = connection.inputStream.bufferedReader().readText()
            writeFile(toLocalFilePath, data)
//            Log.d(TAG, data)
            return data
        } catch (ex: Exception) {
            Log.d(LocaleManager.TAG, ex.toString())
        } finally {
            connection.disconnect()
        }
        return null
    }

    fun loadFile(filePath: String): String? {
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }

        try {
            val inputStream = file.inputStream()
            return inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            Log.d(LocaleManager.TAG, ex.toString())
        }

        return null
    }

    private fun writeFile(filePath: String, data: String) {
        val file = File(filePath)
        Log.d(LocaleManager.TAG, file.absolutePath)
        try {
            PrintWriter(file).use { out -> out.println(data) }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    fun deleteFolder(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.deleteRecursively()
        }
    }

    fun copyFilesInFolder(fromPath: String, toPath: String) {
        val fromFolder = File(fromPath)
        if (fromFolder.exists() && fromFolder.isDirectory) {
            val toFolder = File(toPath)
            fromFolder.copyRecursively(toFolder, true)
        }
    }

}