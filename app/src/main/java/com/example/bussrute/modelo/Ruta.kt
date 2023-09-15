package com.example.bussrute.modelo

class Ruta constructor(id:Int, rutNumero: String,rutPrecio: String,rutEmpresa: String) {
    var rutNumero = rutNumero
    var rutPrecio = rutPrecio
    var rutEmpresa = rutEmpresa
    var id = id
    override fun toString(): String {
        return rutNumero
    }
}