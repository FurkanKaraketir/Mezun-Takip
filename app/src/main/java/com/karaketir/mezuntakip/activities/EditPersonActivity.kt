package com.karaketir.mezuntakip.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.mezuntakip.databinding.ActivityEditPersonBinding

class EditPersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPersonBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore


        var name = ""
        var year = 0
        var city = ""
        var number: Long
        var email = ""
        var school = ""
        var field = ""
        var stateOfGraduation: Boolean
        var description = ""
        val yearList = ArrayList<Int>()

        db.collection("Years").addSnapshotListener { value, _ ->
            if (value != null) {
                for (i in value) {
                    yearList.add(i.id.toInt())
                }
            }
        }


        val personID = intent.getStringExtra("personID")

        val saveButton = binding.saveButton
        val nameEditText = binding.nameEditText
        val yearEditText = binding.graduationYearEditText
        val cityEditCity = binding.cityEditText
        val numberEditText = binding.phoneNumberEditText
        val emailEditText = binding.emailEditText
        val schoolEditText = binding.schoolEditText
        val fieldEditText = binding.fieldEditText
        val switch = binding.stateOfGraduation
        val descriptionEditText = binding.descriptionEditText

        db.collection("People").document(personID!!).get().addOnSuccessListener {
            nameEditText.setText(it.get("name").toString())
            yearEditText.setText(it.get("year").toString())
            cityEditCity.setText(it.get("city").toString())
            numberEditText.setText(it.get("number").toString())
            emailEditText.setText(it.get("email").toString())
            schoolEditText.setText(it.get("school").toString())
            fieldEditText.setText(it.get("field").toString())
            switch.isChecked = it.get("graduation") as Boolean
            descriptionEditText.setText(it.get("description").toString())

        }

        saveButton.setOnClickListener {

            if (nameEditText.text != null) {
                name = nameEditText.text.toString()
            }

            if (yearEditText.text != null) {
                year = if (yearEditText.text.toString() == "") {
                    0
                } else {
                    yearEditText.text.toString().toInt()

                }
            }

            if (cityEditCity.text != null) {
                city = cityEditCity.text.toString()
            }

            number = if (numberEditText.text.toString() == "") {
                0
            } else {
                numberEditText.text.toString().toLong()

            }
            if (emailEditText.text != null) {
                email = emailEditText.text.toString()
            }

            if (schoolEditText.text != null) {
                school = schoolEditText.text.toString()
            }

            if (fieldEditText.text != null) {
                field = fieldEditText.text.toString()
            }

            if (descriptionEditText.text != null) {
                description = descriptionEditText.text.toString()
            }
            stateOfGraduation = switch.isChecked

            val data = hashMapOf(
                "name" to name,
                "year" to year,
                "city" to city,
                "number" to number,
                "email" to email,
                "school" to school,
                "field" to field,
                "graduation" to stateOfGraduation,
                "description" to description
            )

            db.collection("People").document(personID).update(data as Map<String, Any>)
                .addOnSuccessListener {

                    if (year !in yearList) {
                        db.collection("Years").document(year.toString())
                            .set(hashMapOf("year" to year)).addOnSuccessListener {

                                Toast.makeText(this, "İşlem Başarılı", Toast.LENGTH_SHORT).show()
                                finish()


                            }
                    } else {
                        Toast.makeText(this, "İşlem Başarılı", Toast.LENGTH_SHORT).show()
                        finish()

                    }


                }


        }


    }
}