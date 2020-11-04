package com.dominikk787.szyfry

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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