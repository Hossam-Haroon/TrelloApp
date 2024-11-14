package com.example.trelloapp.activities.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.R
import com.example.trelloapp.activities.activities.TaskActionListener
import com.example.trelloapp.activities.activities.TaskActivity
import com.example.trelloapp.activities.models.Task

class TaskAdapter(private val context: Context, private val task : ArrayList<Task>, private  val listener: TaskActionListener):
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    //var onClickListener : OnClickListener? = null
    class TaskViewHolder(view : View): RecyclerView.ViewHolder(view) {
        var addListButton = view.findViewById<TextView>(R.id.tvAddList)
        var addTaskBar = view.findViewById<CardView>(R.id.cvAddTask)
        var addCardBar = view.findViewById<CardView>(R.id.cvAddingCard)
        var detailsBar = view.findViewById<LinearLayout>(R.id.llDetailsBar)
        var cardDetails = view.findViewById<LinearLayout>(R.id.llTaskDetails)
        var editDeleteBar = view.findViewById<CardView>(R.id.card_Edit_delete_bar)
        var addCardButton = view.findViewById<TextView>(R.id.tvAddCard)
        var taskName = view.findViewById<TextView>(R.id.tvTaskName)
        var cancelTaskCreating = view.findViewById<ImageButton>(R.id.ibCancelTaskCreating)
        var confirmTaskCreating = view.findViewById<ImageButton>(R.id.ibConfirmTaskCreating)
        var secondBarConfirm = view.findViewById<ImageButton>(R.id.ibSecondBarConfirm)
        var secondBarCancel = view.findViewById<ImageButton>(R.id.ibSecondBarCancel)
        var deleteButton = view.findViewById<ImageButton>(R.id.ibDelete)
        var editButton = view.findViewById<ImageButton>(R.id.ibEdit)
        var cancelCardButton = view.findViewById<ImageButton>(R.id.ibCancelCardButton)
        var confirmCardButton = view.findViewById<ImageButton>(R.id.ibConfirmCardButton)
        var addingText = view.findViewById<EditText>(R.id.etAddTask)
        var addCard = view.findViewById<EditText>(R.id.etAddCard)
        var secondBarAddingText = view.findViewById<EditText>(R.id.etSecondBarAddText)
        var cardsRecyclerView = view.findViewById<RecyclerView>(R.id.rvTasks)



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        val layoutParams = LinearLayout.LayoutParams((parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)
        view.layoutParams = layoutParams

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val model = task[position]

        if (position == task.size-1){
            holder.addListButton.visibility = View.VISIBLE
            holder.cardDetails.visibility = View.GONE
        }else{
            holder.addListButton.visibility = View.GONE
            holder.cardDetails.visibility = View.VISIBLE
        }
        holder.taskName.text = model.name



        //holder.taskName.text = model.name
        holder.addListButton.setOnClickListener {
            holder.addListButton.visibility = View.GONE
            holder.addTaskBar.visibility = View.VISIBLE
        }
        holder.cancelTaskCreating.setOnClickListener {
            holder.addListButton.visibility = View.VISIBLE
            holder.addTaskBar.visibility = View.GONE
        }
        holder.confirmTaskCreating.setOnClickListener {
            if (holder.addingText.text.isEmpty()){
                Toast.makeText(context,"Add anything please!!",Toast.LENGTH_LONG).show()
            }else{
                holder.addTaskBar.visibility = View.GONE
                holder.cardDetails.visibility = View.VISIBLE
                val title = holder.addingText.text.toString()
                listener.onAddTaskClicked(title)
                /*if (context is TaskActivity) {
                    context.addTaskElements(title)
                }*/
            }
        }
        holder.secondBarAddingText.setText(model.name)


        holder.editButton.setOnClickListener {
            holder.detailsBar.visibility = View.GONE
            holder.editDeleteBar.visibility = View.VISIBLE
        }

        holder.secondBarCancel.setOnClickListener {
            holder.detailsBar.visibility = View.VISIBLE
            holder.editDeleteBar.visibility = View.GONE
        }
        holder.secondBarConfirm.setOnClickListener {
            if (holder.secondBarAddingText.text.isEmpty()){
                Toast.makeText(context,"please fill the edit text!!",Toast.LENGTH_LONG).show()
            }else{
                val name = holder.secondBarAddingText.text.toString()
                if (context is TaskActivity){
                    context.editTask(name,position)
                }
            }
            holder.detailsBar.visibility = View.VISIBLE
            holder.editDeleteBar.visibility = View.GONE
        }
        holder.deleteButton.setOnClickListener {
            if (context is TaskActivity){
                context.deleteTask(position)
            }
        }
        holder.addCardButton.setOnClickListener {
            holder.addCardButton.visibility = View.GONE
            holder.addCardBar.visibility = View.VISIBLE
        }

        holder.cancelCardButton.setOnClickListener {
            holder.addCardButton.visibility = View.VISIBLE
            holder.addCardBar.visibility = View.GONE
        }
        holder.confirmCardButton.setOnClickListener {
            val cardName = holder.addCard.text.toString()
            holder.addCardButton.visibility = View.VISIBLE
            holder.addCardBar.visibility = View.GONE

            if (context is TaskActivity){
                context.addCardItems(cardName,position)
            }

        }
        holder.cardsRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        /*holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position)
            }
        }*/

        holder.cardsRecyclerView.setHasFixedSize(true)

        val adapter = CardAdapter(context, model.cards)
        holder.cardsRecyclerView.adapter = adapter

        adapter.setOnClickListener(object :
            CardAdapter.OnClickListener{
            override fun onClick(cardPosition: Int) {
                if (context is TaskActivity){
                    context.selectedCardForSpecificListItem(holder.adapterPosition,cardPosition)
                }
            }
        }
        )



    }

    override fun getItemCount(): Int {
       return task.size
    }
   /* fun setOnClickListener(onClickListener : TaskAdapter.OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(listItemPosition : Int)
    }*/

    private  fun Int.toDp(): Int = (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this*Resources.getSystem().displayMetrics.density).toInt()


}