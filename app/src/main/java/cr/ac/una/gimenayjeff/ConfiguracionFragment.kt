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

class ConfiguracionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_configuracion, container, false)

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
                        mostrarAlerta("Ahora vera $numero locaciones recientes.")
                    }
                }

                // Log del número ajustado
                Log.d("ConfiguracionFragment", "Número ingresado y ajustado: $numero")
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
}
