package cr.ac.una.gimenayjeff.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import cr.ac.una.gimenayjeff.clases.page
import android.content.Intent
import android.net.Uri
import cr.ac.una.gimenayjeff.R


class BuscadorAdapter(context: Context, pages: List<page>, private val listener: OnItemClickListener) :
    ArrayAdapter<page>(context, 0, pages) {

  /*  interface OnItemClickListener {
        fun onItemClick(articulo: Articulo)
    }*/
    interface OnItemClickListener {
      fun onItemClick(page: page)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_busqueda, parent, false)

        val title = view.findViewById<TextView>(R.id.titleView)
        val extract = view.findViewById<TextView>(R.id.extractView)
        val imageView = view.findViewById<ImageView>(R.id.image_view)

        val pageItem = getItem(position)

        title.text = pageItem?.titles?.normalized ?: "Sin título"

        val extractText = pageItem?.extract ?: "Sin extracto"
        extract.text = if (extractText.length > 300) extractText.substring(0, 300) + "..." else extractText

        pageItem?.thumbnail?.source?.let { url ->
            Glide.with(context)
                .load(url)
                .into(imageView)
        } ?: imageView.setImageResource(R.drawable.placeholder) // Imagen placeholder

        view.setOnClickListener {
            val pageItem = getItem(position)
            val url = "https://es.wikipedia.org/wiki/${pageItem?.title}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }

        return view
    }
}