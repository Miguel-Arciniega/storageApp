package com.example.ladm_u4_p2_galeriaimagenes

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ladm_u4_p2_galeriaimagenes.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val autenticacion = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Blanca Ramirez-Daniel Ayala U4P2")

        if (FirebaseAuth.getInstance().currentUser != null) {
            //SESION ACTIVA
            invocarOtraVentana()
        }

        binding.inscribir.setOnClickListener {
            if (binding.correo.text.toString().isEmpty() && binding.txtPass.text.toString().isEmpty()) {
                alerta("Agregar correo y contraseña valido, por favor")
                return@setOnClickListener
            }
            autenticacion.createUserWithEmailAndPassword(binding.correo.text.toString(), binding.txtPass.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        binding.correo.setText("")
                        binding.txtPass.setText("")
                        mensaje("SE CREO CUENTA")
                        autenticacion.signOut()
                    }else{
                        alertaMensaje("ATENCION","ERROR NO SE CONTRUYO")
                    }
                }
        }

        binding.autenticar.setOnClickListener {
            val dialogo = ProgressDialog(this)
            dialogo.setMessage("AUTENTICANDO USUARIO/CONTRASEÑA")
            dialogo.setCancelable(false)
            dialogo.show()

            autenticacion.signInWithEmailAndPassword(
                binding.correo.text.toString(),
                binding.txtPass.text.toString()
            ).addOnCompleteListener {
                dialogo.dismiss()
                if(it.isSuccessful){
                    invocarOtraVentana()
                    return@addOnCompleteListener
                }else{
                    alertaMensaje("ERROR","NO COINCIDE CON CORREO/CONTRASEÑA")
                }
            }
        }
    }

    private  fun invocarOtraVentana() {
        val otraVentana = Intent(this,MainActivity::class.java)
        startActivity(otraVentana)
        finish()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        supportActionBar!!.show()
    }

    fun alertaMensaje(titulo : String,cadena: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(cadena)
            .setNegativeButton("OK") {_,_ -> }
            .show()
    }

    fun alerta(cadena: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle("CAMPOS VACIOS")
            .setMessage(cadena)
            .setNegativeButton("OK") {_,_ -> }
            .show()
    }

    fun mensaje(cadena : String) {
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show()
    }
}