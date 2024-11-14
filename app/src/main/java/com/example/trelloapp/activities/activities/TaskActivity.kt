package com.example.trelloapp.activities.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.R
import com.example.trelloapp.activities.adapters.TaskAdapter
import com.example.trelloapp.activities.firebase.FirebaseClass
import com.example.trelloapp.activities.models.Board
import com.example.trelloapp.activities.models.Cards
import com.example.trelloapp.activities.models.Task


class TaskActivity : AppCompatActivity(),TaskActionListener {
    var recyclerViewTasks : RecyclerView? = null
      lateinit var mBoard : Board
      private lateinit var boardId : String

      val dataBack = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
          if (it.resultCode == RESULT_OK){
              FirebaseClass().getDataForTaskActivity(this,boardId)
          }
      }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        recyclerViewTasks = findViewById(R.id.rvRecyclerViewTask)

        if (intent.hasExtra("SEND_DATA")){
            boardId = intent.getStringExtra("SEND_DATA")!!
        }

        FirebaseClass().getDataForTaskActivity(this,boardId)









    }

    fun selectedCardForSpecificListItem(listItemPosition : Int,cardPosition : Int){
        val intent = Intent(this@TaskActivity,CardDetailActivity::class.java)
        intent.putExtra("SEND_BOARD", mBoard)
        intent.putExtra("LIST_ITEM_POSITION", listItemPosition)
        intent.putExtra("CARD_POSITION", cardPosition)
        dataBack.launch(intent)
        //startActivity(intent)
    }

    fun toolbarTrigger(title : String){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_taskTitle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = title
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this,R.color.white))
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }




    }
    fun addingAdapterData(board : Board){
        mBoard = board
        val task = Task("Add List")
        mBoard.taskList.add(task)
        Log.d("taskListCheck", "$mBoard")



        //board.taskList.add(0,Task("asdasd",board.documentId))

        //FirebaseClass().updateTaskList(this@TaskActivity,board)
        toolbarTrigger(mBoard.name)

        recyclerViewTasks?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerViewTasks?.setHasFixedSize(true)

        val adapter = TaskAdapter(this, mBoard.taskList,this)
        recyclerViewTasks?.adapter = adapter


    }

    /*fun addTaskElements(taskName: String){
        // problem in mBoard it is empty

       var listItem = Task(taskName,FirebaseClass().userId())


        mBoard!!.taskList.add(0,listItem)
        mBoard!!.taskList.removeAt(mBoard!!.taskList.size - 1)


                //Log.d("taskListCheck", "${mBoard?.taskList}")
        FirebaseClass().updateTaskList(this@TaskActivity, mBoard!! )

    }*/
    fun dataOnSuccess(){

        FirebaseClass().getDataForTaskActivity(this,mBoard.documentId)
    }

    override fun onAddTaskClicked(taskName: String) {
        var listItem = Task(taskName,FirebaseClass().userId())


        mBoard.taskList.add(0,listItem)
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)


        //Log.d("taskListCheck", "${mBoard?.taskList}")
        FirebaseClass().updateTaskList(this@TaskActivity, mBoard)
    }

    fun editTask(name : String, position : Int){
        val listItem = mBoard.taskList
        // when we edit the task the cards get removed because we don't put a value for it to the task class so it will make it null
        listItem[position] = Task(name, FirebaseClass().userId())

        mBoard.taskList.add(0,listItem[position])
        mBoard.taskList.removeAt(mBoard.taskList.size-1)
        mBoard.taskList.removeAt(0)

        FirebaseClass().updateTaskList(this@TaskActivity,mBoard)

    }
    fun deleteTask(position: Int){
        mBoard.taskList.removeAt(position)
        mBoard.taskList.removeAt(mBoard.taskList.size-1)

        FirebaseClass().updateTaskList(this@TaskActivity,mBoard)
    }

    fun addCardItems(cardName : String, position:Int){
        mBoard.taskList.removeAt(mBoard.taskList.size-1)

        val assignedTo = ArrayList<String>()
        assignedTo.add(FirebaseClass().userId())

        val card = Cards(cardName,FirebaseClass().userId(),assignedTo)
        mBoard.taskList[position].cards.add(card)


        FirebaseClass().updateTaskList(this,mBoard)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.members_settings,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.toolbar_three_dots -> {
                val intent = Intent(this,MemberActivity::class.java)
                intent.putExtra("BOARD_DATA",mBoard)
                dataBack.launch(intent)
                //startActivity(intent)
                return true
            }


        }

        return super.onOptionsItemSelected(item)
    }
}