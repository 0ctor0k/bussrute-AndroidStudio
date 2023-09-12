package com.example.bussrute.modelo

class Rol constructor(id:Int, rolNombre: String){
    var id = id
    var rolNombre = rolNombre

    override fun toString(): String {
        return rolNombre
    }
}