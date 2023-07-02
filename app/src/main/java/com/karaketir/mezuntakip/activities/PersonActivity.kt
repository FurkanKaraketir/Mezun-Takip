package com.karaketir.mezuntakip.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.mezuntakip.adapters.PersonAdapter
import com.karaketir.mezuntakip.databinding.ActivityPersonBinding
import com.karaketir.mezuntakip.models.Person
import com.karaketir.mezuntakip.services.addData
import com.karaketir.mezuntakip.services.createExcel
import com.karaketir.mezuntakip.services.createSheetHeader
import com.karaketir.mezuntakip.services.getHeaderStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class PersonActivity : AppCompatActivity() {

    init {
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
    }


    private lateinit var binding: ActivityPersonBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: PersonAdapter
    private var personList = ArrayList<Person>()
    private val workbook = XSSFWorkbook()
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
        val excelButton = binding.excelButton

        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = PersonAdapter(personList)
        recyclerView.adapter = recyclerViewAdapter

        val year = intent.getStringExtra("year")

        if (year != null) {
            db.collection("People").whereEqualTo("year", year.toInt())
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        println(error.localizedMessage)
                    }
                    personList.clear()
                    if (value != null) {
                        for (i in value) {

                            val newID = i.id
                            val newName = i.get("name").toString()
                            val newYear = i.get("year").toString().toInt()
                            val newCity = i.get("city").toString()
                            val newPhone = i.get("number").toString().toLong()
                            val newEmail = i.get("email").toString()
                            val newSchool = i.get("school").toString()
                            val newField = i.get("field").toString()
                            val newGraduate = i.get("graduation") as Boolean
                            val newDescription = i.get("description").toString()
                            val newPhoto = i.get("photoURL").toString()
                            val newPerson = Person(
                                newID,
                                newName,
                                newYear,
                                newCity,
                                newPhone,
                                newEmail,
                                newSchool,
                                newField,
                                newGraduate,
                                newDescription,
                                newPhoto
                            )

                            personList.add(newPerson)
                            recyclerViewAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }

        val sheet: Sheet = workbook.createSheet("Sayfa 1")

        val cellStyle = getHeaderStyle(workbook)

        createSheetHeader(cellStyle, sheet)


        excelButton.setOnClickListener {
            addData(
                sheet, this, workbook, personList
            )

            askForPermissions()
        }


    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                createExcel(this, workbook)
            }
        }

    private fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            createExcel(this, workbook)
        }
    }
}