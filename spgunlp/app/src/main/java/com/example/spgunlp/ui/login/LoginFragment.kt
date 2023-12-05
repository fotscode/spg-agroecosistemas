package com.example.spgunlp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.logging.Logger


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


        binding.btnIniciarSesion.setOnClickListener(){
            performLogin()
        }

        binding.btnCrearUsuario.setOnClickListener(){
            goToRegisterFragment()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createSessionPreferences(jwt: String,email:String){
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        preferences["jwt"] = jwt
        preferences["email"] = email
    }

    private fun performLogin(){
        val editEmail=binding.editMail.text.toString()
        val editPassword=binding.editPassword.text.toString()

        // make the call to the remote API with coroutines
        lifecycleScope.launch {
            val user= AppUser(editEmail, editPassword)
            val response = authService.login(user)

            if (response.code()==400){
                Toast.makeText(context, "El usuario no se encuentra autorizado", Toast.LENGTH_SHORT).show()
                cancel()
            }
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    createSessionPreferences(loginResponse.token,loginResponse.usuario)
                    Toast.makeText(context, "Se ha iniciado sesión correctamente", Toast.LENGTH_SHORT).show()
                    goToActiveFragment()
                }
            } else {
                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
            cancel()
        }
    }

    private fun goToActiveFragment(){
        val newFragment=ActiveFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.add(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.commit()
    }

    private fun goToRegisterFragment(){
        val newFragment=RegisterFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.add(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}