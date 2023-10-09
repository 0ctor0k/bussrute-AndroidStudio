package com.example.bussrute

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bussrute.modelo.Ruta
import androidx.appcompat.app.AppCompatActivity
class AdaptadorFavorito: BaseAdapter {
    var contexto: Context
    var layout: Int = 0
    var listaFavorito: List<Ruta>

    /**
     * Constructor que inicializa el objeto
     * */

    constructor(contexto: Context, layout: Int, listaFavorito: List<Ruta>){
        this.contexto = contexto
        this.layout = layout
        this.listaFavorito=listaFavorito
    }

    /**
     *obtiene el tamaño de la lista del adaptador
     **/
    override fun getCount(): Int {
        return listaFavorito.size
    }
    /**
     * obtiene el item del elemento de acuerdo a la posicion
     * */
    override fun getItem(posicion: Int): Any {
        return listaFavorito[posicion]
    }

    override fun getItemId(posicion: Int): Long {
        return posicion.toLong()
    }
    /**
     * Retorna la vista con los elementos
     * */
    override fun getView(posicion: Int, vista: View?, parent: ViewGroup?): View {
        var v:View
        var inflater: LayoutInflater = LayoutInflater.from(contexto)
        v = inflater.inflate(R.layout.layoutfavorito, null)
        val rutEmpresa: TextView = v.findViewById(R.id.txtEmpresaRuta)
        rutEmpresa.text = "Empresa: "+listaFavorito[posicion].rutEmpresa
        val rutPrecio: TextView = v.findViewById(R.id.txtPrecioRuta)
        rutPrecio.text = "Precio: "+listaFavorito[posicion].rutPrecio
        val rutNumero: TextView = v.findViewById(R.id.txtNumeroRuta)
        rutNumero.text = "Ruta "+listaFavorito[posicion].rutNumero

        if (listaFavorito[posicion].rutEmpresa == "FlotaHuila"){
            val foto: ImageView = v.findViewById(R.id.imgFav)
            val layoutParams = foto.layoutParams
            layoutParams.width = 300 // Ancho fijo en píxeles o dp
            layoutParams.height = 300 // Alto fijo en píxeles o dp
            foto.layoutParams = layoutParams
            foto.setImageResource(R.drawable.flotahuila)
        }else if (listaFavorito[posicion].rutEmpresa == "CootransHuila"){
            val foto: ImageView = v.findViewById(R.id.imgFav)
            val layoutParams = foto.layoutParams
            layoutParams.width = 300 // Ancho fijo en píxeles o dp
            layoutParams.height = 300 // Alto fijo en píxeles o dp
            foto.layoutParams = layoutParams
            foto.setImageResource(R.drawable.cootranshuila)
        }else if (listaFavorito[posicion].rutEmpresa == "Coomotor"){
            val foto: ImageView = v.findViewById(R.id.imgFav)
            val layoutParams = foto.layoutParams
            layoutParams.width = 300 // Ancho fijo en píxeles o dp
            layoutParams.height = 300 // Alto fijo en píxeles o dp
            foto.layoutParams = layoutParams
            foto.setImageResource(R.drawable.coomotor)
        }else if (listaFavorito[posicion].rutEmpresa == "CootransNeiva"){
            val foto: ImageView = v.findViewById(R.id.imgFav)
            val layoutParams = foto.layoutParams
            layoutParams.width = 300 // Ancho fijo en píxeles o dp
            layoutParams.height = 300 // Alto fijo en píxeles o dp
            foto.layoutParams = layoutParams
            foto.setImageResource(R.drawable.cootransneiva)
        }else if (listaFavorito[posicion].rutEmpresa == "AutoBuses"){
            val foto: ImageView = v.findViewById(R.id.imgFav)
            val layoutParams = foto.layoutParams
            layoutParams.width = 300 // Ancho fijo en píxeles o dp
            layoutParams.height = 300 // Alto fijo en píxeles o dp
            foto.layoutParams = layoutParams
            foto.setImageResource(R.drawable.autobuses)
        }


        val item = getItem(posicion)
        val rutaId = listaFavorito[posicion].id
        {}    // Configurar otros elementos de la vista aquí

        // Agregar un botón para eliminar y configurar un clic en él
        val eliminarButton = v.findViewById<Button>(R.id.btnEliminarRuta)
        val vizualizarRuta = v.findViewById<Button>(R.id.btnVizualizarRuta)

        vizualizarRuta.setOnClickListener {
            // Crear una instancia del fragmento y pasar el dato como argumento
            val fragment = pantallaPrincipalFragment()

            val bundle = Bundle()
            bundle.putString("ruta", "$item") // Cambia "Valor que deseas pasar" al dato que deseas llevar contigo
            fragment.arguments = bundle

            // Realizar la transacción del fragmento
            val fragmentManager = (contexto as AppCompatActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.pantallaFavorito, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        eliminarButton.setOnClickListener {
            val sharedPreferences = contexto.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val idUsuario = sharedPreferences.getString("idUsuario", "")
            val url = "https://bussrute.pythonanywhere.com/favoritoAndroid/$idUsuario/$rutaId"
            val queue = Volley.newRequestQueue(contexto)
            val resultadoPost = object : StringRequest(
                Method.DELETE,url,
                Response.Listener { response ->
                    Toast.makeText(contexto, "Favorito Eliminado", Toast.LENGTH_SHORT).show()
                    // Copia listaFavorito a una MutableList
                    val listaFavoritoMutable = listaFavorito.toMutableList()

                    // Elimina la ruta de la lista cuando se hace clic en el botón
                    listaFavoritoMutable.remove(item)
                    // Asigna la lista modificada de vuelta a listaFavorito
                    listaFavorito = listaFavoritoMutable

                    notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(contexto, "$error", Toast.LENGTH_SHORT).show()
                }
            ){
            }
            queue.add(resultadoPost)

        }
        return v;

    }
}