package com.karaketir.mezuntakip.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karaketir.mezuntakip.R
import com.karaketir.mezuntakip.databinding.PersonRowBinding
import com.karaketir.mezuntakip.models.Person


class PersonAdapter(
    private val personList: ArrayList<Person>
) : RecyclerView.Adapter<PersonAdapter.PersonHolder>() {
    class PersonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = PersonRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.person_row, parent, false)
        return PersonHolder(view)
    }

    override fun getItemCount(): Int {
        return personList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PersonHolder, position: Int) {
        with(holder) {
            val item = personList[position]
            val name = binding.nameText
            var a = false
            val year = binding.yearText
            val city = binding.cityText
            val number = binding.phoneNumberText
            val email = binding.emailText
            val school = binding.schoolText
            val field = binding.fieldText
            val stateOfGraduation = binding.stateOfGraduationText
            val description = binding.descriptionText

            val downButton = binding.downButtton

            name.text = item.name
            year.text = "Lise Mezuniyet Yılı: " + item.year.toString()
            city.text = "Bulunduğu İl: " + item.city
            number.text = item.number.toString()
            email.text = item.email
            school.text = "Üniversite: " + item.school
            field.text = "Bölüm: " + item.field
            if (item.graduation) {
                stateOfGraduation.text = "Üniversite Mezuniyet Durumu: Mezun"
            } else {
                stateOfGraduation.text = "Üniversite Mezuniyet Durumu: Mezun Değil"
            }

            number.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:+90${item.number}")
                holder.itemView.context.startActivity(callIntent)
            }

            description.text = item.description

            downButton.setOnClickListener {
                a = !a

                if (a) {
                    year.visibility = View.VISIBLE
                    city.visibility = View.VISIBLE
                    number.visibility = View.VISIBLE
                    email.visibility = View.VISIBLE
                    school.visibility = View.VISIBLE
                    field.visibility = View.VISIBLE
                    stateOfGraduation.visibility = View.VISIBLE
                    description.visibility = View.VISIBLE

                } else {
                    year.visibility = View.GONE
                    city.visibility = View.GONE
                    number.visibility = View.GONE
                    email.visibility = View.GONE
                    school.visibility = View.GONE
                    field.visibility = View.GONE
                    stateOfGraduation.visibility = View.GONE
                    description.visibility = View.GONE
                }

            }

        }
    }
}