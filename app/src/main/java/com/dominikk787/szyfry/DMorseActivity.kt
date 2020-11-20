package com.dominikk787.szyfry

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_d_morse.*

class DMorseActivity : AppCompatActivity() {
    private var intxt = ""
    private var copyUnicode = false
    private fun updateMorse() {
        DinText.setText(intxt.replace('.', '\u2022').replace('-', '\u2212'))
        DoutText.setText(Morse.decode(intxt))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d_morse)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        copyUnicode = preferences.getBoolean(getString(R.string.key_morseUnicode), false)

        intxt = savedInstanceState?.getString("InTxt", "") ?: ""

        DbtnDot.setOnClickListener {
            intxt += '.'
            updateMorse()
        }
        DbtnLine.setOnClickListener {
            intxt += '-'
            updateMorse()
        }
        DbtnSlash.setOnClickListener {
            intxt += '/'
            updateMorse()
        }
        DbtnBack.setOnClickListener {
            intxt = intxt.dropLast(1)
            updateMorse()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("InTxt", intxt)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_copy, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_copy) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("From Morse", DoutText.text.toString()))
            Toast.makeText(this, R.string.toast_copy, Toast.LENGTH_SHORT).show()
        } else if(item.itemId == R.id.menu_paste) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if(text != null && text.isNotEmpty()) {
                if(copyUnicode) {
                    text = text.replace('\u2022', '.').replace('\u2212', '-')
                }
                text = text.replace("[^./-]".toRegex(), "")
                intxt = text
                updateMorse()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}