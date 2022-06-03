package com.example.ladm_u4_p2_galeriaimagenes.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import com.example.ladm_u4_p2_galeriaimagenes.Dato

class DatabaseHelper {
    private val bdEventos = FirebaseFirestore.getInstance().collection(COLLECTION_EVENTOS)

    suspend fun obtenerEventosPorPropetario(propetario : String): ArrayList<Dato> {
        val returnList = ArrayList<Dato>()
        val query = bdEventos.whereEqualTo(CAMPO_PROPIETARIO, propetario).get().await()

        val documents = query.documents

        if (documents.isNotEmpty()) {
            for (document in documents) {
                val evento = document.toObject<Dato>()
                returnList.add(evento!!)
            }
        }

        return returnList
    }

    companion object DbConstants {

        const val COLLECTION_EVENTOS = "Eventos"
        const val CAMPO_PROPIETARIO = "propietario"
    }
}