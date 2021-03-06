package keyfun.app.remotelocale

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    val SERVER_PATH = "http://192.168.1.47/JsonSample/"
//    val SERVER_PATH = "http://192.168.1.47/JsonSampleNew/"
    val ROOT_FILE = "version.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initApp()
    }

    private fun initApp() {
        LocaleManager.initConfig(this.filesDir.absolutePath + "/LocaleDemo/", SERVER_PATH, ROOT_FILE)
//        LocaleManager.clear()
        LocaleManager.initLocalData()
        LocaleManager.run {
            Log.d(TAG, "LocaleManager::run completed")
            updateText(it.toString())
        }
    }

    private fun updateText(string: String) {
        val tv: TextView = this.findViewById(R.id.textView)
        tv.text = string

        val tc = LocaleManager.getString("tc", "app_name")
        val sc = LocaleManager.getString("sc", "app_name")
        val en = LocaleManager.getString("en", "app_name")

        tv.text = String.format("%s\n%s\n%s\n%s", string, tc, sc, en)
    }
}
