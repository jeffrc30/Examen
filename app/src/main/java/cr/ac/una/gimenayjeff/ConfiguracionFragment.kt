package cr.ac.una.gimenayjeff

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import cr.ac.una.gimenayjeff.clases.Preferencia
import cr.ac.una.gimenayjeff.dao.PreferenciasDAO
import cr.ac.una.gimenayjeff.db.AppDatabase
import kotlinx.coroutines.launch

class ConfiguracionFragment : Fragment() {

    private lateinit var preferenciasDao: PreferenciasDAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuracion, container, false)

        // Inicializa la base de datos
        preferenciasDao = AppDatabase.getInstance(requireContext()).preferenciasDao()

        val numeroEditText: EditText = view.findViewById(R.id.preferenciaNumero)
        val guardarButton: Button = view.findViewById(R.id.guardarButton)

        guardarButton.setOnClickListener {
            val numeroString = numeroEditText.text.toString()
            if (numeroString.isNotEmpty()) {
                var numero = numeroString.toInt()

                // Realizar ajustes de rango
                when {
                    numero < 1 -> {
                        numero = 1
                        mostrarAlerta("El número debe ser al menos 1. Se ha puesto como preferencia 1.")
                    }
                    numero > 15 -> {
                        numero = 15
                        mostrarAlerta("El número debe ser como máximo 15. Se ha puesto como preferencia 15.")
                    }
                    else -> {
                        mostrarAlerta("Ahora verá $numero locaciones recientes.")
                    }
                }

                // Guardar el número en la base de datos
                lifecycleScope.launch {
                    manejarPreferencia(numero)
                }
            } else {
                mostrarAlerta("Ingrese un número válido.")
            }
        }

        return view
    }

    private fun mostrarAlerta(mensaje: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alerta")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val alerta = builder.create()
        alerta.show()
    }

    private suspend fun manejarPreferencia(numero: Int) {
        try {
            val preferenciaExistente = preferenciasDao.getFirstPreferencia()
            if (preferenciaExistente != null) {
                // Si existe, actualizarla
                preferenciaExistente.CantidadFavoritos = numero
                preferenciasDao.update(preferenciaExistente)
                Log.d("ConfiguracionFragment", "Preferencia actualizada con el número: $numero")
            } else {
                // Si no existe, insertarla
                val nuevaPreferencia = Preferencia(id = null, CantidadFavoritos = numero)
                preferenciasDao.insert(nuevaPreferencia)
                Log.d("ConfiguracionFragment", "Nueva preferencia insertada con el número: $numero")
            }
        } catch (e: Exception) {
            Log.e("ConfiguracionFragment", "Error al manejar la preferencia: ${e.message}")
        }
    }
}
