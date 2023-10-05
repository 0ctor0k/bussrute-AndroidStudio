package com.example.bussrute.modelo

class Comentario constructor(id: Int, comDescripcion: String, comValoracion: String, comUsuario: String, comRuta: String ) {
    var id = id
    var comUsuario = comUsuario
    var comValoracion = comValoracion
    var comDescripcion = comDescripcion
    var comRuta = comRuta

    override fun toString(): String {
        return comDescripcion
    }
}