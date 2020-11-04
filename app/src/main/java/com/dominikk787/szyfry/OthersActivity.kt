package com.dominikk787.szyfry

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_others.*
import kotlinx.android.synthetic.main.layout_others_recycler.view.*

class OthersActivity : AppCompatActivity() {

    data class MyOtherCipherItem(val title: String, val img: Int, val description: String)
    class MyOtherCipherAdapter(private val context: Context, private var list: List<MyOtherCipherItem>) : RecyclerView.Adapter<MyOtherCipherAdapter.MyViewHolder>() {
        data class MyViewHolder(val view: View, val text1: TextView, val text2: TextView, val div: View, var pos: Int) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int = list.size
        private fun getItem(pos: Int): MyOtherCipherItem = list[pos]
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.layout_others_recycler, parent, false)
            println("create")
            return MyViewHolder(view, view.text1, view.text2, view.div, -1)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.pos = position
            holder.text1.text = getItem(position).title
            if(getItem(position).img != 0) {
                val img = ResourcesCompat.getDrawable(context.resources, getItem(position).img, null) as BitmapDrawable
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    val w = img.bitmap.width
                    val mx = Resources.getSystem().displayMetrics.widthPixels / 2.5F
                    val scale = mx / w
                    val sd = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(img.bitmap, (img.bitmap.width * scale).toInt(), (img.bitmap.height * scale).toInt(), false))
                    sd.level = 10000
                    println("img $w $mx $sd ${mx / w}")
                    holder.text2.setCompoundDrawablesWithIntrinsicBounds(sd, null, null, null)
                } else {
                    val w = img.bitmap.width
                    val mx = Resources.getSystem().displayMetrics.widthPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20F, context.resources.displayMetrics)
                    val scale = mx / w
                    val sd = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(img.bitmap, (img.bitmap.width * scale).toInt(), (img.bitmap.height * scale).toInt(), false))
                    println("img $w $mx $sd ${mx / w}")
                    holder.text2.setCompoundDrawablesWithIntrinsicBounds(null, sd, null, null)
                }
            }
            holder.text2.text = HtmlCompat.fromHtml(getItem(position).description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            if(position == list.size - 1) holder.div.visibility = View.INVISIBLE
            println("bind $holder")
        }
    }

    private var list = mutableListOf<MyOtherCipherItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others)

        val namesa = resources.getStringArray(R.array.others_names)
        val imgsa = resources.obtainTypedArray(R.array.others_imgs)
        val descsa = resources.getStringArray(R.array.others_descs)
        if(namesa.isNotEmpty() && imgsa.length() > 0 && descsa.isNotEmpty()) {
            val n = minOf(namesa.size, imgsa.length(), descsa.size) - 1
            for(i in 0..n) {
                println("$i")
                val idi = imgsa.getResourceId(i, 0)
                val name = namesa[i] ?: ""
                val desc = descsa[i] ?: ""
                list.add(MyOtherCipherItem(name, idi, desc))
            }
        }
        imgsa.recycle()

        Orecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@OthersActivity)
            adapter = MyOtherCipherAdapter(this@OthersActivity, list)
        }
    }
}