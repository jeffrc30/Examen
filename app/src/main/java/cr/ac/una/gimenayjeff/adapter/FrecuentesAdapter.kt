package cr.ac.una.gimenayjeff.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import cr.ac.una.gimenayjeff.R
import cr.ac.una.gimenayjeff.clases.Movimiento
import cr.ac.una.gimenayjeff.clases.TopMovimiento

class FrecuentesAdapter(
    context: Context,
    movimientos: List<TopMovimiento>,
    private val lifecycleScope: LifecycleCoroutineScope
) : ArrayAdapter<TopMovimiento>(context, 0, movimientos) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_frecuentes, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val titleView = view.findViewById<TextView>(R.id.TitleView)
        val extractView = view.findViewById<TextView>(R.id.extractView)
        val visitasView = view.findViewById<TextView>(R.id.VisitasView)

        val movimiento = getItem(position)

        // Configura la imagen predeterminada
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder))

        // Configura los textos basados en los datos de `TopMovimiento`
        titleView.text = movimiento?.Nombre ?: "Sin nombre"
        extractView.text = movimiento?.Descripcion?.let {
            if (it.length > 30) it.substring(0, 30) + "..." else it
        } ?: "Sin descripción"
        visitasView.text = "Apareció ${movimiento?.Count ?: 0} veces"

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
