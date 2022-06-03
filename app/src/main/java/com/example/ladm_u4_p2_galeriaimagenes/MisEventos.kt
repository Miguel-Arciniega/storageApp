package com.example.ladm_u4_p2_galeriaimagenes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ladm_u4_p2_galeriaimagenes.dao.DatabaseHelper
import com.example.ladm_u4_p2_galeriaimagenes.databinding.ActivityMisEventosBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MisEventos : AppCompatActivity() {

    lateinit var binding : ActivityMisEventosBinding
    private val databaseHelper = DatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisEventosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val propetario = this.intent.extras!!.getString("propetario")!!

        CoroutineScope(Dispatchers.IO).launch {
            updateListView(binding.listView,
                databaseHelper.obtenerEventosPorPropetario(propetario),
                applicationContext)
        }

        alerta()

        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                val evento = binding.listView.getItemAtPosition(position) as Dato

                // Construimos un AlertDialog
                AlertDialog.Builder(this)

                    // Añadimos el titulo y el mensaje al AlertDialog
                    .setTitle("AVISO")
                    .setMessage("¿Que acción desea realizar?")

                    // Si se hace click en el boton de ELIMINAR, processamos la acción
                    .setNegativeButton("COPIAR ID EVENTO") { _, _ ->
                        copyText(evento.idEvento)
                    }

                    // Si se hace click en el boton de ACTUALIZAR, mostramos el modal para actualizar
                    .setPositiveButton("VER EL EVENTO") { _, _ ->
                        invocarVentanaEvento(evento.idEvento)
                    }

                    // Si se hace click en el boton de CANCELAR, processamos la acción
                    .setNeutralButton("CANCELAR") { _, _ ->
                    }.show()
            }

        binding.regresar2.setOnClickListener{
            val otraVentana = Intent(this, MainActivity::class.java)
            startActivity(otraVentana)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val integrantes = "Integrantes:\n" +
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

    private  fun invocarVentanaEvento(idEvento : String) {
        val otraVentana = Intent(this, Eventos::class.java)
        otraVentana.putExtra("idEvento", idEvento)
        startActivity(otraVentana)
        finish()
    }

    fun copyText(eventoId : String) {
        textoCopiado = eventoId
        Toast.makeText(this, "Se guardo el id del evento en el portapaples", Toast.LENGTH_LONG).show()
    }

    private suspend fun updateListView(
        lvAreaView: ListView,
        list : List<Any>,
        context: Context
    ) {
        withContext(Dispatchers.Main) {
            if (list.isNotEmpty()) {
                lvAreaView.isClickable = true
                lvAreaView.adapter = ArrayAdapter(context,
                    android.R.layout.simple_list_item_1, list)
            } else {
                lvAreaView.isClickable = false
                lvAreaView.adapter = ArrayAdapter(context,
                    android.R.layout.simple_list_item_1, listOf("No tiene ningún evento creado"))
            }
        }
    }

    private fun alerta() {
        AlertDialog.Builder(this)
            .setTitle("Informacion")
            .setMessage("Para ver un evento dar click en el evento que esta en la lista y dar click en VER EVENTO. Donde puede usted editar.\n\n"+"Si quiere copiar un ID de evento para mandarlo a sus amigos dar click en copiar ID de evento.")
            .setNegativeButton("OK") {_,_ -> }
            .show()
    }

    companion object PortaPapeles {
        var textoCopiado = ""
    }
}