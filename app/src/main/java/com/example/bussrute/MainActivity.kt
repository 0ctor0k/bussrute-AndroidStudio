package com.example.bussrute

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.widget.Toast
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var btnMenu: BottomNavigationView

    // Declaración de la variable global para el nombre del usuario
    var nombreUsuario: String? = null
    private lateinit var nombreUsuarioView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        btnMenu = findViewById(R.id.btnMenu)
        loadFragment(pantallaPrincipalFragment())

        // Obtén el nombre del usuario desde el servidor
        val sharedPreferences = this.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", "")

        val url = "https://bussrute.pythonanywhere.com/usuario/$idUsuario"

        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response ->
                nombreUsuario = response.getString("usuNombre")
                if (this::nombreUsuarioView.isInitialized) {
                    nombreUsuarioView.text = nombreUsuario
                }
                invalidateOptionsMenu() // Esto hará que se llame a onPrepareOptionsMenu()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(request)

        // Evento al dar click a una de las opciones del botón
        btnMenu.setOnNavigationItemSelectedListener { MenuItem ->
            when (MenuItem.itemId) {
                R.id.Inicio -> {
                    supportActionBar?.setTitle("Buscar Ruta")
                    loadFragment(pantallaPrincipalFragment())
                    true
                }
                R.id.comentarios -> {
                    supportActionBar?.setTitle("Comentarios")
                    loadFragment(comentariosFragment())
                    true
                }
                R.id.favoritos -> {
                    supportActionBar?.setTitle("Favoritos")
                    loadFragment(favoritosFragment())
                    true
                }
                R.id.settings -> {
                    AlertDialog.Builder(this)
                        .setTitle("Salir")
                        .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
                        .setPositiveButton("Sí") { dialog, which ->
                            val sharedPreferences = this.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.remove("idUsuario")
                            editor.apply()

                            val intent = Intent(this, inicio_sesion::class.java)
                            startActivity(intent)

                            this.finish()
                        }
                        .setNegativeButton("No", null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_nombre, menu)
        val item = menu.findItem(R.id.nombreUsuario)
        val view = layoutInflater.inflate(R.layout.nombre_diseno, null)
        nombreUsuarioView = view.findViewById<TextView>(R.id.nombreUsuario)
        item.actionView = view
        nombreUsuarioView.text = nombreUsuario
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        nombreUsuarioView.text = nombreUsuario ?: "Usuario"
        return super.onPrepareOptionsMenu(menu)
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView,fragment)
            .commit()
    }
}