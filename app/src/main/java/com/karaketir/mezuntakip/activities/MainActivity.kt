package com.karaketir.mezuntakip.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.mezuntakip.adapters.MainAdapter
import com.karaketir.mezuntakip.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: MainAdapter

    private var yearList = ArrayList<Int>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        recyclerView = binding.mainRecycler

        val layoutManager = GridLayoutManager(applicationContext, 2)

        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = MainAdapter(yearList)
        recyclerView.adapter = recyclerViewAdapter

        binding.addPersonButton.setOnClickListener {
            val intent = Intent(this, AddPersonActivity::class.java)
            this.startActivity(intent)
        }

        binding.searchPersonButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            this.startActivity(intent)
        }


        db.collection("Years").addSnapshotListener { value, _ ->
            yearList.clear()
            if (value != null) {
                for (i in value) {
                    yearList.add(i.id.toInt())
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }


    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        }
    }

}
