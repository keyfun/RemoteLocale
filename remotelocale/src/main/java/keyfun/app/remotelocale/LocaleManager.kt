package keyfun.app.remotelocale

import android.util.Log

enum class LocaleStatus {
    SUCCESS_LOCAL, SUCCESS_REMOTE, FAIL_LOCAL, FAIL_REMOTE
}

object LocaleManager {

    const val TAG = "LocaleManager"
    private var cacheFolder = ""
    private var serverPath = ""
    private var rootFile = ""
    private const val tmpFolder = "tmp/"
    private var callback: ((status: LocaleStatus) -> Unit)? = null
    private var localVersion = VersionModel()
    private var remoteVersion = VersionModel()
    private var remoteFilesTotal = 0
    private var downloadedCount = 0

    init {
        Log.d(TAG, "Init LocaleManager")
    }

    fun initConfig(cacheFolder: String, serverPath: String, rootFile: String) {
        this.cacheFolder = cacheFolder
        this.serverPath = serverPath
        this.rootFile = rootFile

        // init cache folder
        FileCache.initConfig(cacheFolder + tmpFolder)
    }

    fun initLocalData() {
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

    fun clear() {
        FileCache.deleteFolder(cacheFolder)
    }

    private fun loadLocaleFiles(files: List<FileModel>) {
        files.forEach {
            val data = FileCache.loadFile(cacheFolder + it.filePath)
            if (data != null) {
                it.setData(data)
            }
        }
    }

    fun run(callback: ((status: LocaleStatus) -> Unit)) {
        this.callback = callback
        // download remote root file
        val from = serverPath + rootFile
        val to = cacheFolder + tmpFolder + rootFile
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
                callback?.invoke(LocaleStatus.SUCCESS_LOCAL)
            }
        } ?: run {
            callback?.invoke(LocaleStatus.FAIL_LOCAL)
        }
    }

    private fun isValidVersion(): Boolean {
//        Log.d(TAG, "remoteVersion = ${remoteVersion.getDate()}")
//        Log.d(TAG, "localVersion = ${localVersion.getDate()}")
        return remoteVersion.getDate().after(localVersion.getDate())
    }

    private fun downloadLocaleFiles() {
        remoteFilesTotal = 0
        downloadedCount = 0
        remoteVersion.files?.let {
            remoteFilesTotal = it.size
            it.forEach {
                val from = serverPath + it.filePath
                val to = cacheFolder + tmpFolder + it.filePath
                val locale = it.locale
                FileCache.DownloadTask {
//                    Log.d(TAG, "RemoteVersion = $it")
                    gotLocaleFile(locale, it.orEmpty())
                }.execute(from, to)
            }
        }
    }

    // from remote data
    private fun gotLocaleFile(locale: String, data: String) {
        Log.d(TAG, "locale = $locale \ndata = $data")
        remoteVersion.files?.first { it.locale == locale }?.setData(data)
        downloadedCount++

        if (downloadedCount >= remoteFilesTotal) {
            onCompleteDownload()
        }
    }

    private fun onCompleteDownload() {
        callback?.invoke(LocaleStatus.SUCCESS_REMOTE)

        // copy all tmp folder files to local folder
        FileCache.copyFilesInFolder(cacheFolder + tmpFolder, cacheFolder)
    }

    private fun getVersion(): VersionModel {
        return if (isValidVersion()) {
            remoteVersion
        } else {
            localVersion
        }
    }

    fun getString(locale: String, key: String): String {
        return getVersion().getString(locale, key)
    }
}