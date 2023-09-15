package com.example.bussrute.modelo

class Favorito constructor(id:Int, favRuta: String,favUsuario: String) {
    var favRuta = favRuta
    var favUsuario = favUsuario
    var id = id
    override fun toString(): String {
        return favRuta
    }
}