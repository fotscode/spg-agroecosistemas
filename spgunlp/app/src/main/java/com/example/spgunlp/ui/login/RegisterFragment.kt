package com.example.spgunlp.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentRegisterBinding
import com.example.spgunlp.io.AuthService
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.login.LoginFragment
import com.example.spgunlp.ui.login.RegisterViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment() {

    val mapItemInteger = mapOf("Consumidor" to 1, "Equipo tecnico" to 2, "Productor/a" to 3, "Representante de organizacion" to 4)

    private val authService: AuthService by lazy {
        AuthService.create()
    }

    override var bottomNavigationViewVisibility = View.GONE
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registerViewModel =
            ViewModelProvider(this).get(RegisterViewModel::class.java)

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val items=mapItemInteger.keys.toList()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.autoCompleteTextView.setAdapter(adapter)


        binding.btnInicioSesion.setOnClickListener(){
            goToLoginFragment()
        }

        binding.btnRegistrar.setOnClickListener(){
            performRegister()
        }

        return root
    }

    private fun goToLoginFragment(){
        val transaction = parentFragmentManager.beginTransaction()
        val newFragment = LoginFragment()
        transaction.add(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.remove(this)
        transaction.commit()
    }

    private fun performRegister(){
        //val editEmail=binding.editMail.text.toString()
        val editPassword=binding.editPassword.text.toString()
        val editName=binding.editName.text.toString()
        val editCellphone=binding.editCellphone.text.toString()
        val editOrganization=binding.editOrganization.text.toString()
        val position=mapItemInteger[binding.autoCompleteTextView.text.toString()]

        // make the call to the remote API with coroutines
        lifecycleScope.launch {
            val user= AppUser("", editPassword)
            val response = authService.registro(user)

            if (response.isSuccessful) {
                Toast.makeText(context, "Se ha creado el usuario correctamente", Toast.LENGTH_SHORT).show()
                goToLoginFragment()
            } else {
                Toast.makeText(context, "El mail ya se encuentra registrado", Toast.LENGTH_SHORT).show()
            }
            cancel()
        }

    }

}