package com.example.bussrute.modelo

class DetalleRuta constructor(val id: Int, val detRuta: Int, val detLatitud: Double, val detLongitud: Double) {
    override fun toString(): String {
        return "$detLatitud, $detLongitud"
    }
}