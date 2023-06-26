package com.karaketir.mezuntakip.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.mezuntakip.R
import com.karaketir.mezuntakip.adapters.MainAdapter
import com.karaketir.mezuntakip.adapters.PersonAdapter
import com.karaketir.mezuntakip.databinding.ActivityPersonBinding
import com.karaketir.mezuntakip.databinding.PersonRowBinding
import com.karaketir.mezuntakip.models.Person

class PersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: PersonAdapter
    private var personList = ArrayList<Person>()

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        recyclerView = binding.personRecycler

        val layoutManager = LinearLayoutManager(applicationContext)

        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = PersonAdapter(personList)
        recyclerView.adapter = recyclerViewAdapter

        val year = intent.getStringExtra("year")

        if (year != null) {
            db.collection("People").whereEqualTo("year", year.toInt())
                .addSnapshotListener { value, error ->
                    if (error!=null){
                        println(error.localizedMessage)
                    }
                    personList.clear()
                    if (value != null) {
                        for (i in value) {

                            val newName = i.get("name").toString()
                            val newYear = i.get("year").toString().toInt()
                            val newCity = i.get("city").toString()
                            val newPhone = i.get("number").toString().toLong()
                            val newEmail = i.get("email").toString()
                            val newSchool = i.get("school").toString()
                            val newField = i.get("field").toString()
                            val newGraduate = i.get("graduation") as Boolean
                            val newDescription = i.get("description").toString()

                            val newPerson = Person(
                                newName,
                                newYear,
                                newCity,
                                newPhone,
                                newEmail,
                                newSchool,
                                newField,
                                newGraduate,
                                newDescription
                            )

                            personList.add(newPerson)
                            recyclerViewAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }

    }
}