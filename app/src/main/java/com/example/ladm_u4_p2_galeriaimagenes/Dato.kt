package com.example.ladm_u4_p2_galeriaimagenes

class Dato {
    var propietario = ""
    var idEvento = ""
    var titulo = ""
    var visibilidad = ""
    var estado = ""

    // toString
    override fun toString(): String {
        return "ID Evento: $idEvento\n" +
                "Nombre: $titulo\n" +
                "Visibilidad: $visibilidad\n" +
                "Estado: $estado\n"
    }
}