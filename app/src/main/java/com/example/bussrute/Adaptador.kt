package com.example.bussrute

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.bussrute.modelo.Comentario

class Adaptador: BaseAdapter {
    var contexto: Context
    var layout: Int = 0
    var listaComentario: List<Comentario>

    /**
     * Constructor que inicializa el objeto
     * */

    constructor(contexto: Context, layout: Int, listarComentarios: List<Comentario>){
        this.contexto = contexto
        this.layout = layout
        this.listaComentario=listarComentarios
    }

    /**
     *obtiene el tamaño de la lista del adaptador
     **/
    override fun getCount(): Int {
        return listaComentario.size
    }
    /**
     * obtiene el item del elemento de acuerdo a la posicion
     * */
    override fun getItem(posicion: Int): Any {
        return listaComentario[posicion]
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
        v = inflater.inflate(R.layout.layoutcomentario, null)
        val comDescripcion: TextView = v.findViewById(R.id.comDescripcion)
        comDescripcion.text = listaComentario[posicion].comDescripcion
        val comValoracion: TextView = v.findViewById(R.id.comValoracion)
        comValoracion.text = listaComentario[posicion].comValoracion.toString()
        val comUsuario: TextView = v.findViewById(R.id.comUsuario)
        comUsuario.text = listaComentario[posicion].comUsuario.toString()
        val foto: ImageView = v.findViewById(R.id.imgFoto)
        foto.setImageResource(R.drawable.user)

        return v;

    }

}