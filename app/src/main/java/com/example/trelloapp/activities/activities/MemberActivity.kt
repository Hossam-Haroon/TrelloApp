package com.example.trelloapp.activities.activities

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.R
import com.example.trelloapp.activities.adapters.MemberAdapter
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Board
import com.example.trelloapp.activities.models.Users

class MemberActivity : AppCompatActivity() {
     //var mBoard : Board? = null
    var recyclerViewMember : RecyclerView? = null
    var toolbar : Toolbar? = null
    lateinit var assignedMembers : ArrayList<Users>
    lateinit var mBoard: Board

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        memberToolBar()

         mBoard  = intent.getParcelableExtra("BOARD_DATA", Board::class.java)!!
        FirebaseClass().getUsersForMember(this,mBoard.joiners)

    }

    private fun memberToolBar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_member_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = "Members"
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this,R.color.white))
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
    }

    fun addMembers(list : ArrayList<Users>){
        assignedMembers = list
        recyclerViewMember = findViewById(R.id.rvMemberActivity)
        recyclerViewMember!!.layoutManager = LinearLayoutManager(this@MemberActivity)
        recyclerViewMember!!.setHasFixedSize(true)

        val adapter = MemberAdapter(this@MemberActivity, list)
        recyclerViewMember!!.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemAddMember -> {
                makingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makingDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.adding_member_dialog)

        dialog.findViewById<TextView>(R.id.tvAddMember).setOnClickListener {
            val email =  dialog.findViewById<EditText>(R.id.etMemberEmail).text.toString()
            FirebaseClass().addMemberWithEmail(this,email)
            setResult(Activity.RESULT_OK)
            dialog.dismiss()
            // here use the email to add the member

        }
        dialog.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    fun addingAssignedMembers(user: Users): ArrayList<Users> {
        mBoard.joiners.add(user.id)
        FirebaseClass().updateAddingMembersList(mBoard)
        assignedMembers.add(user)
        return assignedMembers


    }

   /* fun addingAssignedMembers(user: Users) {
        mBoard.joiners.add(user.id)
        FirebaseClass().updateAddingMembersList(this,mBoard,user)

    }

    fun addingMembersToTheList(user : Users){
        assignedMembers.add(user)
        addMembers(assignedMembers)
    }*/






}