package com.example.ladm_u4_p2_galeriaimagenes

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ladm_u4_p2_galeriaimagenes.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val autenticacion = FirebaseAuth.getInstance()
    private val baseRemota = FirebaseFirestore.getInstance().collection("Eventos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Blanca Ramirez-Daniel Ayala U4P2")
        switchVisibilidad()

        var propietario = autenticacion.currentUser?.email.toString()

        binding.crearEvento.setOnClickListener {
            if(!binding.txtEvento.text.toString().isEmpty()){
                val cal = GregorianCalendar.getInstance()
                var idEvento = ""
                idEvento = cal.get(Calendar.HOUR).toString() +
                        cal.get(Calendar.MINUTE).toString() +
                        cal.get(Calendar.MILLISECOND).toString()

                val progrssData = ProgressDialog(this)
                progrssData.setMessage("Subiendo los datos")
                progrssData.show()

                val datos = hashMapOf(
                    "idEvento" to idEvento,
                    "titulo" to binding.txtEvento.text.toString(),
                    "visibilidad" to binding.swicthVisibilidad.text.toString(),
                    "propietario" to propietario,
                    "estado" to "Evento Abierto"
                )

                baseRemota.document(propietario+idEvento).set(datos)
                    .addOnSuccessListener {
                        mensaje("SE CREO EL EVENTO")
                        binding.txtEvento.setText("")
                        progrssData.dismiss()
                        invocarVentanaEvento(idEvento)
                    }
                    .addOnFailureListener {
                        alerta("Error... \n${it.message}")
                        progrssData.dismiss()
                    }
            } else {
                alerta("Campos vacios... revisalos")
                return@setOnClickListener
            }
        }

        binding.unirseEvento.setOnClickListener {
            var id = binding.unirseID.text.toString()
            if (id.isEmpty()) {
                alerta("Campos idEvento vacio")
                return@setOnClickListener
            }
            var datos = Dato()
            var propietario = autenticacion.currentUser?.email.toString()
            baseRemota.whereEqualTo("idEvento",id)
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
                        datos.titulo = documento.getString("titulo").toString()
                    }
                    if (datos.propietario == propietario) {
                        cargarDatos(id)
                    } else {
                        if (datos.visibilidad == "Oculto") {
                            mensaje("No se encontro evento")
                        }
                        if (datos.visibilidad == "Público") {
                            cargarDatos(id)
                        }else{
                            mensaje("No se encontro evento")
                        }
                    }
                }
        }

        binding.misEventos.setOnClickListener{
            invocarVentanaMisEventos(propietario)
        }

        binding.unirseEvento2.setOnClickListener {
            binding.unirseID.setText(MisEventos.textoCopiado)
        }
    }

    private fun switchVisibilidad() {
        val visi = resources.getStringArray(R.array.visibilidad)
        val arrayAdapter = ArrayAdapter(this, R.layout.switchitem, visi)
        binding.swicthVisibilidad.setAdapter(arrayAdapter)
    }


    private  fun invocarVentanaEvento(idEvento : String) {
        val otraVentana = Intent(this, Eventos::class.java)
        otraVentana.putExtra("idEvento",idEvento)
        startActivity(otraVentana)
        finish()
    }

    private  fun invocarVentanaMisEventos(propietario : String) {
        val otraVentana = Intent(this, MisEventos::class.java)
        otraVentana.putExtra("propetario", propietario)
        startActivity(otraVentana)
        finish()
    }

    private fun cargarDatos(idEvento : String){
        baseRemota.whereEqualTo("idEvento",idEvento).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (!it.result.isEmpty) {
                    invocarVentanaEvento(idEvento)
                } else {
                    mensaje("No se encontro evento")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var integrantes = "Integrantes:\n" +
                "16400887 DANIEL ALEJANDRO AYALA RAMIREZ\n" +
                "16400985 BLANCA ESTEFANI RAMIREZ BARAJAS\n"

        when(item.itemId){
            R.id.acercade -> {
                AlertDialog.Builder(this)
                    .setTitle("BINA:")
                    .setMessage(integrantes)
                    .setPositiveButton("Salir"){_,_ ->}
                    .show()
            }
            R.id.cerrarsesión -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,Login::class.java))
                finish()
            }
            R.id.salir -> {
                finish()
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        binding.swicthVisibilidad.setText("Público")
        switchVisibilidad()
    }

    private fun alerta(cadena: String) {
        AlertDialog.Builder(this)
            .setTitle("CAMPOS VACIOS")
            .setMessage(cadena)
            .setNegativeButton("OK") {_,_ -> }
            .show()
    }

    private fun mensaje(cadena : String) {
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }
}