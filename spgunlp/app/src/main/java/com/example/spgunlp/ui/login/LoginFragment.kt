package com.example.spgunlp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.MainActivity
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentLoginBinding
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.active.ActiveFragment


class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    override var bottomNavigationViewVisibility = View.GONE

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        binding.btnIniciarSesion.setOnClickListener(){
            if (isCorrectLogin()){
                val newFragment=ActiveFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment_activity_main, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }else{
                Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isCorrectLogin():Boolean{
        // TODO: Implementar la lógica de login
        return true
    }
}