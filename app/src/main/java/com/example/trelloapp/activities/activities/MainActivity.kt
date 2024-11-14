package com.example.trelloapp.activities.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.activities.adapters.BoardAdapter
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Board
import com.example.trelloapp.activities.models.Users
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView



class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    var mBoardList : ArrayList<Board>? = null
    var drawerLayout : DrawerLayout? = null
    var navigationView : NavigationView? = null
    var profilePicture : CircleImageView? = null
    var nameText : TextView? = null
    var boardButton : FloatingActionButton? = null
    lateinit var userName : String

    lateinit var mSharedPreferences: SharedPreferences

    val updateBoardCards = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            FirebaseClass().addBoardToList(this)
        }
    }


    val updateDataResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if (result.resultCode == RESULT_OK){
            FirebaseClass().userLogin(this)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
        boardButton = findViewById(R.id.leadToBoard)

        //FirebaseClass().userLogin(this@MainActivity)
        mSharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean("fcmTokenUpdated", false)

        if (tokenUpdated){
            FirebaseClass().userLogin(this)
        }else{
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener(this@MainActivity) {
                    updateFcmToken(it)
                }
        }

        navigationView!!.setNavigationItemSelectedListener(this)
        toolbarTrigger()



        boardButton?.setOnClickListener {
            val intent = Intent(this,BoardActivity::class.java)
            intent.putExtra("NAME",userName)
            updateBoardCards.launch(intent)
        }

        Log.d("CheckingDataNow", "$mBoardList")







    }

    private fun toolbarTrigger(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mainActivity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this,R.color.white))
        toolbar.setNavigationOnClickListener {
            drawNavDrawer()
        }
    }
    private fun drawNavDrawer(){
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logOut -> {
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                finish()
            }
            R.id.profileItem -> {
                val intent = Intent(this,ProfileActivity::class.java)
                updateDataResult.launch(intent)
            }

        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }
    // enter email
    // get the different member
    // add the boards that member has


    fun showBoardData(itemList : ArrayList<Board>){
        mBoardList = itemList
        val recyclerView = findViewById<RecyclerView>(R.id.rvBoardRecycler)
             val textZeroBoard = findViewById<TextView>(R.id.tvBoardZero)
        if (itemList.size > 0){
            recyclerView.visibility = View.VISIBLE
            textZeroBoard.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

            val adapter = BoardAdapter(this@MainActivity,itemList)
            recyclerView.adapter = adapter
            adapter.setOnClickListener(object : BoardAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskActivity::class.java)
                    intent.putExtra("SEND_DATA",model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            recyclerView.visibility = View.GONE
            textZeroBoard.visibility = View.VISIBLE
        }

    }

     fun settingHeaderUserData(user:Users){

         userName = user.name
        // put the ids here, if You put them in the onCreate the name from the fire store
        //doesn't load because the program run the ui first before he fetches data from fire store
        profilePicture = findViewById(R.id.pPictureIv)
        nameText = findViewById(R.id.tvName)
         val picture1 = user.profileImageUrl
        Log.d("MessagePicture", "User picture URL: ${user.profileImageUrl}")
            Glide
                .with(this@MainActivity)
                .load(picture1)
                .centerCrop()
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(profilePicture!!)

            nameText?.text = user.name

         FirebaseClass().addBoardToList(this)

    }

    fun updateFcmToken(token : String){
        val hashMap = HashMap<String,Any>()
        hashMap["token"] = token

        FirebaseClass().updateUserInformation(this,hashMap)

    }

    fun tokenUpdateSuccess(){
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean("fcmTokenUpdated",true)
        editor.apply()
        FirebaseClass().userLogin(this)

    }



}