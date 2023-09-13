package com.example.bussrute

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
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


        btnVerificarCuenta.setOnClickListener {
            agregarUsuario()
        }

    }
    fun generarCodigoVerificacion(): String {
        return (100000..999999).random().toString()
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
        val nombreUsuario = txtNombreUsuario.text.toString()
        val correoElectronico = txtCorreoElectronico.text.toString()
        val contraseña = txtContraseña.text.toString()
        if (nombreUsuario.isEmpty() || correoElectronico.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            return
        }
        if (nombreUsuario.length < 6) {
            Toast.makeText(this, "El nombre de usuario tiene que ser mínimo de 6 caracteres", Toast.LENGTH_LONG).show()
            return
        }
        val url = url + "usuario"
        // Primero, verifica si el nombre de usuario y el correo electrónico ya existen
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val usuario = response.getJSONObject(i)
                    if (usuario.getString("usuNombre") == nombreUsuario) {
                        // Si el nombre de usuario ya existe, muestra un mensaje y termina la función
                        Toast.makeText(this, "El nombre de usuario ya existe, por favor utiliza otro", Toast.LENGTH_LONG).show()
                        return@JsonArrayRequest
                    }
                    if (usuario.getString("usuCorreo") == correoElectronico) {
                        // Si el correo electrónico ya existe, muestra un mensaje y termina la función
                        Toast.makeText(this, "El correo electrónico ya está en uso, por favor utiliza otro", Toast.LENGTH_LONG).show()
                        return@JsonArrayRequest
                    }
                }
                val hashedPassword = hashPassword(contraseña)
                // Si el nombre de usuario y el correo electrónico no existen, guarda los datos del usuario en las preferencias compartidas
                val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putString("usuNombre", nombreUsuario)
                    putString("usuCorreo", correoElectronico)
                    putString("usuContraseña", hashedPassword)
                    apply()
                }
                enviarCorreoVerificacion()
                val intent = Intent(this, VerificarCorreo::class.java)
                startActivity(intent)
                finishAffinity()
            },
            { error ->
                // Manejo de errores
                Toast.makeText(this, "Error${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }
    private fun enviarCorreoVerificacion() {
        val url = url+"enviarCorreoMovil/"
        val queue = Volley.newRequestQueue(this)
        val parametros = HashMap<String, String>()
        parametros.put("correoUsuarioIngresado", txtCorreoElectronico.text.toString())
        val codigoVerificacion = generarCodigoVerificacion()
        parametros.put("codigoVerificacionMovil", codigoVerificacion)

        // Guarda el código de verificación en las preferencias compartidas
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("codigoVerificacion", codigoVerificacion)
            apply()
        }
        val resultadoPost = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener<String>{ response ->
                Toast.makeText(this, "Correo de verificación enviado", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener{ error ->
                Toast.makeText(this, "Error al enviar el correo de verificación: ${error.message}", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                return parametros
            }
        }
        queue.add(resultadoPost)
    }

}