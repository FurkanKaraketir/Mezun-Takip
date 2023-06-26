package com.karaketir.mezuntakip.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karaketir.mezuntakip.R
import com.karaketir.mezuntakip.activities.PersonActivity
import com.karaketir.mezuntakip.databinding.MainButtonRowBinding
import com.karaketir.mezuntakip.models.Person

class MainAdapter(
    private val personList: ArrayList<Int>
) : RecyclerView.Adapter<MainAdapter.PersonHolder>() {
    class PersonHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = MainButtonRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.main_button_row, parent, false)
        return PersonHolder(view)
    }

    override fun getItemCount(): Int {
        return personList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PersonHolder, position: Int) {
        with(holder){
            val item = personList[position]
            val name = binding.yearName
            val card = binding.personGridCard
            name.text = "$item Mezunları"
            card.setOnClickListener {
                val intent = Intent(
                    holder.itemView.context, PersonActivity::class.java
                )
                intent.putExtra(
                    "year",item.toString()
                )

                holder.itemView.context.startActivity(intent)
            }



        }
    }
}