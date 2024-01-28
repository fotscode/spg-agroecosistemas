package com.example.spgunlp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentLoginBinding
import com.example.spgunlp.io.AuthService
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.active.ActiveFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.example.spgunlp.util.performLogin
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class LoginFragment : BaseFragment() {

    private val authService: AuthService by lazy {
        AuthService.create()
    }

    override var bottomNavigationViewVisibility = View.GONE

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Check if the user is already logged in
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val jwt = preferences["jwt", ""]
        if (jwt.contains("."))
            goToActiveFragment()


        binding.btnIniciarSesion.setOnClickListener() {
            val editEmail = binding.editMail.text.toString()
            val editPassword = binding.editPassword.text.toString()
            lifecycleScope.launch {
                if (performLogin(editEmail, editPassword, requireContext(), authService)) {
                    Toast.makeText(
                        context,
                        "Se ha iniciado sesión correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToActiveFragment()
                }
            }
        }

        binding.btnCrearUsuario.setOnClickListener() {
            goToRegisterFragment()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun goToActiveFragment() {
        val newFragment = ActiveFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.add(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.commit()
    }

    private fun goToRegisterFragment() {
        val newFragment = RegisterFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.add(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.commit()
    }
}