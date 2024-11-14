package com.example.trelloapp.activities.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.R
import com.example.trelloapp.activities.adapters.CardColorAdapter
import com.example.trelloapp.activities.adapters.MemberCardDetailAdapter
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Board
import com.example.trelloapp.activities.models.Users
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CardDetailActivity : AppCompatActivity() {
    lateinit var mBoard : Board
    private var listItemPosition = -1
    private var cardPosition = -1
    var etCardName : EditText? = null
    var btnUpdate : Button? = null
    var colorRecyclerView : RecyclerView? = null
    var rvAddingMembers : RecyclerView? = null
    var tvColorChanging : TextView? = null
    var mColorSelected : String = ""
    var btnAddingMember : TextView? = null
    var tvSelectDueDate : TextView? = null
    var mSelectedDate : Long = 0
   lateinit var usersData : ArrayList<Users>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        receiveDataFromTaskActivity()
        toolbarTrigger()
        FirebaseClass().getUsersForCardDetails(this,mBoard.joiners)
        tvSelectDueDate = findViewById(R.id.tv_select_dueDate)
        mSelectedDate = mBoard.taskList[listItemPosition].cards[cardPosition].dueDate

        if (mSelectedDate > 0){
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = sdf.format(Date(mSelectedDate))
            tvSelectDueDate!!.text = selectedDate
        }

        tvColorChanging = findViewById(R.id.tvColorChanging)
        mColorSelected = mBoard.taskList[listItemPosition].cards[cardPosition].colorTint

        if (mColorSelected.isNotEmpty()){
            chooseColor()
        }
        tvColorChanging!!.setOnClickListener {
            dialogForChangingColorOfTheCard()
        }

        btnAddingMember = findViewById(R.id.tvAddingMember)
        btnAddingMember!!.setOnClickListener {
            dialogAddingMembers()
        }


        tvSelectDueDate!!.setOnClickListener {
            setDueDateFromCalender()
        }


        etCardName = findViewById(R.id.etCardName)
        etCardName!!.setText(mBoard.taskList[listItemPosition].cards[cardPosition].cardName)
        etCardName!!.setSelection(etCardName!!.text.toString().length)


        btnUpdate = findViewById(R.id.btnUpdateCardDetails)
        btnUpdate!!.setOnClickListener {
            editingCardData()
            finish()
            // update card name
            // update other stuff
            // finish()
        }


    }
    private fun toolbarTrigger(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_card_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = mBoard.taskList[listItemPosition].cards[cardPosition].cardName
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this,R.color.white))
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun receiveDataFromTaskActivity(){
        if (intent.hasExtra("SEND_BOARD")){
            mBoard = intent.getParcelableExtra("SEND_BOARD", Board::class.java)!!
        }
        if (intent.hasExtra("LIST_ITEM_POSITION")){
            listItemPosition = intent.getIntExtra("LIST_ITEM_POSITION",-1)
        }
        if (intent.hasExtra("SEND_BOARD")){
            cardPosition = intent.getIntExtra("CARD_POSITION", -1)
        }
    }

    private fun deleteCard(itemListPosition : Int, cardPosition : Int){
        mBoard.taskList[itemListPosition].cards.removeAt(cardPosition)
        mBoard.taskList.removeAt(mBoard.taskList.size-1)
        FirebaseClass().updateTaskList(this,mBoard)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun dialogForDeletingCard(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.deleting_card_item)

        dialog.findViewById<TextView>(R.id.tvConfirmDeleting).setOnClickListener {
            deleteCard(listItemPosition,cardPosition)
            dialog.dismiss()


        }
        dialog.findViewById<TextView>(R.id.tvCancelDeleting).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun colorList():ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }

    private fun dialogForChangingColorOfTheCard(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_color_changing)

        colorRecyclerView = dialog.findViewById(R.id.rvColorChangingForTheCard)
        colorRecyclerView?.layoutManager = LinearLayoutManager(
            dialog.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        colorRecyclerView?.setHasFixedSize(true)


        val adapter = CardColorAdapter(this, colorList())
        colorRecyclerView?.adapter = adapter

        adapter.setOnClickListener(object : CardColorAdapter.OnClickListener{
            override fun onClick(position: Int, color: String) {
                mColorSelected = color
                chooseColor()
                dialog.dismiss()
            }

        })

        dialog.show()



    }
    private fun chooseColor(){
        tvColorChanging!!.text = ""
        tvColorChanging!!.setBackgroundColor(Color.parseColor(mColorSelected))
    }


    private fun editingCardData(){
        mBoard.taskList[listItemPosition].cards[cardPosition].cardName = etCardName!!.text.toString()
        mBoard.taskList[listItemPosition].cards[cardPosition].colorTint = mColorSelected
        mBoard.taskList[listItemPosition].cards[cardPosition].dueDate = mSelectedDate
        mBoard.taskList.removeAt(mBoard.taskList.size-1)
       /* val card = Cards(
            etCardName!!.text.toString()
            ,mBoard.taskList[listItemPosition].createdBy
            ,mBoard.taskList[listItemPosition].cards[cardPosition].assignedTo
            ,mColorSelected)
        mBoard.taskList[listItemPosition].cards.add(card)*/
        FirebaseClass().updateTaskList(this,mBoard)
        setResult(Activity.RESULT_OK)
    }

    fun returnUsersList(list : ArrayList<Users>):ArrayList<Users>{
        usersData = list
        return usersData
    }

    private fun dialogAddingMembers(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.members_dialog)

        rvAddingMembers = dialog.findViewById(R.id.rvAddingMembers)
        rvAddingMembers?.layoutManager = LinearLayoutManager(
            dialog.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        rvAddingMembers?.setHasFixedSize(true)

        // we need arraylist of users
        val adapter = MemberCardDetailAdapter(this, returnUsersList(usersData))
        rvAddingMembers?.adapter = adapter

        dialog.show()
    }

    fun setDueDateFromCalender(){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                val dayOfMonth = if (day < 10) "0$day" else "$day"
                val monthOfYear = if ((month+1) < 10) "0${month+1}" else "${month+1}"
                val selectedDate = "$day/$month/$year"
                tvSelectDueDate!!.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDate = theDate!!.time


            },
            year,
            month,
            day)
        dpd.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemDelete -> {
                dialogForDeletingCard()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}