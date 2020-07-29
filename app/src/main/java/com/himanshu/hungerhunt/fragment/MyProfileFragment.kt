package com.himanshu.hungerhunt.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.himanshu.hungerhunt.R

class MyProfileFragment : Fragment() {
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtMobileNumber: TextView
    private lateinit var txtAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.app_preferences),
            Context.MODE_PRIVATE
        )

        txtName = view.findViewById(R.id.txtName)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtAddress = view.findViewById(R.id.txtAddress)

        txtName.text = sharedPreferences.getString("userName", "no Name")
        txtEmail.text = sharedPreferences.getString("userEmail", "no Name")
        txtMobileNumber.text = sharedPreferences.getString("userMobileNumber", "no Name")
        txtAddress.text = sharedPreferences.getString("userAddress", "no Name")

        return view
    }
}