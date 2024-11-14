package com.example.trelloapp.activities.activities

import android.app.Activity
import android.app.Dialog
import android.app.Instrumentation.ActivityResult
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Board
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class BoardActivity : AppCompatActivity() {
    var toolbar : Toolbar? = null
    var dialog : Dialog? = null
    var selectedImage : Uri? = null
    var boardImage : String = ""
    var imageHolder : CircleImageView? = null
    var btnCreateBoard : Button? = null
    var boardName : EditText? = null
    var creatorName : String = ""
    private val startGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null){
            selectedImage = it.data?.data
            Glide
                .with(this)
                .load(selectedImage)
                .centerCrop()
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(imageHolder!!)
        }
    }

    val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        if(isGranted){
            Toast.makeText(this,"permission is granted!!", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startGallery.launch(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        boardToolBar()
        btnCreateBoard = findViewById(R.id.btnBoard)
        imageHolder = findViewById(R.id.ivBoard)
        imageHolder?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                &&
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES)
            ) {
                Toast.makeText(this,"permission is required!!",Toast.LENGTH_LONG).show()
            }else{
                requestPermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
         creatorName = intent.getStringExtra("NAME").toString()
        btnCreateBoard?.setOnClickListener {
            if (selectedImage != null){
                createFullBoard()
            }else{
                progressbar()
                createBoard()
            }



        }

    }

    private fun boardToolBar(){
        toolbar = findViewById(R.id.toolbar_board)
        setSupportActionBar(toolbar)
        toolbar?.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        toolbar?.navigationIcon?.setTint(ContextCompat.getColor(this,R.color.white))
        toolbar?.setTitle(R.string.board_name)
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
    fun creationNotification(){
        Toast.makeText(this,"Board has been created!!",Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_OK)
        progressbarDismiss()
        finish()
    }
    private fun createFullBoard(){

            val firebaseStorage = FirebaseStorage
                .getInstance()
                .reference
                .child("BOARD_DATA"+System.currentTimeMillis()+"."+getFileExtension(selectedImage))

            firebaseStorage.putFile(selectedImage!!).addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->
                    boardImage = uri.toString()
                    Log.d("valueImage", "board image is : $boardImage")
                    createBoard()
                }
            }.addOnFailureListener {
                Log.d("valueImage", "board image is : $boardImage")

                progressbarDismiss()

            }

    }

    private fun createBoard(){
        val joiners : ArrayList<String> = ArrayList()
        joiners.add(FirebaseAuth.getInstance().currentUser!!.uid)
        boardName = findViewById(R.id.etBoard)
        val board = Board(boardName?.text.toString(),boardImage,creatorName,joiners)
        FirebaseClass().createBoard(this,board)

    }
    private fun getFileExtension(uri : Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}