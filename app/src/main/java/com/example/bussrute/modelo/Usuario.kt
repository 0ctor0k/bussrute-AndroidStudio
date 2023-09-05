package com.example.bussrute.modelo

class Usuario constructor(id:Int, usuNombre: String, usuCorreo: String, usuPassword: String, usuCreadoGoogle: Boolean, usuTokenCambioContrasena: String, usuRol: Int) {
    var id = id
    var usuNombre = usuNombre
    var usuCorreo = usuCorreo
    var usuPassword = usuPassword
    var usuCreadoGoogle = usuCreadoGoogle
    var usuTokenCambioContrasena = usuTokenCambioContrasena
    var usuRol = usuRol

    override fun toString(): String {
        return usuNombre
    }

}