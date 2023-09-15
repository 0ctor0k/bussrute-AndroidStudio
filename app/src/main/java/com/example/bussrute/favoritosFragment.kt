package com.example.bussrute

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.bussrute.modelo.Comentario
import com.example.bussrute.modelo.Ruta
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [favoritosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class favoritosFragment : Fragment(R.layout.fragment_favoritos) {
    private var urlBase = "https://bussrute.pythonanywhere.com/"
    private lateinit var listarRuta: MutableList<Ruta>
    private lateinit var listaViewFavorito: ListView
    // TODO: Rename and change types of parameters
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarRuta = mutableListOf()
        listaViewFavorito = requireView().findViewById(R.id.listaFavoritos)
        obtenerFavoritos()
    }
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
                        Toast.makeText(requireContext(),favRuta.toString(), Toast.LENGTH_SHORT).show()
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
    private fun Rutasytal(IdRuta:Int) {
        val url = urlBase+"rutaAndroid/"+IdRuta

        val queue = Volley.newRequestQueue(requireContext())
        val jsonRuta = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val rutNumero = jsonObject.getString("rutNumero")
                        val rutPrecio = jsonObject.getString("rutPrecio")
                        val rutEmpresa = jsonObject.getString("rutEmpresa")
                        val rutas = Ruta(id.toInt(), rutNumero,rutPrecio,rutEmpresa)
                        listarRuta.add(rutas)
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
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            })
        queue.add(jsonRuta)
    }
}