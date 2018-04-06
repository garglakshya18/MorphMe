package com.example.shashankmohabia.morphme.MainGame.Activities.BottomNavbarOptions.HomeFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shashankmohabia.morphme.MainGame.HomeFragments.QuestionAdapter
import com.example.shashankmohabia.morphme.MainGame.HomeFragments.QuestionModel
import com.example.shashankmohabia.morphme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.util.ArrayList

class HomeFragment : Fragment() {

    var questionList: ArrayList<QuestionModel> = ArrayList()
    var questionAdapter: QuestionAdapter? = null

    var currentPhase = "Phase1"
    var currentLevel = "Level1"

    var levelTracker = 0

    var i = 0
    var questionCount = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getQuestionData()

        questionAdapter = QuestionAdapter(view.context, R.layout.swing_item, questionList)


        swingView?.setAdapter(questionAdapter)
        swingView?.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                questionList.removeAt(0)
                questionAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                val item = dataObject as QuestionModel
                alert(item.questionCompanionQuestion.toString()) {
                    title = "Fake"
                    positiveButton("Yes") {
                        val companionQuestionAnswer = "Yes"
                        if (item.questionAnswer.equals("Fake")) {
                            addCorrectResponse(item, companionQuestionAnswer)
                        } else {
                            addWrongResponse(item, companionQuestionAnswer)
                        }
                    }
                    negativeButton("No") {
                        val companionQuestionAnswer = "No"
                        if (item.questionAnswer.equals("Fake")) {
                            addCorrectResponse(item, companionQuestionAnswer)
                        } else {
                            addWrongResponse(item, companionQuestionAnswer)
                        }
                    }
                }.show()

                levelTracker++
                setLevelIndicator()

            }

            override fun onRightCardExit(dataObject: Any) {
                val item = dataObject as QuestionModel
                alert(item.questionCompanionQuestion.toString()) {
                    title = " Not Fake"
                    positiveButton("Yes") {
                        val companionQuestionAnswer = "Yes"
                        if (item.questionAnswer.equals("Not Fake")) {
                            addCorrectResponse(item, companionQuestionAnswer)
                        } else {
                            addWrongResponse(item, companionQuestionAnswer)
                        }
                    }
                    negativeButton("No") {
                        val companionQuestionAnswer = "No"
                        if (item.questionAnswer.equals("Not Fake")) {
                            addCorrectResponse(item, companionQuestionAnswer)
                        } else {
                            addWrongResponse(item, companionQuestionAnswer)
                        }
                    }
                }.show()

                levelTracker++
                setLevelIndicator()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}

            override fun onScroll(scrollProgressPercent: Float) {}
        })


        // Optionally add an OnItemClickListener
        //swingView?.setOnItemClickListener(SwipeFlingAdapterView.OnItemClickListener { itemPosition, dataObject -> Toast.makeText(view?.context, "Left", Toast.LENGTH_LONG).show() })


    }

    private fun setLevelIndicator() {
        when (levelTracker) {
            2 -> {
                level1.visibility = View.VISIBLE
            }
            4 -> {
                level2.visibility = View.VISIBLE
            }
            6 -> {
                level3.visibility = View.VISIBLE
            }
            8 -> {
                level4.visibility = View.VISIBLE
            }
        }
    }

    private fun addWrongResponse(item: QuestionModel, companionQuestionAnswer: String) {
        val dbRefer = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid).child("Responses").child("Wrong").child(item.questionId)
        val questionInfo: MutableMap<String, Any> = mutableMapOf()
        questionInfo.put("companionQuestionResponse", companionQuestionAnswer)
        dbRefer.updateChildren(questionInfo)
    }

    private fun addCorrectResponse(item: QuestionModel, companionQuestionAnswer: String) {
        val dbRefer = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid).child("Responses").child("Correct").child(item.questionId)
        val questionInfo: MutableMap<String, Any> = mutableMapOf()
        questionInfo.put("companionQuestionResponse", companionQuestionAnswer)
        dbRefer.updateChildren(questionInfo)
    }

    private fun getQuestionData() {

        val questionDb = FirebaseDatabase.getInstance().reference.child("Questions")
        questionDb.addChildEventListener(object : ChildEventListener {


            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.exists() and ((dataSnapshot.child("phase").value == currentPhase) and (dataSnapshot.child("level").value == currentLevel))) {
                        val id = dataSnapshot.key
                        val caption = dataSnapshot.child("caption")?.value!!.toString()
                        val answer = dataSnapshot.child("answer")?.value!!.toString()
                        val companionQuestion = dataSnapshot.child("companionQuestion")?.value!!.toString()
                        val mediaDownloadUri = dataSnapshot.child("mediaDownloadUri")?.value!!.toString()
                        val item = QuestionModel(id, caption, answer, companionQuestion, mediaDownloadUri)


                        questionCount++
                        when (questionCount) {
                            2 -> {
                                currentLevel = "Level2"
                            }
                            4 -> {
                                currentLevel = "Level3"
                                // toast("Level 3")
                            }
                            6 -> {
                                currentPhase = "Phase2"
                                currentLevel = "Level4"
                                //toast("Level 4")
                            }
                        }

                        questionList.add(item)
                        questionAdapter?.notifyDataSetChanged()
                    } else {
                        toast("No questions available for $currentLevel")
                        questionCount = 10
                    }
                } else {
                    toast("No questions available")
                    questionCount = 10
                }
            }


            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}

        })
    }

}

