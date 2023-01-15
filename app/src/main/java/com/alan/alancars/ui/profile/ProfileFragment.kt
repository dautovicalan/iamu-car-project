package com.alan.alancars.ui.profile

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.alan.alancars.R
import com.alan.alancars.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        displayUserProfile()

    }

    private fun displayUserProfile() {
        binding.txtUserEmail.setText(auth.currentUser?.email.toString())
        binding.txtUserFirstName.setText(
            auth.currentUser?.displayName?.split(" ")?.get(0)?.trim() ?: getString(R.string.first_name))
        binding.txtUserLastName.setText(
            auth.currentUser?.displayName?.split(" ")?.get(1)?.trim() ?: getString(R.string.last_name))
    }

    private fun setupListeners() {
        binding.btnOpenChangePasswordDialog.setOnClickListener {
            showPasswordChangeDialog()
        }
        binding.btnResetPassword.setOnClickListener {
            resetPassword()
        }
        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun showPasswordChangeDialog() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        builder.setTitle("Change Password")
        val viewInflated: View = LayoutInflater.from(context)
            .inflate(com.alan.alancars.R.layout.change_password, view as ViewGroup?, false)
        val input = viewInflated.findViewById<View>(com.alan.alancars.R.id.txtInputNewPassword) as EditText
        builder.setView(viewInflated)

        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog, _ ->
            dialog.dismiss()
            var newPassword = input.text.toString().trim()
            if (newPassword.isNotBlank()){
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(), getString(R.string.password_changed), Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            Log.d(TAG, it.exception.toString())
                        }
                    }
            } else {
                input.error = "Please insert"
            }
        }
        builder.setNegativeButton(
            android.R.string.cancel
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }


    private fun resetPassword() {
        auth.currentUser?.email?.let { auth.sendPasswordResetEmail(it) }
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), getString(R.string.check_email), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

    }

    private fun updateProfile() {
        if (formValid()){
            val profileUpdates = userProfileChangeRequest {
                displayName = "${binding.txtUserFirstName.text.toString().trim()} ${binding.txtUserLastName.text.toString().trim()}"
            }
            auth.currentUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
    }

    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.txtUserFirstName, binding.txtUserLastName)
            .forEach {
                if (it.text?.trim()?.isBlank() == true){
                    it.error = "Please insert"
                    ok = false
                }
            }
        return ok
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}