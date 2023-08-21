package com.example.bussrute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var btnMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        btnMenu = findViewById(R.id.btnMenu)
        loadFragment(pantallaPrincipalFragment())

        // Evento al dar click a una de las opciones del botón
        btnMenu.setOnItemReselectedListener{MenuItem->
            when (MenuItem.itemId) {
                R.id.Inicio->{
                    supportActionBar?.setTitle("bussrute")
                    loadFragment(pantallaPrincipalFragment())
                    true
                }
                R.id.comentarios->{
                    supportActionBar?.setTitle("Comentarios")
                    loadFragment(comentariosFragment())
                    true
                }
                R.id.buscarRuta->{
                    supportActionBar?.setTitle("Buscar Ruta")
                    loadFragment(buscarRutaFragment())
                    true
                }
                R.id.favoritos->{
                    supportActionBar?.setTitle("Favoritos")
                    loadFragment(favoritosFragment())
                    true
                }
                R.id.settings->{
                    supportActionBar?.setTitle("Ajustes")
                    loadFragment(settingsFragment())
                    true
                }

                else->false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView,fragment)
            .commit()
    }
}