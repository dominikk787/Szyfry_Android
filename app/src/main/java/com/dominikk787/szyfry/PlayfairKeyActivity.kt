package com.dominikk787.szyfry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_playfair_key.*
import kotlinx.android.synthetic.main.layout_playfair_key_grid.view.*
import java.util.*

class PlayfairKeyActivity : AppCompatActivity() {
    private val key = Playfair.Key()
    class CharAdapter(private val c: Context, private val key: Playfair.Key) : BaseAdapter() {
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val inflater = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = p1 ?: inflater.inflate(R.layout.layout_playfair_key_grid, p2, false)
            v.text1.text = getItem(p0) as String
            return v
        }
        override fun getItem(p0: Int): Any? {
            return key.get(p0.rem(key.getAlphabet().size), p0 / (key.getAlphabet().size)).toString()
        }
        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }
        override fun getCount(): Int {
            return (key.getAlphabet().size) * (key.getAlphabet().size)
        }
    }

    var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playfair_key)

        PKspnAb.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.array_playfairAlphabets))

        val extras = intent.extras
        if(extras != null) {
            id = extras.getInt("Id", -1)
            PKspnAb.setSelection(extras.getInt("Alphabet", 0))
            PKeditName.setText(extras.getString("KeyName", ""))
            PKeditKey.setText(extras.getString("KeyV", ""))
        }

        fun onKeyChange() {
            println("test")
            val id = PKspnAb.selectedItemId
            println(id)
            key.setAlphabet(id.toInt())
            key.setKey(PKeditKey.text.toString())
            PKgridKey.numColumns = key.getAlphabet().size
            PKgridKey.adapter = CharAdapter(this, key)
        }
        PKspnAb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                onKeyChange()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        PKeditKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onKeyChange()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_playfairkey, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_save) {
            val intent = Intent()
            intent.putExtra("KeyName", PKeditName.text.toString().toUpperCase(Locale.getDefault()))
            intent.putExtra("Alphabet", PKspnAb.selectedItemId.toInt())
            intent.putExtra("KeyV", PKeditKey.text.toString())
            if(id >= 0) intent.putExtra("Id", id)
            setResult(1, intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(0)
        super.onBackPressed()
    }
}