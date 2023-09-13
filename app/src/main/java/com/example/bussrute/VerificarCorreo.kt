package com.example.bussrute

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VerificarCorreo : AppCompatActivity() {
    lateinit var txtCodigoVeri: EditText
    lateinit var btnContinuar: Button
    lateinit var btnCancelarVerificacion: Button
    private var url: String = "https://bussrute.pythonanywhere.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificar_correo)

        txtCodigoVeri = findViewById(R.id.txtCodigoVeri)
        btnContinuar = findViewById(R.id.btnContinuar)
        btnCancelarVerificacion = findViewById(R.id.btnCancelarVerificacion)
        btnContinuar.setOnClickListener{ codigoVerificacionConfirmacion() }
        btnCancelarVerificacion.setOnClickListener{ eliminarVerificacion() }

    }
    private fun codigoVerificacionConfirmacion() {
        // Recupera el código de verificación de las preferencias compartidas
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val codigoVerificacion = sharedPref.getString("codigoVerificacion", "")
        // Obtiene el código ingresado por el usuario
        val codigoIngresado = txtCodigoVeri.text.toString()
        // Compara los códigos
        if (codigoIngresado == codigoVerificacion) {
            // Recupera los datos del usuario de las preferencias compartidas
            val nombreUsuario = sharedPref.getString("usuNombre", "")
            val correoElectronico = sharedPref.getString("usuCorreo", "")
            val contraseña = sharedPref.getString("usuContraseña", "")

            val url = url + "usuario"
            val queue = Volley.newRequestQueue(this)
            // Realiza la solicitud POST para agregar el usuario a la base de datos
            val resultadoPost = object : StringRequest(
                Request.Method.POST,url,
                Response.Listener<String>{ response ->
                    // Parsea la respuesta para obtener el ID del usuario
                    val jsonResponse = JSONObject(response)
                    val idUsuario = jsonResponse.getString("id")

                    // Guarda el ID del usuario en las preferencias compartidas
                    with (sharedPref.edit()) {
                        putString("idUsuario", idUsuario)
                        apply()
                    }

                    Toast.makeText(this, "Código de verificación correcto, su usuario ha sido creado exitosamente", Toast.LENGTH_LONG).show()
                    val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        remove("usuNombre")
                        remove("usuCorreo")
                        remove("usuContraseña")
                        remove("codigoVerificacion")
                        apply()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, Response.ErrorListener{ error ->
                    Toast.makeText(this, "Error al agregar el usuario: ${error.message}", Toast.LENGTH_LONG).show()
                }){
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("usuNombre", nombreUsuario!!)
                    parametros.put("usuCorreo", correoElectronico!!)
                    parametros.put("usuPassword", contraseña!!)
                    parametros.put("usuRol", "2")
                    return parametros
                }
            }
            queue.add(resultadoPost)
        } else {
            // Si los códigos no coinciden, muestra un mensaje al usuario
            Toast.makeText(this, "Código de verificación incorrecto, por favor intenta de nuevo", Toast.LENGTH_LONG).show()
        }
    }

    private fun eliminarVerificacion() {
        // Muestra un cuadro de diálogo al usuario para confirmar la cancelación
        AlertDialog.Builder(this)
            .setTitle("Cancelar Verificación")
            .setMessage("¿Estás seguro de que quieres cancelar la verificación?")
            .setPositiveButton("Sí") { dialog, which ->
                // Si el usuario confirma, borra todas las sesiones
                val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    remove("usuNombre")
                    remove("usuCorreo")
                    remove("usuContraseña")
                    remove("codigoVerificacion")
                    apply()
                }
                Toast.makeText(this, "Verificación cancelada", Toast.LENGTH_LONG).show()
                // Aquí puedes agregar el código para redirigir al usuario a la actividad que desees después de cancelar la verificación
                val intent = Intent(this, inicio_sesion::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
