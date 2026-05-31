// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/adapters/CarreraAdapter.kt
package com.tuuniversidad.corte2universidades.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuuniversidad.corte2universidades.databinding.ItemCarreraBinding
import com.tuuniversidad.corte2universidades.models.Carrera

class CarreraAdapter(
    private var lista: MutableList<Carrera>,
    private val onItemClick: (Carrera) -> Unit,
    private val onDeleteClick: (Carrera) -> Unit
) : RecyclerView.Adapter<CarreraAdapter.CarreraViewHolder>() {

    inner class CarreraViewHolder(private val binding: ItemCarreraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(carrera: Carrera) {
            binding.tvNombre.text = carrera.nombre
            binding.tvNivel.text = carrera.nivelFormacion
            binding.tvUniversidad.text = carrera.universidadNombre.ifEmpty { "ID: ${carrera.universidadId}" }
            binding.tvCreditos.text = "Créditos: ${carrera.numCreditos} | Semestres: ${carrera.numSemestres}"

            binding.root.setOnClickListener { onItemClick(carrera) }
            binding.btnDelete.setOnClickListener { onDeleteClick(carrera) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarreraViewHolder {
        val binding = ItemCarreraBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarreraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarreraViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Carrera>) {
        lista.clear()
        lista.addAll(newList)
        notifyDataSetChanged()
    }
}
