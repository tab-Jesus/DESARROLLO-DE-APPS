// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/adapters/UniversidadAdapter.kt
package com.tuuniversidad.corte2universidades.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuuniversidad.corte2universidades.databinding.ItemUniversidadBinding
import com.tuuniversidad.corte2universidades.models.Universidad

class UniversidadAdapter(
    private var lista: MutableList<Universidad>,
    private val onItemClick: (Universidad) -> Unit,
    private val onDeleteClick: (Universidad) -> Unit
) : RecyclerView.Adapter<UniversidadAdapter.UniversidadViewHolder>() {

    inner class UniversidadViewHolder(private val binding: ItemUniversidadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(universidad: Universidad) {
            binding.tvNombre.text = universidad.nombre
            binding.tvCiudad.text = "${universidad.ciudad}, ${universidad.pais}"
            binding.tvCategoria.text = universidad.categoria
            binding.tvCarreras.text = "Carreras: ${universidad.numeroCarreras}"

            binding.root.setOnClickListener { onItemClick(universidad) }
            binding.btnDelete.setOnClickListener { onDeleteClick(universidad) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversidadViewHolder {
        val binding = ItemUniversidadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UniversidadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UniversidadViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Universidad>) {
        lista.clear()
        lista.addAll(newList)
        notifyDataSetChanged()
    }

    fun getList() = lista
}
