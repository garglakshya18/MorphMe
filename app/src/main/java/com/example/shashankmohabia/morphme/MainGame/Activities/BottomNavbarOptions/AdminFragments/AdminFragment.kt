package com.example.shashankmohabia.morphme.MainGame.AdminFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shashankmohabia.morphme.MainGame.AdminFragments.AddQuestions.AddQuestionFragment
import com.example.shashankmohabia.morphme.MainGame.AdminFragments.AllUsers.AllUsersFragment

import com.example.shashankmohabia.morphme.R
import kotlinx.android.synthetic.main.fragment_admin.*

class AdminFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allUsers.setOnClickListener(View.OnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.mainFrame, AllUsersFragment())?.addToBackStack(null)?.commit()
        })
        addQuestion.setOnClickListener(View.OnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.mainFrame, AddQuestionFragment())?.addToBackStack(null)?.commit()
        })
        overallAnalysis.setOnClickListener(View.OnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.mainFrame, AnalysisFragment())?.addToBackStack(null)?.commit()
        })
    }
}
