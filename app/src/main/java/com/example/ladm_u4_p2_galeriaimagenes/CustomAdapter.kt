package mx.edu.ittepic.daar.ladm_u4_p2_albumfotos_berb_daar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ladm_u4_p2_galeriaimagenes.Imagen
import com.example.ladm_u4_p2_galeriaimagenes.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CustomAdapter (private val data:ArrayList<Imagen>) : RecyclerView.Adapter<CustomAdapter.DatoViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DatoViewHolder {
        val image = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view, viewGroup, false)
        return DatoViewHolder(image)
    }

    override fun onBindViewHolder(holder : DatoViewHolder, i: Int) {
        val item = data[i]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class DatoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImagen = itemView.findViewById<ImageView>(R.id.foto)

        fun render(imagen: Imagen) {
            itemImagen.setImageBitmap(imagen.imagen)
        }
    }
}