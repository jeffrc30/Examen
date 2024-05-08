package cr.ac.una.controlfinancierocamera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EditarMovimientoFragment : Fragment() {

    lateinit var saveButton: Button
    lateinit var cancelButton: Button
    lateinit var captureButton : Button
    lateinit var tipoMovimientoSpinner: Spinner
    lateinit var montoEditText: TextView
    private lateinit var datePicker: DatePicker
    lateinit var imageView: ImageView
    lateinit var movimiento: Movimiento

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            // Permiso denegado, manejar la situación aquí si es necesario
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
        } else {
            // Manejar el caso en el que no se haya podido capturar la imagen
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_editar_movimiento, container, false)
        saveButton = view.findViewById(R.id.saveMovimientoButtonEditar)
        cancelButton = view.findViewById(R.id.cancelButtonEditar)
        tipoMovimientoSpinner = view.findViewById(R.id.tipoMovimientoSpinnerEditar)
        montoEditText = view.findViewById(R.id.textMontoEditar)
        datePicker = view.findViewById(R.id.textFechaEditar)
        imageView = view.findViewById<ImageView>(R.id.imageView)

        movimiento = arguments?.getSerializable("movimiento") as Movimiento

        montoEditText.text = movimiento.monto.toString()
        val fecha = movimiento.fecha.split("/")
        datePicker.updateDate(fecha[2].toInt(), fecha[1].toInt(), fecha[0].toInt())
        captureButton = view.findViewById(R.id.captureButtonEditar)
        imageView = view.findViewById(R.id.imageView)

        //imageView.setImageBitmap(movimiento.img)

        val tiposMovimiento = resources.getStringArray(R.array.tiposMovimiento)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tiposMovimiento,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoMovimientoSpinner.adapter = adapter
            // Establecer el valor seleccionado en el Spinner
            val selectedIndex = tiposMovimiento.indexOf(movimiento.tipo)
            tipoMovimientoSpinner.setSelection(selectedIndex)
        }

        saveButton.setOnClickListener {
            mostrarConfirmacionGuardar()
        }

        cancelButton.setOnClickListener {
            // Regresar al fragmento anterior sin guardar cambios
            requireActivity().supportFragmentManager.popBackStack()
        }

        captureButton.setOnClickListener {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermission()
            }
        }

        return view
    }
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun mostrarConfirmacionGuardar() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que quieres guardar la edición?")
        builder.setPositiveButton("Sí") { dialogInterface: DialogInterface, _: Int ->
            // Lógica para guardar el movimiento aquí
            guardarMovimiento()
            dialogInterface.dismiss() // Cerrar el diálogo
        }
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss() // Cerrar el diálogo
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun guardarMovimiento() {
        val nuevoMonto = montoEditText.text.toString().toDouble()
        val nuevaFecha = datePicker.dayOfMonth.toString() + "/" + datePicker.month.toString() + "/" + datePicker.year.toString()
        val nuevoTipo = tipoMovimientoSpinner.selectedItem.toString()
        val bitmap: Bitmap = imageView.drawToBitmap()
       // val nuevoImg = obtenerBitmapDesdeImageView(imageView)


        val movimientoActualizado = Movimiento(
            movimiento._uuid,
            nuevoMonto,
            nuevoTipo,
            nuevaFecha,
            bitmap,
        )
        val actividad = activity as MainActivity
        GlobalScope.launch(Dispatchers.IO) {
            actividad.movimientoController.editarMovimiento(movimientoActualizado)
            // Regresa al fragmento anterior
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
        // Actualizar los datos del movimiento con los valores del formulario
        //val fragmentManager = requireActivity().supportFragmentManager
        //fragmentManager.popBackStack()
    }
}