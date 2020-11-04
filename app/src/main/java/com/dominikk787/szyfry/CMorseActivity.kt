package com.dominikk787.szyfry

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_c_morse.*

class CMorseActivity : AppCompatActivity() {
    private var copyUnicode = false

    private fun updateMorse() {
        CoutText.setText(Morse.encode(CinText.text.toString()).replace('.', '\u2022').replace('-', '\u2212'))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_morse)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        copyUnicode = preferences.getBoolean(getString(R.string.key_morseUnicode), false)

        CinText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateMorse()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_copy, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_copy) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var text = CoutText.text.toString()
            if(!copyUnicode) {
                text = text.replace('\u2022', '.').replace('\u2212', '-')
            }
            clipboard.setPrimaryClip(ClipData.newPlainText("Morse", text))
            Toast.makeText(this, R.string.toast_copy, Toast.LENGTH_SHORT).show()
        } else if(item.itemId == R.id.menu_paste) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if(text != null && text.isNotEmpty()) {
                CinText.setText(text)
                updateMorse()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}