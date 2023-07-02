package com.karaketir.mezuntakip.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.karaketir.mezuntakip.R
import com.karaketir.mezuntakip.adapters.PersonAdapter
import com.karaketir.mezuntakip.databinding.ActivitySearchBinding
import com.karaketir.mezuntakip.models.Person
import com.karaketir.mezuntakip.services.addData
import com.karaketir.mezuntakip.services.createExcel
import com.karaketir.mezuntakip.services.createSheetHeader
import com.karaketir.mezuntakip.services.getHeaderStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.Locale


class SearchActivity : AppCompatActivity() {


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


    private lateinit var binding: ActivitySearchBinding
    private var selection = 1
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: PersonAdapter
    private val workbook = XSSFWorkbook()

    private var filteredList = ArrayList<Person>()
    private var personList = ArrayList<Person>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        recyclerView = binding.searchRecyclerView
        val excelButton = binding.excelButton

        setupRecyclerView(personList)
        val searchEditText = binding.searchEditText

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button1 -> {
                        selection = 1
                    }

                    R.id.button2 -> {
                        selection = 2
                    }

                    R.id.button3 -> {
                        selection = 3
                    }

                    R.id.button4 -> {
                        selection = 4
                    }

                    R.id.button5 -> {
                        selection = 5
                    }
                }

            }
        }


        val sheet: Sheet = workbook.createSheet("Sayfa 1")

        val cellStyle = getHeaderStyle(workbook)

        createSheetHeader(cellStyle, sheet)


        excelButton.setOnClickListener {
            addData(
                sheet, this, workbook, filteredList
            )

            askForPermissions()
        }

        db.collection("People").addSnapshotListener { value, _ ->
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


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("DefaultLocale")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredList = ArrayList()
                if (p0.toString() != "") {
                    filteredList.clear()
                    for (item in personList) {
                        db.collection("People").document(item.personID).get()
                            .addOnSuccessListener { user ->

                                var name = ""
                                when (selection) {
                                    1 -> {
                                        name = user.get("name").toString()
                                    }

                                    2 -> {
                                        name = user.get("year").toString()

                                    }

                                    3 -> {
                                        name = user.get("city").toString()
                                    }

                                    4 -> {
                                        name = user.get("school").toString()
                                    }

                                    5 -> {
                                        name = user.get("field").toString()
                                    }
                                }
                                println(name)
                                if (name.lowercase(Locale.getDefault())
                                        .contains(p0.toString().lowercase(Locale.getDefault()))
                                ) {
                                    filteredList.add(item)
                                }
                                setupRecyclerView(filteredList)


                            }

                    }
                } else {
                    setupRecyclerView(personList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private fun setupRecyclerView(list: ArrayList<Person>) {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = PersonAdapter(list)
        recyclerView.adapter = recyclerViewAdapter
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