package com.example.trelloapp.activities.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Users
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    var toolbar : Toolbar? = null
    var profileImage : CircleImageView? = null
    var etName : EditText? = null
    var etEmail : EditText? = null
    var etNumber : EditText? = null
    var selectedImageUri : Uri? = null
    var btnProfileActivity : Button? = null
    var profileImageUri : String? = null
    var dialog : Dialog? = null
    lateinit var users : Users


    val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        // check first if the data is not null or is it ok
        if (it.resultCode == RESULT_OK && it.data != null){
            // using uri assign the result to the image view
            selectedImageUri = it.data?.data
            Glide
                .with(this)
                .load(selectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(profileImage!!)
        }
    }

    val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        if (isGranted){
            Toast.makeText(this,"permission is granted!!", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // LAUNCH THE INTENT WITH THE RESULT LAUNCHER
            galleryResult.launch(intent)

        }else{
            Toast.makeText(this,"permission was denied!!",Toast.LENGTH_LONG).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        toolbarSettings()

        

        FirebaseClass().userLogin(this)
        btnProfileActivity = findViewById(R.id.btnProfileActivity)
        profileImage = findViewById(R.id.civProfileActivity)

        profileImage?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                &&
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES)
            ) {
                Toast.makeText(this,"permission is required!!",Toast.LENGTH_LONG).show()
            } else {
                requestPermission.launch(
                   android.Manifest.permission.READ_MEDIA_IMAGES
                )
            }
        }
        btnProfileActivity?.setOnClickListener {
            progressbar()
            updateUserData()
        }
    }

    private fun updateUserData(){
        if (selectedImageUri != null){
            val firebaseStorage = FirebaseStorage
                .getInstance()
                .reference
                .child("USER_DATA"+System.currentTimeMillis()+"."+getFileExtension(selectedImageUri))

            firebaseStorage.putFile(selectedImageUri!!).addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->
                    profileImageUri = uri.toString()
                    updateUser()
                }?.addOnFailureListener { exception ->
                    Toast.makeText(
                        this@ProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    progressbarDismiss()
                }
            }
        }
    }
    private fun getFileExtension(uri : Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    private fun updateUser(){
        val hashMap = HashMap<String,Any>()
        if (profileImageUri!!.isNotEmpty() && profileImageUri != users.profileImageUrl){
            hashMap["profileImageUrl"] = profileImageUri!!
        }
        if (etName?.text.toString() != users.name){
            hashMap["name"] = etName?.text.toString()
        }
        if (etNumber?.text.toString().toLong() != users.number){
            hashMap["number"] = etNumber?.text.toString().toLong()
        }
        Log.d("updateUser", "Updates map: $hashMap")
        FirebaseClass().updateUserInformation(this@ProfileActivity,hashMap)
    }
    fun profileSettings(user: Users){
        profileImage = findViewById(R.id.civProfileActivity)
        etName = findViewById(R.id.etProfileActivity_name)
        etEmail = findViewById(R.id.etProfileActivity_email)
        etNumber = findViewById(R.id.etProfileActivity_number)
        users = user
        Glide
            .with(this)
            .load(user.profileImageUrl)
            .centerCrop()
            .placeholder(R.drawable.profile_picture_placeholder)
            .into(profileImage!!)
        etName?.setText(user.name)
        etEmail?.setText(user.email)
        if (user.number != 0L){
            etNumber?.setText(user.number.toString())
        }
    }
    private fun toolbarSettings(){
        toolbar = findViewById(R.id.tbProfileActivity)
        setSupportActionBar(toolbar)
        toolbar?.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        toolbar?.navigationIcon?.setTint(ContextCompat.getColor(this,R.color.white))
        toolbar?.setTitle(R.string.activity_name)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun progressbar(){
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.progress_bar)
        dialog!!.show()
    }
    private fun progressbarDismiss(){
        dialog!!.dismiss()
    }

    fun successNotification(){
        Toast.makeText(this,"You have updated you data!!", Toast.LENGTH_LONG).show()
        progressbarDismiss()
        setResult(Activity.RESULT_OK)
        finish()
        //FirebaseClass().userLogin(MainActivity()) // it works but alittle bit weird as the theme background launch too

    }





}