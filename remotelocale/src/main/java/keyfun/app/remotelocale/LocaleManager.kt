package keyfun.app.remotelocale

import android.util.Log

object LocaleManager {

    const val TAG = "LocaleManager"
    private var cacheFolder: String = ""
    private var serverPath: String = ""
    private var rootFile: String = ""
    private var callback: ((data: String?) -> Unit)? = null
    private var localVersion: VersionModel = VersionModel()
    private var remoteVersion: VersionModel = VersionModel()

    init {
        Log.d(TAG, "Init LocaleManager")
    }

    fun initConfig(cacheFolder: String, serverPath: String, rootFile: String) {
        this.cacheFolder = cacheFolder
        this.serverPath = serverPath
        this.rootFile = rootFile

        // init cache folder
        FileCache.initConfig(cacheFolder)

        // load local cache files
        val versionStr = FileCache.loadFile(cacheFolder + rootFile)
        Log.d(TAG, "versionStr = $versionStr")
        versionStr?.let {
            localVersion = VersionModel(it)
            // load all languages data
            localVersion.files?.let {
                loadLocaleFiles(it)
            }
        }
    }

    private fun loadLocaleFiles(files: List<FileModel>) {
        files.forEach {
            val data = FileCache.loadFile(cacheFolder + it.filePath)
            if (data != null) {
                it.setData(data)
            }
        }
    }

    fun run(callback: ((data: String?) -> Unit)) {
        this.callback = callback
        // download remote root file
        val from = serverPath + rootFile
        val to = cacheFolder + rootFile
        FileCache.DownloadTask {
            Log.d(TAG, "RemoteVersion = $it")
            gotRootFile(it)
        }.execute(from, to)
    }

    private fun gotRootFile(data: String?) {
        data?.let {
            remoteVersion = VersionModel(it)
//            remoteVersion.printString()

            if (isValidVersion()) {
                downloadLocaleFiles()
            } else {
                callback?.invoke(null)
            }
        } ?: run {
            callback?.invoke(null)
        }
    }

    private fun isValidVersion(): Boolean {
//        Log.d(TAG, "remoteVersion = ${remoteVersion.getDate()}")
//        Log.d(TAG, "localVersion = ${localVersion.getDate()}")
        return remoteVersion.getDate().after(localVersion.getDate())
    }

    private fun downloadLocaleFiles() {
        remoteVersion.files?.let {
            it.forEach {
                val from = serverPath + it.filePath
                val to = cacheFolder + it.filePath
                val locale = it.locale
                FileCache.DownloadTask {
                    Log.d(TAG, "RemoteVersion = $it")
                    gotLocaleFile(locale, it.orEmpty())
                }.execute(from, to)
            }
        }
    }

    // from remote data
    private fun gotLocaleFile(locale: String, data: String) {
        Log.d(TAG, "locale = $locale \ndata = $data")
        remoteVersion.files?.first { it.locale == locale }?.setData(data)
    }

    private fun getVersion(): VersionModel {
        if (isValidVersion()) {
            return remoteVersion
        } else {
            return localVersion
        }
    }

    fun getString(locale: String, key: String): String {
        return getVersion().getString(locale, key)
    }
}