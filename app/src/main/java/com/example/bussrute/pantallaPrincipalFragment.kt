package com.example.bussrute

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.elmapa2.modelo.DetalleRuta
import com.example.elmapa2.modelo.Ruta
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [pantallaPrincipalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class pantallaPrincipalFragment : Fragment(R.layout.fragment_pantalla_principal) {
    private lateinit var webView: WebView
    lateinit var contenido: Context
    lateinit var txtNombreRuta: EditText
    lateinit var txtColorRuta: EditText
    lateinit var txtHorarioRuta: EditText
    lateinit var txtEmpresaRuta: EditText
    lateinit var cbRuta: Spinner
    lateinit var btnConsultar: Button
    lateinit var listaRutas: MutableList<Ruta>
    private val coordenadas = mutableListOf<DetalleRuta>()
    private var idRuta: Int = 0
    private var numRuta: Int = 0
    private var urlBase = "https://bussrute.pythonanywhere.com/"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contenido = requireContext()
        txtNombreRuta = requireView().findViewById(R.id.txtNombreRuta)
        txtNombreRuta.isEnabled = false
        txtEmpresaRuta = requireView().findViewById(R.id.txtEmpresa)
        txtEmpresaRuta.isEnabled = false
        txtHorarioRuta = requireView().findViewById(R.id.txtHorario)
        txtHorarioRuta.isEnabled = false
        txtColorRuta = requireView().findViewById(R.id.txtColor)
        txtColorRuta.isEnabled = false

        cbRuta = requireView().findViewById(R.id.cbRutas)

        obtenerRutas()



        webView = requireView().findViewById(R.id.webView)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Aquí, después de que el contenido del WebView se haya cargado completamente, inyectaremos una función de JavaScript que captura los mensajes de console.log y los envía a Android
                val javascript = """
            window.onerror = function(message, source, lineno, colno, error) {
                Android.onConsoleLog(message);
            };
        """.trimIndent()
                webView.evaluateJavascript(javascript, null)
            }
        }

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // Habilita JavaScript en el WebView
        webView.webChromeClient = WebChromeClient() // Permite mostrar alertas y diálogos web

        // Carga el contenido de Leaflet desde el archivo HTML local
        val htmlContent = contenido.assets.open("map.html")?.bufferedReader()?.use { it.readText() }
        webView.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "UTF-8", null)



        cbRuta = requireView().findViewById(R.id.cbRutas)
        btnConsultar = requireView().findViewById(R.id.btnConsultar)
        listaRutas = mutableListOf<Ruta>()





        cbRuta.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                idRuta = listaRutas[posicion].id
                numRuta = listaRutas[posicion].rutNumero.toInt()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        btnConsultar.setOnClickListener {
            consultar() }

    }

    // Agregar la interfaz JavaScriptInterface fuera de la clase MainActivity
    class JavaScriptInterface {
        @JavascriptInterface
        fun onRouteUpdated(route: String) {
            // Aquí recibirás la información actualizada desde JavaScript
            // Puedes procesar la información o actualizar la interfaz de usuario en Kotlin
            // Por ejemplo, si route contiene un JSON con los waypoints, puedes parsearlo y usarlo en Kotlin
            // Aquí, simplemente mostramos un mensaje de depuración
            Log.d("JavaScriptInterface", "Ruta actualizada: $route")


        }

        // Función en Kotlin para procesar los waypoints actualizados
        private fun procesarWaypoints(route: String) {
            // Aquí puedes parsear la información recibida en 'route' y realizar las acciones necesarias en Kotlin
            // Por ejemplo, actualiza la interfaz de usuario con los waypoints recibidos
        }
    }


    private fun consultar(){
        recargarWebView()
        val numeroRuta = cbRuta.selectedItem
        val url = urlBase + "ruta/$numeroRuta"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                obtenerDetalleRutas(response.getString("id"))
                txtEmpresaRuta.setText(response.getString("rutEmpresa"))
                txtNombreRuta.setText(response.getString("rutNumero"))
                txtHorarioRuta.setText(response.getString("rutPrecio"))
                if (response.getString("rutEmpresa") == "Coomotor"){
                    txtColorRuta.setText("Azul")
                }
                if (response.getString("rutEmpresa") == "Cootranshuila"){
                    txtColorRuta.setText("Verde Claro con Blanco")
                }
                if (response.getString("rutEmpresa") == "Flotahuila"){
                    txtColorRuta.setText("Gris / Plateado")
                }
                if (response.getString("rutEmpresa") == "Cootransneiva"){
                    txtColorRuta.setText("Blanco con Rojo")
                }
                if (response.getString("rutEmpresa") == "AutoBuses S.A.S"){
                    txtColorRuta.setText("Verde Oscuro")
                }

            }, Response.ErrorListener { error ->
                Toast.makeText(contenido, error.toString(), Toast.LENGTH_SHORT).show()
                Log.e("Error: ", error.toString())
            })
        queue.add(jsonObjectRequest)
    }

    private fun obtenerRutas() {
        val url = urlBase+"ruta"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonCategoria = JsonArrayRequest(
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
                        listaRutas.add(rutas)
                    }
                    val adaptador = ArrayAdapter(
                        contenido,
                        android.R.layout.simple_spinner_item,
                        listaRutas
                    )
                    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    cbRuta.adapter = adaptador
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
                Toast.makeText(contenido, error.toString(), Toast.LENGTH_SHORT).show()
            })
        queue.add(jsonCategoria)
    }

    // Cargar el contenido del WebView
    private fun loadWebViewContent() {
        val htmlContent = contenido.assets.open("map.html")?.bufferedReader()?.use { it.readText() }
        webView.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "UTF-8", null)
    }

    // Función personalizada para recargar el WebView
    private fun recargarWebView() {
        // Puedes volver a cargar el contenido del WebView de la misma manera que lo hiciste inicialmente
        loadWebViewContent()
    }

    private fun obtenerDetalleRutas(numeroRuta:String) {
        val url = urlBase+"detalleRuta/"+numeroRuta
        val queue = Volley.newRequestQueue(requireContext())
        val jsonDetalleRuta = JsonArrayRequest(com.android.volley.Request.Method.GET, url, null,
            Response.Listener<JSONArray> { response ->
                try {
                    val jsonArray = response
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val detRuta = jsonObject.getInt("detRuta")
                        val detLatitud = jsonObject.getDouble("detLatitud")
                        val detLongitud = jsonObject.getDouble("detLongitud")
                        val jsFunctionCall = "javascript:trazarRuta($detLatitud,$detLongitud)"
                        webView.loadUrl(jsFunctionCall)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            })
        queue.add(jsonDetalleRuta)
    }



}