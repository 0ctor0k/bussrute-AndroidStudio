package com.example.bussrute

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.security.MessageDigest

class inicio_sesion : AppCompatActivity() {
    lateinit var  txtCorreoONombre: EditText
    lateinit var  txtContraseña: EditText
    lateinit var  btnInicio:Button
    private var url ="https://bussrute.pythonanywhere.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        txtCorreoONombre = findViewById(R.id.txtCorreoONombre)
        txtContraseña = findViewById(R.id.txtContraseña)
        btnInicio = findViewById(R.id.btnInicio)


        btnInicio.setOnClickListener{iniciarSesion()}
        val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", null)

        if (idUsuario != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        val recuperarContraseña: TextView = findViewById(R.id.txtRecuperarContraseña)
        recuperarContraseña.setOnClickListener {
            val intent = Intent(this, RecuperarContraseña::class.java)
            startActivity(intent)
        }

        val createAccountTextView: TextView = findViewById(R.id.createAccountTextView)
        createAccountTextView.setOnClickListener {
            val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val codigoVerificacion = sharedPref.getString("codigoVerificacion", null)
            val intent = if (codigoVerificacion != null) {
                Intent(this, VerificarCorreo::class.java)
            } else {
                Intent(this, CrearCuenta::class.java)
            }
            startActivity(intent)
        }
    }

    fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun limpiar(){
        txtCorreoONombre.text.clear()
        txtContraseña.text.clear()
    }

    private fun iniciarSesion() {
        if (txtCorreoONombre.text.toString().isEmpty() || txtContraseña.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, llena ambos campos", Toast.LENGTH_LONG).show()
            return
        }

        val url = url+"usuario"

        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response ->
                var usuarioEncontrado = false
                var contraseñaCorrecta = false
                var idUsuario = ""
                for (i in 0 until response.length()) {
                    val usuario = response.getJSONObject(i)
                    if (usuario.getString("usuNombre") == txtCorreoONombre.text.toString() ||
                        usuario.getString("usuCorreo") == txtCorreoONombre.text.toString()) {
                        usuarioEncontrado = true
                        idUsuario = usuario.getString("id")
                        if (usuario.getString("usuPassword") == hashPassword(txtContraseña.text.toString())) {
                            contraseñaCorrecta = true
                            break
                        }
                    }
                }
                runOnUiThread {
                    when {
                        !usuarioEncontrado -> Toast.makeText(this, "El nombre de usuario o correo no se encuentran registrados", Toast.LENGTH_LONG).show()
                        !contraseñaCorrecta -> Toast.makeText(this, "La contraseña proporcionada es incorrecta", Toast.LENGTH_LONG).show()
                        else -> {
                            limpiar()
                            Toast.makeText(this, "Has iniciado correctamente", Toast.LENGTH_LONG).show()

                            // Guarda la información del usuario en las preferencias compartidas osea la session del usuario
                            val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("idUsuario", idUsuario)
                            editor.apply()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            },
            Response.ErrorListener { error ->
                runOnUiThread {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )

        requestQueue.add(request)
    }
}