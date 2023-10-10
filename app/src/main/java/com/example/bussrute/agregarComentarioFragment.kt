package com.example.bussrute

import SharedViewModel
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bussrute.modelo.Ruta
import org.json.JSONException

class agregarComentarioFragment : Fragment() {

    private lateinit var usuarioCom: EditText
    private lateinit var agregarCom: Button
    private lateinit var descripCom: EditText
    private lateinit var valoracionCom: RatingBar
    private lateinit var rutaCom: EditText
    private lateinit var idusu: EditText
    private lateinit var idRutaCom: EditText
    val model: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agregar_comentario, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtén una referencia a tu EditText
        usuarioCom = view.findViewById(R.id.usuarioCom)
        agregarCom = view.findViewById(R.id.agregarCom)
        descripCom = view.findViewById(R.id.descripCom)
        valoracionCom = view.findViewById(R.id.valoracionCom)
        rutaCom = view.findViewById(R.id.rutaCom)
        idusu = view.findViewById(R.id.idusu)
        idRutaCom = view.findViewById(R.id.idRutaCom)


        model.selectedId.observe(viewLifecycleOwner, Observer { id ->

                idRutaCom.setText(id)
                obtenerRuta(id)


        })

        agregarCom.setOnClickListener { agregar() }

        // Obtén el nombre del usuario desde el servidor
        val sharedPreferences = this.requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", "")

        val url = "https://bussrute.pythonanywhere.com/usuario/$idUsuario"

        val requestQueue = Volley.newRequestQueue(this.requireActivity())

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response ->
                val nombreUsuario = response.getString("usuNombre")
                // Establece el texto del EditText al nombre del usuario
                usuarioCom.text = Editable.Factory.getInstance().newEditable(nombreUsuario)
                if (this::usuarioCom.isInitialized) {
                    usuarioCom.text = Editable.Factory.getInstance().newEditable(nombreUsuario)
                }
                // Encuentra el EditText por su ID y establece el texto al ID del usuario
                val idusuEditText = requireActivity().findViewById<EditText>(R.id.idusu)
                idusuEditText.text = Editable.Factory.getInstance().newEditable(idUsuario)
                idusuEditText.visibility = View.GONE

                requireActivity().invalidateOptionsMenu() // Esto hará que se llame a onPrepareOptionsMenu()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this.requireActivity(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        // Añade la solicitud a la cola de solicitudes
        requestQueue.add(request)



    }
    fun obtenerRuta(id: String) {
        val url = "https://bussrute.pythonanywhere.com/rutaAndroid/$id"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val rutNumero = response.getString("rutNumero")

                    // Asegúrate de tener una referencia a tu EditText rutaCom
                    val rutaCom: EditText = requireView().findViewById(R.id.rutaCom)
                    rutaCom.setText("Ruta: $rutNumero")


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            })
        queue.add(jsonObjectRequest)
    }
    private fun agregar() {
        val url = "https://bussrute.pythonanywhere.com/comentario"
        val queve = Volley.newRequestQueue(requireActivity())
        val progresBar = ProgressDialog.show(requireActivity(), "Enviando Datos...", "espere por favor")
        val resultadoPost = object : StringRequest(
            com.android.volley.Request.Method.POST,url,
            Response.Listener<String> {response->
                progresBar.dismiss()
                Toast.makeText(requireActivity(), "Comentario agregado exitosamente",Toast.LENGTH_LONG).show()
                clean()
            }, Response.ErrorListener { error ->
                progresBar.dismiss()
                Toast.makeText(requireActivity(), "Error ${error.message}", Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String?> {
                val parametros = HashMap<String,String?>()
                parametros.put("comDescripcion", descripCom.text.toString())
                parametros.put("comValoracion", valoracionCom.rating.toString())
                parametros.put("comUsuario", idusu.text.toString())
                parametros.put("comRuta", idRutaCom.text?.toString())

                return parametros
            }
        }
        queve.add(resultadoPost)
    }

    fun clean(){

        idRutaCom.text.clear()
        rutaCom.text.clear()
        descripCom.text.clear()
        valoracionCom.rating = 0f
    }


}