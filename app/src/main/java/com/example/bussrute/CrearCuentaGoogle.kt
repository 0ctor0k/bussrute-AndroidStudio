package com.example.bussrute

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.security.MessageDigest

class CrearCuentaGoogle : AppCompatActivity() {
    lateinit var txtNombreUsuarioGoogle: EditText
    lateinit var txtCorreoUsuarioGoogle: EditText
    lateinit var btnCrearCuentaGoogle: Button
    private var url: String = "https://bussrute.pythonanywhere.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta_google)

        txtNombreUsuarioGoogle = findViewById(R.id.txtNombreUsuarioGoogle)
        txtCorreoUsuarioGoogle = findViewById(R.id.txtCorreoUsuarioGoogle)
        btnCrearCuentaGoogle = findViewById(R.id.btnCrearCuentaGoogle)

        val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val correoUsuario = sharedPreferences.getString("correoUsuario", "")

        txtCorreoUsuarioGoogle.setText(correoUsuario)

        txtCorreoUsuarioGoogle.isEnabled = false


        btnCrearCuentaGoogle.setOnClickListener { crearUsuario() }

    }

    fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    private fun generarContraseña(): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..10)
            .map { caracteres.random() }
            .joinToString("")
    }
    private fun crearUsuario(){
        val nombreUsuarioGoogle = txtNombreUsuarioGoogle.text.toString()
        if (nombreUsuarioGoogle.isEmpty()){
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            return
        }
        if (nombreUsuarioGoogle.length < 6) {
            Toast.makeText(this, "El nombre de usuario tiene que ser mínimo de 6 caracteres", Toast.LENGTH_LONG).show()
            return
        }
        val url = url + "usuario"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val usuario = response.getJSONObject(i)
                    if (usuario.getString("usuNombre") == nombreUsuarioGoogle) {
                        // Si el nombre de usuario ya existe, muestra un mensaje y termina la función
                        Toast.makeText(this, "El nombre de usuario ya existe, por favor utiliza otro", Toast.LENGTH_LONG).show()
                        return@JsonArrayRequest
                    }
                }
                // Si el nombre de usuario no existe en la base de datos, crea un usuario
                val contraseña = generarContraseña()
                val contraseñaEncriptada = hashPassword(contraseña)

                // Recupera el correo electrónico de la sesión
                val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                val correoElectronico = sharedPreferences.getString("correoUsuario", "")

                // Realiza la solicitud POST para agregar el usuario a la base de datos
                val resultadoPost = object : StringRequest(
                    Request.Method.POST,url,
                    Response.Listener<String>{ response ->
                        // Parsea la respuesta para obtener el ID del usuario
                        val jsonResponse = JSONObject(response)
                        val idUsuario = jsonResponse.getString("id")

                        // Guarda el ID del usuario en las preferencias compartidas
                        with (sharedPreferences.edit()) {
                            putString("idUsuario", idUsuario)
                            apply()
                        }

                        Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            remove("correoUsuario")
                            apply()
                        }
                    }, Response.ErrorListener{ error ->
                        Toast.makeText(this, "El nombre de usuario ya existe, por favor utiliza otro", Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("usuNombre", nombreUsuarioGoogle)
                        parametros.put("usuCorreo", correoElectronico ?: "")
                        parametros.put("usuPassword", contraseñaEncriptada)
                        parametros.put("usuCreadoConGoogle", "true")
                        parametros.put("usuRol", "2")
                        return parametros
                    }
                }
                Volley.newRequestQueue(this).add(resultadoPost)
            },
            { error ->
                // Maneja los errores aquí
            }
        )
        Volley.newRequestQueue(this).add(request)
    }



}