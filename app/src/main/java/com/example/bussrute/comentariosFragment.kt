package com.example.bussrute

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
import org.json.JSONException


class comentariosFragment : Fragment() {

    private lateinit var listarComentario: MutableList<Comentario>
    private lateinit var listaViewComentario: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_comentarios, container, false)
        listarComentario = mutableListOf()
        listaViewComentario = rootView.findViewById(R.id.listaComentarios)
        obtenerComentarios()
        return rootView
    }
        /**
         * Funcion que realiza una peticion a la api para obtener todos los comentarios
         * */
        private fun obtenerComentarios() {
            val url = "https://bussrute.pythonanywhere.com/comentario"
            val queve = Volley.newRequestQueue(requireContext()) // Cambio a requireContext()
            val jsonComentario = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    try {
                        for (i in 0 until response.length()) {
                            val jsonObject = response.getJSONObject(i)
                            val id = jsonObject.getInt("id")
                            val comDescripcion = jsonObject.getString("comDescripcion")
                            val comValoracion = jsonObject.getString("comValoracion")
                            val comUsuarioId = jsonObject.getString("comUsuario")

                            // Obtener el nombre de usuario utilizando su ID
                            obtenerNombreUsuario(comUsuarioId, id, comDescripcion, comValoracion)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                })
            queve.add(jsonComentario)
        }

        private fun obtenerNombreUsuario(usuarioId: String, id: Int, comDescripcion: String, comValoracion: String) {
            val urlUsuario = "https://bussrute.pythonanywhere.com/usuario/$usuarioId"
            val queveUsuario = Volley.newRequestQueue(requireContext()) // Cambio a requireContext()
            val jsonUsuario = JsonObjectRequest(
                Request.Method.GET, urlUsuario, null,
                { response ->
                    try {
                        val nombreUsuario = response.getString("usuNombre")
                        val comentario = Comentario(id, comDescripcion, comValoracion, nombreUsuario)
                        listarComentario.add(comentario)

                        val adaptador = Adaptador(requireContext(), R.layout.layoutcomentario, listarComentario)
                        listaViewComentario.adapter = adaptador
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                })
            queveUsuario.add(jsonUsuario)
        }
}

