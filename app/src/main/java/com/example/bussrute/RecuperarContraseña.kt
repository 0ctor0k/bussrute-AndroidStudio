package com.example.bussrute

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
import kotlin.random.Random

class RecuperarContraseña : AppCompatActivity(){
    lateinit var txtCorreoCambio: EditText
    lateinit var btnEnviarCorreo: Button
    private var url: String = "https://bussrute.pythonanywhere.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        txtCorreoCambio = findViewById(R.id.txtCorreoCambio)
        btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo)
        btnEnviarCorreo.setOnClickListener{ enviarCorreoConLink() }
    }
    fun generarToken(): String {
        val caracteres = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(32) { caracteres[Random.nextInt(caracteres.size)] }.joinToString("")
    }
    fun limpiar(){
        txtCorreoCambio.text.clear()
    }
    private fun esCorreoValido(correo: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }
    private fun enviarCorreoConLink() {
        val correoCambio = txtCorreoCambio.text.toString()
        val tokenCambio = generarToken()
        if (correoCambio.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            return
        }
        if (!esCorreoValido(correoCambio)) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido.", Toast.LENGTH_LONG).show()
            return
        }
        val urlUsuarios = url + "usuario"

        val request = JsonArrayRequest(
            Request.Method.GET, urlUsuarios, null,
            { response ->
                for (i in 0 until response.length()) {
                    val usuario = response.getJSONObject(i)
                    if (usuario.getString("usuCorreo") == correoCambio) {
                        if (!usuario.getBoolean("usuCreadoConGoogle")) {
                            val url = url+"enviarCorreoRecuperacion/"
                            val stringRequest = object : StringRequest(
                                Method.POST, url,
                                Response.Listener<String> { response ->
                                    limpiar()
                                    Toast.makeText(this, "Correo de recuperacion enviado correctamente", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, inicio_sesion::class.java)
                                    startActivity(intent)
                                    finish()
                                },
                                Response.ErrorListener {
                                    Toast.makeText(this, "El correo no existe, Verifica nuevamente.", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                override fun getParams(): Map<String, String> {
                                    val params = HashMap<String, String>()
                                    params["correoCambio"] = correoCambio
                                    params["tokenCambio"] = tokenCambio
                                    return params
                                }
                            }
                            Volley.newRequestQueue(this).add(stringRequest)
                        } else {
                            Toast.makeText(this, "Este usuario ha creado su cuenta con Google.", Toast.LENGTH_SHORT).show()
                            limpiar()
                        }
                        return@JsonArrayRequest
                    }
                }
                Toast.makeText(this, "El correo no existe, Verifica nuevamente.", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error al obtener la información del usuario.", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }


}