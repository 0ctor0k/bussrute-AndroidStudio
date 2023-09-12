package com.example.bussrute

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.security.MessageDigest

class CrearCuenta : AppCompatActivity() {
    lateinit var txtNombreUsuario: EditText
    lateinit var txtCorreoElectronico: EditText
    lateinit var txtContraseña: EditText

    lateinit var btnVerificarCuenta: Button
    private var url: String = "https://bussrute.pythonanywhere.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        txtCorreoElectronico = findViewById(R.id.txtCorreoUsuario)
        txtContraseña = findViewById(R.id.txtCreaContraseña)
        btnVerificarCuenta = findViewById(R.id.btnVerificarCuenta)


        btnVerificarCuenta.setOnClickListener{ agregarUsuario() }

    }
    fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    private fun limpiar() {
        txtNombreUsuario.text.clear()
        txtCorreoElectronico.text.clear()
        txtContraseña.text.clear()
    }

    private fun agregarUsuario() {
        val url = url + "usuario"
        val queue = Volley.newRequestQueue(this)
        val progresBar = ProgressDialog.show(this, "Enviando Datos...", "Espere por favor")

        val contraseña = txtContraseña.text.toString()
        val hashedPassword = hashPassword(contraseña)

        val resultadoPost = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener<String>{ response ->
                progresBar.dismiss()
                Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, inicio_sesion::class.java)
                startActivity(intent)
                limpiar()

            }, Response.ErrorListener{ error ->
                progresBar.dismiss()
                Toast.makeText(this, "Error${error.message}", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("usuNombre", txtNombreUsuario.text.toString())
                parametros.put("usuCorreo", txtCorreoElectronico.text.toString())
                parametros.put("usuPassword", hashedPassword)

                parametros.put("usuRol", "2")


                return parametros
            }
        }
        queue.add(resultadoPost)

    }
}