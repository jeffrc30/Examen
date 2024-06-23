package cr.ac.una.gimenayjeff.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import cr.ac.una.gimenayjeff.R
import cr.ac.una.gimenayjeff.clases.Movimiento

class RecientesAdapter(context: Context, movimientos: List<Movimiento>, private val lifecycleScope: LifecycleCoroutineScope)
    : ArrayAdapter<Movimiento>(context, 0, movimientos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_lugares, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val titleView = view.findViewById<TextView>(R.id.TitleView)
        val extractView = view.findViewById<TextView>(R.id.extractView)
        val fechaView = view.findViewById<TextView>(R.id.FechaView)

        val movimiento = getItem(position)

        // Configura la imagen predeterminada
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder))

        // Configura los textos basados en los datos de `Movimiento`
        titleView.text = movimiento?.Nombre ?: "Sin nombre"
        extractView.text = movimiento?.Descripcion?.let {
            if (it.length > 30) it.substring(0, 30) + "..." else it
        } ?: "Sin descripción"
        fechaView.text = movimiento?.FechayHora ?: "Fecha desconocida"

        // Carga la imagen desde la URL usando una biblioteca como Glide o Picasso
        movimiento?.Imagen?.let { imageUrl ->
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(imageView)
        }

        return view
    }
}
