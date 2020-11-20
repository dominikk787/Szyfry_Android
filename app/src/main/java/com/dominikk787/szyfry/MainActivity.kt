package com.dominikk787.szyfry

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {

    private fun str2version(str: String): Triple<Int, Int, Int> {
        val tab = str.split('.')
        val v0 = tab[0].toInt()
        val v1 = if(tab.size > 1) tab[1].toInt() else 0
        val v2 = if(tab.size > 2) tab[2].toInt() else 0
        return Triple(v0, v1, v2)
    }

    private fun checkVersion() : Triple<Boolean, String, Uri> {
        val url = URL("https://api.github.com/repos/" + getString(R.string.update_repo) + "/releases/latest")
        val urlConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        var res: Triple<Boolean, String, Uri>
        try {
            val stream: InputStream = BufferedInputStream(urlConnection.inputStream)
            BufferedReader(stream.reader()).use { reader ->
                val json = JSONObject(reader.readText())
                var tag = json.getString("tag_name")
                tag = tag.replace("[^0-9.]".toRegex(), "")
                val (vm0, vm1, vm2) = str2version(BuildConfig.VERSION_NAME)
                val (vi0, vi1, vi2) = str2version(tag)
                res = if(vi0 > vm0 || vi1 > vm1 || vi2 > vm2)
                            Triple(true, getString(R.string.text_update_new, tag, BuildConfig.VERSION_NAME), Uri.parse(json.getString("html_url")))
                        else Triple(false, getString(R.string.text_update_actual), Uri.parse(json.getString("html_url")))
                println(res)
            }
        } finally {
            urlConnection.disconnect()
        }
        return res
    }

    private fun checkUpdates(allowCell: Boolean) {
        var nettransport = NetworkCapabilities.TRANSPORT_WIFI
        if(Build.VERSION.SDK_INT > 22) {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifi = cm.getNetworkCapabilities(cm.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
            if(!wifi) {
                val cell = cm.getNetworkCapabilities(cm.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
                nettransport = if(cell) NetworkCapabilities.TRANSPORT_CELLULAR else 0
            }
        }
        if(nettransport == NetworkCapabilities.TRANSPORT_WIFI || (nettransport == NetworkCapabilities.TRANSPORT_CELLULAR && allowCell)) {
            Thread {
                val (new, str, uri) = checkVersion()
                if(new) {
                    this.runOnUiThread {
                        val builder = AlertDialog.Builder(this)
                        builder.apply {
                            setMessage(str)
                            setTitle(R.string.text_update_new_title)
                            setNegativeButton(R.string.text_update_close) { _: DialogInterface, _: Int -> }
                            setPositiveButton(R.string.text_update_open) { _: DialogInterface, _: Int ->
                                startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                            create().show()
                        }
                    }
                } else {
                    this.runOnUiThread {
                        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val updatesCell = !preferences.getBoolean(getString(R.string.key_generalUpdates), false)

        if(savedInstanceState == null) checkUpdates(updatesCell)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Mgrid.columnCount = 2
        } else {
            Mgrid.columnCount = 1
        }

        MbtnCMorse.setOnClickListener {
            val intent = Intent(this, CMorseActivity::class.java)
            startActivity(intent)
        }

        MbtnDMorse.setOnClickListener {
            val intent = Intent(this, DMorseActivity::class.java)
            startActivity(intent)
        }

        MbtnPlayfair.setOnClickListener {
            val intent = Intent(this, PlayfairActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.menu_others) {
            val intent = Intent(this, OthersActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}