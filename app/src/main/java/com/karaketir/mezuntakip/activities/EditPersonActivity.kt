@file:Suppress("DEPRECATION")

package com.karaketir.mezuntakip.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karaketir.mezuntakip.databinding.ActivityEditPersonBinding
import com.karaketir.mezuntakip.services.glide
import com.karaketir.mezuntakip.services.placeHolderYap

class EditPersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPersonBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var spaceRef: StorageReference
    private lateinit var personImage: ImageView
    private var urlFinal = ""
    private var fileName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()


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

        val storageRef = storage.reference
        val imagesRef = storageRef.child("photos")

        fileName = "${personID}.jpg"
        spaceRef = imagesRef.child(fileName)

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
        personImage = binding.personImage

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
            urlFinal = it.get("photoURL").toString()
            personImage.glide(urlFinal, placeHolderYap(this@EditPersonActivity))

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
                "description" to description,
                "photoURL" to urlFinal
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


        personImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //İzin Verilmedi, iste
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    ), 1
                )


            } else {
                ImagePicker.with(this@EditPersonActivity)
                    .crop(13f, 17f) //Crop square image, its same as crop(1f, 1f)
                    .start()
            }

        }

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!
                personImage.glide("", placeHolderYap(this))

                Toast.makeText(this, "Lütfen Bekleyiniz", Toast.LENGTH_SHORT).show()


                spaceRef.putFile(uri).addOnSuccessListener {

                    val yuklenenGorselReference =
                        FirebaseStorage.getInstance().reference.child("photos").child(fileName)

                    yuklenenGorselReference.downloadUrl.addOnSuccessListener { downloadURL ->

                        urlFinal = downloadURL.toString()
                        personImage.setImageURI(uri)

                    }


                }

            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        //İzin Yeni Verildi
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.with(this@EditPersonActivity)
                    .crop(13f, 17f) //Crop square image, its same as crop(1f, 1f)
                    .start()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}