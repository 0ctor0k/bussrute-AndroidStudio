package com.example.bussrute.modelo

class Comentario constructor(id: Int, comDescripcion: String, comValoracion: String, comUsuario: String ) {
    var id = id
    var comUsuario = comUsuario
    var comValoracion = comValoracion
    var comDescripcion = comDescripcion

    override fun toString(): String {
        return comDescripcion
    }
}