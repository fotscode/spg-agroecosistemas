package com.example.spgunlp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentProfileBinding
import com.example.spgunlp.io.UserService
import com.example.spgunlp.model.Perfil
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.login.LoginFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment() {
    private val userService: UserService by lazy {
        UserService.create()
    }

    private lateinit var mProfileViewModel: ProfileViewModel

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mProfileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        populateProfile()

        binding.btnCerrarSesion.setOnClickListener() {
            performLogout()
        }

        return root
    }

    private fun populateProfile() {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val email = preferences["email", ""]
        mProfileViewModel.getPerfilByEmail(email).also { it ->
            it.observe(viewLifecycleOwner) { perfil ->
                if (perfil != null) {
                    fillProfile(perfil)
                } else {
                    lifecycleScope.launch {
                        val jwt = preferences["jwt", ""]
                        if (!jwt.contains("."))
                            cancel()
                        val header = "Bearer $jwt"
                        val perfil = getProfile(header, email)
                        Log.i("ProfileFragment", "populateProfile: $perfil")
                        if (perfil != null) {
                            fillProfile(perfil)
                            mProfileViewModel.addPerfil(perfil)
                        }else{
                            binding.profileData.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performLogout() {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        preferences["jwt"] = ""
        preferences["email"] = ""
        preferences["LAST_UPDATE"] = 0L
        preferences["UPDATE_PRINCIPLES"] = 0L
        goToLoginFragment()
    }

    private fun goToLoginFragment() {
        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_active
        val newFragment = LoginFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.commit()
    }
    private suspend fun getProfile(header: String, email: String): Perfil? {
        try {
            val response = userService.getUsers(header)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Log.i("ProfileFragment", "getProfile: made api call and was successful")
                val user = body.filter { it.email == email }[0]
                val nombre = user.nombre ?: "Sin Nombre"
                val posicion = user.posicionResponse?.nombre ?: "Sin Posición"
                val celular = user.celular ?: "Sin Celular"
                val organizacion = user.organizacion ?: "Sin Organización"
                val rol = user.roles?.get(0)?.nombre ?: ""
                return Perfil(
                    email,
                    nombre,
                    posicion,
                    celular,
                    organizacion,
                    rol
                )
            } else if (response.code() == 401) {
                Toast.makeText(
                    requireContext(),
                    "El token ha expirado, porfavor reiniciar sesión o sincronizar",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: rehacer sincronizar, con los datos
                return null
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "No se pudo obtener el perfil, verificar la conexión a internet",
                Toast.LENGTH_SHORT
            ).show()
        }
        Log.i("ProfileFragment", "getProfile: made api call and was not successful")
        return null
    }

    private fun fillProfile(perfil: Perfil){
        binding.profileName.text = perfil.nombre
        binding.profilePosition.text = perfil.posicion
        binding.profileEmail.text = perfil.email
        binding.profileCellphone.text = perfil.celular
        binding.profileOrganization.text = perfil.organizacion
        binding.profileRole.text = if (perfil.rol == "ROLE_ADMIN") "Administrador" else "Usuario"
    }
}