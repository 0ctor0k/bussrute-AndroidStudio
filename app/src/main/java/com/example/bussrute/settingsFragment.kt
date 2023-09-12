package com.example.bussrute

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [settingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class settingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var txtNombredelUsuario: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnCerrar = view.findViewById<Button>(R.id.btnCerrar)
        txtNombredelUsuario = view.findViewById(R.id.txtNombredelUsuario)

        val sharedPreferences = this.requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", "")

        val url = "https://bussrute.pythonanywhere.com/usuario/$idUsuario"

        val requestQueue = Volley.newRequestQueue(this.context)

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response ->
                val nombreUsuario = response.getString("usuNombre")
                txtNombredelUsuario.text = "Hola ¡$nombreUsuario!"
            },
            Response.ErrorListener { error ->
                Toast.makeText(this.context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(request)


        btnCerrar.setOnClickListener {
            // Borra la información del usuario de las preferencias compartidas
            val sharedPreferences = this.requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("idUsuario")
            editor.apply()

            // Redirige al usuario a la actividad de inicio de sesión
            val intent = Intent(this.context, inicio_sesion::class.java)
            startActivity(intent)

            // Cierra la actividad actual
            this.requireActivity().finish()
        }
    }


}