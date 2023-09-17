package com.example.bussrute

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bussrute.modelo.Comentario
import com.example.bussrute.modelo.Ruta
import org.json.JSONException


class favoritosFragment  : Fragment() {

    private lateinit var listarRuta: MutableList<Ruta>
    private lateinit var listaViewFavorito: ListView
    private lateinit var contenido: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_favoritos, container, false)
        contenido = requireContext()
        listarRuta = mutableListOf()
        listaViewFavorito = rootView.findViewById(R.id.listaFavoritos)
        obtenerFavoritos()
        return rootView
    }
    /**
     * Funcion que realiza una peticion a la api para obtener todos los comentarios
     * */
    private fun obtenerFavoritos() {
        val sharedPreferences = this.requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", "")
        val url = "https://bussrute.pythonanywhere.com/favorito/$idUsuario"
        val queve = Volley.newRequestQueue(requireContext()) // Cambio a requireContext()
        val jsonFavorito = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val favRuta = jsonObject.getInt("favRuta")
                        val favUsuario = jsonObject.getString("favUsuario")
                        Rutasytal(favRuta)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            })
        queve.add(jsonFavorito)
    }


    private fun Rutasytal(idRuta:Int) {
        val url = "https://bussrute.pythonanywhere.com/ruta"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonRuta = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        if (id==idRuta){
                            val rutNumero = jsonObject.getInt("rutNumero")
                            val rutPrecio = jsonObject.getString("rutPrecio")
                            val rutEmpresa = jsonObject.getString("rutEmpresa")
                            val rutas = Ruta(id, rutNumero.toString(),rutPrecio,rutEmpresa)
                            listarRuta.add(rutas)
                        }
                    }
                    val adaptadorFav = AdaptadorFavorito(
                        requireContext(),
                        R.layout.layoutfavorito,
                        listarRuta
                    )
                    listaViewFavorito.adapter = adaptadorFav

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
                Toast.makeText(contenido, error.message, Toast.LENGTH_LONG).show()
            })
        queue.add(jsonRuta)
    }
}

