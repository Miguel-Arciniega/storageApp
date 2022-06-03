package com.example.ladm_u4_p2_galeriaimagenes

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ladm_u4_p2_galeriaimagenes.databinding.ActivityEventosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mx.edu.ittepic.daar.ladm_u4_p2_albumfotos_berb_daar.CustomAdapter
import mx.edu.ittepic.daar.ladm_u4_p2_albumfotos_berb_daar.Dato
import java.io.File
import java.util.*

class Eventos : AppCompatActivity() {
    lateinit var binding : ActivityEventosBinding
    val autenticacion = FirebaseAuth.getInstance()
    private val baseRemota = FirebaseFirestore.getInstance().collection("Eventos")
    private lateinit var imageUri : Uri
    var vector = ArrayList<Imagen>()
    var datos = Dato()
    var idEvento = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idEvento = this.intent.extras!!.getString("idEvento")!!
        cargarDatos(idEvento)
        imagenesStorage(idEvento)

        binding.elegirImagen.setOnClickListener {
            val galeria = Intent(Intent.ACTION_GET_CONTENT)
            galeria.type = "image/*"
            startActivityForResult(galeria,2)
        }

        binding.subir.setOnClickListener {
            subirArchivos(idEvento)
        }

        binding.cambios.setOnClickListener {
            var propietario = autenticacion.currentUser?.email.toString()
            var estado = binding.swEstadoEvento.isChecked
            var visibilidad = binding.swOculto.isChecked
            var visible = ""
            var status = "Evento Abierto"

            if (estado == true) {
                status = "Evento Abierto"
            }
            if (estado == false) {
                status = "Evento Cerrado"
            }
            if (visibilidad == false) {
                visible = "Oculto"
            }
            if (visibilidad == true) {
                visible = "Público"
            }
            val datos = hashMapOf(
                "idEvento" to idEvento,
                "titulo" to title,
                "visibilidad" to visible,
                "propietario" to propietario,
                "estado" to status
            )

            baseRemota.document(propietario+idEvento).set(datos)
                .addOnSuccessListener {
                    mensaje("SE ACTUALIZO EL EVENTO")
                }
                .addOnFailureListener {
                    alerta("Error... \n${it.message}")
                }
        }

        binding.regresar.setOnClickListener {
            val otraVentana = Intent(this,MainActivity::class.java)
            startActivity(otraVentana)
            finish()
        }
    }

    private fun imagenesStorage(idEvento: String) {
        vector.clear()
            val storageRef = FirebaseStorage.getInstance().reference.child(idEvento)
            storageRef.listAll()
                .addOnSuccessListener {
                    it.items.forEach {
                        val foto =
                            FirebaseStorage.getInstance().reference.child("${idEvento}/${it.name}")

                        val archivoTemporal = File.createTempFile("imagenTemp", ".jpg")

                        foto.getFile(archivoTemporal)
                            .addOnSuccessListener {
                                val imagenBits = BitmapFactory.decodeFile(archivoTemporal.absolutePath)
                                vector.add(Imagen(imagenBits))
                                val adapter = CustomAdapter(vector)
                                binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                                binding.recyclerView.adapter = adapter
                            }
                    }
                }
    }

    private fun subirArchivos(idEvento: String){
        var idEvento = idEvento
        val cal = GregorianCalendar.getInstance()
        var nombre = ""
        nombre = cal.get(Calendar.YEAR).toString() +
                cal.get(Calendar.MONTH).toString() +
                cal.get(Calendar.DAY_OF_MONTH).toString() +
                cal.get(Calendar.HOUR).toString() +
                cal.get(Calendar.MINUTE).toString() +
                cal.get(Calendar.SECOND).toString() +
                cal.get(Calendar.MILLISECOND).toString()

        val imageRef = FirebaseStorage.getInstance().reference.child("${idEvento}/${nombre}")
        try {
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    mensaje("SE SUBIO ARCHIVO")
                    binding.fotos.setImageBitmap(null)
                    imagenesStorage(idEvento)
                }
                .addOnFailureListener {
                    alerta("NO SE PUDO SUBIR LA IMAGEN")
                }
        } catch (e:Exception) {
            mensaje("No se ha seleccionado imagen")
        }
    }

    private fun cargarDatos(idEvento : String) {
        baseRemota.whereEqualTo("idEvento",idEvento)
            .addSnapshotListener { query, error ->
                if (error != null) {
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                for (documento in query!!) {
                    datos.propietario = documento.getString("propietario").toString()
                    datos.estado = documento.getString("estado").toString()
                    datos.visibilidad = documento.getString("visibilidad").toString()
                    datos.idEvento = documento.getString("idEvento").toString()
                    datos.nombreEvento = documento.getString("titulo").toString()
                }
                /**
                    Mandamos los valores que tiene el evento
                    y modificamos el titulo tambien
                 */
                if (datos.estado.equals("Evento Abierto")) {
                    binding.estatus.setTextColor(Color.GREEN)
                    binding.swEstadoEvento.isChecked = true
                    binding.elegirImagen.visibility = View.VISIBLE
                    binding.subir.visibility = View.VISIBLE
                }
                if (datos.estado.equals("Evento Cerrado")) {
                    binding.estatus.setTextColor(Color.RED)
                    binding.swEstadoEvento.isChecked = false
                    binding.elegirImagen.visibility = View.INVISIBLE
                    binding.subir.visibility = View.INVISIBLE
                }
                if (datos.visibilidad.equals("Público")) {
                    binding.swOculto.isChecked = true
                }
                if (datos.visibilidad.equals("Oculto")) {
                    binding.swOculto.isChecked = false
                }
                binding.idEventoActual.setText("ID EVENTO\n${datos.idEvento}")
                binding.creador.setText("Propietario:\n${datos.propietario}")
                binding.estatus.setText(datos.estado)
                setTitle(datos.nombreEvento)

                /**
                 * Idenficamos si el usuario que esta logeado es el propietario o no
                 * en caso de que no sea le ocultamos el panel de modificación
                 * */
                var usuario = autenticacion.currentUser?.email.toString()
                var propíetario = datos.propietario
                if (usuario.equals(propíetario)) {
                    binding.cardPanel.visibility = View.VISIBLE
                } else {
                    binding.cardPanel.visibility = View.INVISIBLE
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var integrantes = "Integrantes:\n" +
                "16400887 DANIEL ALEJANDRO AYALA RAMIREZ\n" +
                "16400985 BLANCA ESTEFANI RAMIREZ BARAJAS\n"
        /**
            por medio de la carpeta menu, hacemos referencia a los items que vamos a usar y la pasamos cuando
            sean seleccionados hacer dichas funciones que se ven a continuación
         */
        when (item.itemId) {
            R.id.acercade -> {
                AlertDialog.Builder(this)
                    .setTitle("BINA:")
                    .setMessage(integrantes)
                    .setPositiveButton("Salir") { _, _ -> }
                    .show()
            }
            R.id.cerrarsesión -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, Login::class.java))
                finish()
            }
            R.id.salir -> {
                finish()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            imageUri = data!!.data!!
            binding.fotos.setImageURI(imageUri)
        }
    }

    private fun alerta(cadena: String) {
        AlertDialog.Builder(this)
            .setTitle("CAMPOS VACIOS")
            .setMessage(cadena)
            .setNegativeButton("OK") { _, _ -> }
            .show()
    }

    private fun mensaje(cadena: String) {
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }
}