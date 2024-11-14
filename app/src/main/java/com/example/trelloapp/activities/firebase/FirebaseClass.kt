package com.example.trelloapp.activities.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trelloapp.activities.activities.BoardActivity
import com.example.trelloapp.activities.activities.CardDetailActivity
import com.example.trelloapp.activities.activities.MainActivity
import com.example.trelloapp.activities.activities.MemberActivity
import com.example.trelloapp.activities.activities.ProfileActivity
import com.example.trelloapp.activities.activities.SignUpActivity
import com.example.trelloapp.activities.activities.TaskActivity
import com.example.trelloapp.activities.models.Board
import com.example.trelloapp.activities.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class FirebaseClass {

    val fireStore = FirebaseFirestore.getInstance()

    // we register the user twice,
      // first in the authentication section in firebase,
      // second in the firestore database section


    fun registerUser(activity:SignUpActivity,user : Users){
        //collection
          // -> documents ( the IDs for the emails that exist in the authentication tab)
          // -> fields(like name, email, number)
        fireStore.collection("users")
            .document(userId())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.setThingsUp()
        }
    }
    fun createBoard(activity:BoardActivity,board : Board){
        fireStore.collection("Boards")
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,"Board has been created successfully!!",Toast.LENGTH_LONG).show()
                activity.creationNotification()
            }.addOnFailureListener {
                Toast.makeText(activity,"Board has failed to be created!!",Toast.LENGTH_LONG).show()
            }
    }

    fun addBoardToList(activity: MainActivity){
        fireStore.collection("Boards")
            .whereArrayContains("joiners",userId())
            .get()
            .addOnSuccessListener {
                document->
                val boardList : ArrayList<Board> = ArrayList()
                Log.d("this is list","$boardList")
                for (i in document.documents){
                    val boardItem = i.toObject(Board::class.java)!!
                    boardItem.documentId = i.id
                    boardList.add(boardItem)

                }
                activity.showBoardData(boardList)

            }
    }

    fun userLogin(activity:Activity){
        fireStore.collection("users")
            .document(userId())
            .get()
            .addOnSuccessListener {
                val signedInUser = it.toObject(Users::class.java)!!
                Log.d("LoggedInUser","data : $signedInUser")
                    when(activity){
                        is MainActivity -> {
                            activity.settingHeaderUserData(signedInUser)
                        }
                        is ProfileActivity -> {
                            activity.profileSettings(signedInUser)
                        }
                    }
            }
    }
    fun updateUserInformation(activity : Activity, hashMap: HashMap<String,Any>){
        fireStore.collection("users")
            .document(userId())
            .update(hashMap)
            .addOnSuccessListener {

                when(activity){
                    is ProfileActivity -> activity.successNotification()
                    is MainActivity -> activity.tokenUpdateSuccess()
                }

            }
    }

    fun getDataForTaskActivity(activity: TaskActivity,documentId : String){
        fireStore.collection("Boards")
            .document(documentId)
            .get()
            .addOnSuccessListener {
                val document = it.toObject(Board::class.java)
                document?.documentId = it.id
                activity.addingAdapterData(document!!)


            }
    }

    fun updateTaskList(activity : TaskActivity,board : Board){
        var hashMap = HashMap<String,Any>()
        hashMap["taskList"] = board.taskList
        fireStore.collection("Boards")
            .document(board.documentId)
            .update(hashMap)
            .addOnSuccessListener {
                    activity.dataOnSuccess()
            }
    }
    fun updateTaskList(activity : CardDetailActivity,board : Board){
        var hashMap = HashMap<String,Any>()
        hashMap["taskList"] = board.taskList
        fireStore.collection("Boards")
            .document(board.documentId)
            .update(hashMap)
            .addOnSuccessListener {
                Toast.makeText(activity,"Your Card Has Been Upgraded!!",Toast.LENGTH_LONG).show()
            }
    }
    fun getUsersForMember(activity : MemberActivity, joiners: ArrayList<String> ){
        fireStore.collection("users")
            .whereIn("id",joiners)
            .get()
            .addOnSuccessListener {
                document->
                val userList : ArrayList<Users> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(Users::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }

                activity.addMembers(userList)

            }
    }
    fun getUsersForCardDetails(activity : CardDetailActivity, joiners: ArrayList<String> ){
        fireStore.collection("users")
            .whereIn("id",joiners)
            .get()
            .addOnSuccessListener {
                    document->
                val userList : ArrayList<Users> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(Users::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }

                activity.returnUsersList(userList)

            }
    }
    fun addMemberWithEmail(activity: MemberActivity, email: String) {
        fireStore
            .collection("users")
            .whereEqualTo("email",email)
            .get()
            .addOnSuccessListener {document->

                if (document.documents.size > 0){
                    val user = document.documents[0].toObject(Users::class.java)
                    if (user != null){
                        activity.addMembers(activity.addingAssignedMembers(user))
                    }
                }


            }
    }
    fun updateAddingMembersList( board: Board){
        val hashMap = HashMap<String, Any>()
        hashMap["joiners"] = board.joiners
        fireStore
            .collection("Boards")
            .document(board.documentId)
            .update(hashMap)
            .addOnSuccessListener {

            }
    }
    // take the id of the user
    //
    // we need to add the user id to the joiners then update the data then show it


    // we need to get the ID from the firebase instance
    fun userId(): String{
        val currentUid = FirebaseAuth.getInstance().currentUser
        var currentUser = ""
        if (currentUid != null){
            currentUser = currentUid.uid
        }
        return currentUser
    }



}