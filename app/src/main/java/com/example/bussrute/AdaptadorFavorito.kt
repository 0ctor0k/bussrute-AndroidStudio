package com.example.bussrute

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.bussrute.modelo.Ruta

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
        rutEmpresa.text = listaFavorito[posicion].rutEmpresa
        val rutPrecio: TextView = v.findViewById(R.id.txtPrecioRuta)
        rutPrecio.text = listaFavorito[posicion].rutPrecio
        val rutNumero: TextView = v.findViewById(R.id.txtNumeroRuta)
        rutNumero.text = listaFavorito[posicion].rutNumero

        return v;

    }

}