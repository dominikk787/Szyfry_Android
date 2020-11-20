package com.dominikk787.szyfry

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_playfair.*
import kotlinx.android.synthetic.main.dialog_playfair_select_key.view.*
import kotlinx.android.synthetic.main.layout_playfair_key_item.view.*

class PlayfairActivity : AppCompatActivity() {

    private var dbHelper: PlayfairDBHelper? = null
    private var listKeys = listOf<KeyListItem>()
    private var selectedItemId = -1
    private var devMode = false

    private fun updatePlayfair() {
        if(selectedItemId >= 0) {
            var text = Playfair.crypt(PinText.text.toString(), PtoggleDir.isChecked)
            if(devMode) {
                text += "\n\n"
                text += if(PtoggleDir.isChecked) Playfair.crypt(PinText.text.toString(), PtoggleDir.isChecked, false)
                else Playfair.key.getAlphabet().trim(PinText.text.toString(), !PtoggleDir.isChecked)
            }
            PoutText.setText(text)
        } else PoutText.setText("")
    }

    class MyKeyAdapter(private val context: Context, private val list: List<KeyListItem>, private val new_key: KeyListItem,
                       private val dummy_key: KeyListItem, private val listener: MyItemClicksListener? = null) : RecyclerView.Adapter<MyKeyAdapter.MyKeyViewHolder>() {
        data class MyKeyViewHolder(val view: View, val textView: TextView, val button: Button, val div: View, var pos: Int) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int = (list.size + 1).coerceAtLeast(2)
        private fun getItem(pos: Int): KeyListItem = if(list.isEmpty() && pos == 0) dummy_key
        else if(pos >= list.size) new_key
        else list[pos]
        override fun onBindViewHolder(holder: MyKeyViewHolder, position: Int) {
            holder.pos = position
            holder.textView.text = getItem(position).name
            holder.view.setOnClickListener { listener?.onClickListener(it, position, getItem(position)) }
            if(getItem(position).id >= 0) holder.button.setOnClickListener { listener?.onButtonClickListener(it, position, getItem(position)) }
            else holder.button.visibility = View.INVISIBLE
            if(position == 0) holder.div.visibility = View.INVISIBLE
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyKeyViewHolder {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.layout_playfair_key_item, parent, false)
            return MyKeyViewHolder(view, view.text1, view.btn, view.div, -1)
        }

        interface MyItemClicksListener {
            fun onClickListener(view: View, pos: Int, item: KeyListItem)
            fun onButtonClickListener(view: View, pos: Int, item: KeyListItem)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playfair)

        dbHelper = PlayfairDBHelper(this)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        devMode = preferences.getBoolean(getString(R.string.key_playfairDev), false)

        genKeyList()
        PinText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updatePlayfair()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        PtoggleDir.setOnClickListener {
            updatePlayfair()
        }
        PbtnSelKey.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            lateinit var dialog: AlertDialog
            lateinit var viewAdapter: MyKeyAdapter
            val inflater = this.layoutInflater
            val v = inflater.inflate(R.layout.dialog_playfair_select_key, null)
            viewAdapter = MyKeyAdapter(this, listKeys, KeyListItem(-2, getString(R.string.text_newKey)), KeyListItem(-1, ""),
                object : MyKeyAdapter.MyItemClicksListener {
                    override fun onClickListener(view: View, pos: Int, item: KeyListItem) {
                        when(item.id) {
                            -1 -> {
                                PtextKeyName.text = item.name
                                selectedItemId = item.id
                            }
                            -2 -> {
                                val intent = Intent(this@PlayfairActivity, PlayfairKeyActivity::class.java)
                                startActivityForResult(intent, 1)
                            }
                            in 0..Int.MAX_VALUE -> {
                                PtextKeyName.text = item.name
                                Playfair.key.setAlphabet(item.abId)
                                Playfair.key.setKey(item.key)
                                selectedItemId = item.id
                                updatePlayfair()
                            }
                        }
                        dialog.dismiss()
                    }
                    override fun onButtonClickListener(view: View, pos: Int, item: KeyListItem) {
                        println("button click $view at $pos on $item")
                        val build = AlertDialog.Builder(ContextThemeWrapper(this@PlayfairActivity, R.style.AlertDialogTheme))
                        build.apply {
                            setPositiveButton("Edytuj") { dialogInterface, i ->
                                println("edit key $item $dialogInterface $i")
                                dialog.dismiss()
                                val intent = Intent(this@PlayfairActivity, PlayfairKeyActivity::class.java)
                                intent.putExtra("Id", item.id)
                                intent.putExtra("KeyName", item.name)
                                intent.putExtra("Alphabet", item.abId)
                                intent.putExtra("KeyV", item.key)
                                startActivityForResult(intent, 1)
                            }
                            setNegativeButton("Usuń") { dialogInterface, i ->
                                println("delete key $item $dialogInterface $i")
                                dbHelper?.deleteKey(item.id)
                                genKeyList()
                                dialog.dismiss()
                                PbtnSelKey.callOnClick()
                            }
                            setNeutralButton("Anuluj") { dialogInterface, i ->
                                println("cancel key $item $dialogInterface $i")
                            }
                            setTitle(item.name)
                        }
                        build.create().show()
                    }
                })
            v.PDrecycler.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@PlayfairActivity)
                adapter = viewAdapter
            }
            builder.setView(v).setTitle("Wybierz klucz")
            dialog = builder.create()
            dialog.show()
        }
    }

    private fun genKeyList() {
        listKeys = dbHelper?.getAllKeys() ?: listOf()
        println(listKeys)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            println("result key ${data?.getStringExtra("KeyName")} ${data?.getIntExtra("Alphabet", 0)} ${data?.getStringExtra("KeyV")}")
            if(resultCode == 1 && data != null && data.hasExtra("KeyName") && data.hasExtra("Alphabet") && data.hasExtra("KeyV")) {
                val key = KeyListItem(0,
                    data.getStringExtra("KeyName") ?: "",
                    data.getIntExtra("Alphabet", 0),
                    data.getStringExtra("KeyV") ?: "")
                if (data.hasExtra("Id")) {
                    key.id = data.getIntExtra("Id", 0)
                    dbHelper?.updateKey(key)
                } else {
                    key.id = dbHelper?.addKey(key)?.toInt() ?: 0
                }
                genKeyList()
                println("result ${listKeys.size} ${key.id} ${key.name} ${key.key}")
                Playfair.key.setAlphabet(key.abId)
                Playfair.key.setKey(key.key)
                PtextKeyName.text = key.name
                selectedItemId = key.id
                updatePlayfair()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_copy, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_copy && selectedItemId >= 0) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Playfair", Playfair.crypt(PinText.text.toString(), PtoggleDir.isChecked)))
            Toast.makeText(this, R.string.toast_copy, Toast.LENGTH_SHORT).show()
        } else if(item.itemId == R.id.menu_paste) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if(text != null && text.isNotEmpty()) {
                PinText.setText(text)
                updatePlayfair()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}