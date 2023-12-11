package com.example.spgunlp.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigator
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentProfileBinding
import com.example.spgunlp.io.AuthService
import com.example.spgunlp.io.UserService
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.LAST_UPDATE_PROFILE
import com.example.spgunlp.model.MODIFIED_VISIT
import com.example.spgunlp.model.PROFILE
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.active.ActiveFragment
import com.example.spgunlp.ui.login.LoginFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.example.spgunlp.util.getVisits
import com.example.spgunlp.util.updateRecycler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date

class ProfileFragment : BaseFragment() {
    private val userService: UserService by lazy {
        UserService.create()
    }

    private lateinit var user: AppUser

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textProfile
        //profileViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        populateProfile()

        binding.btnCerrarSesion.setOnClickListener() {
            performLogout()
        }

        return root
    }

    private fun populateProfile() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header = "Bearer $jwt"
            user = getProfile(header)
            Log.i("ProfileFragment", "populateProfile: $user")
            binding.profileName.text = if (user.nombre?.isBlank() == false) user.nombre else "Sin nombre"
            binding.profilePosition.text =
                if (user.posicionResponse?.nombre?.isBlank()==false) user.posicionResponse?.nombre else "Sin posicion"
            binding.profileEmail.text = if (user.email?.isBlank()==false) user.email else "Sin email"
            binding.profileCellphone.text =if (user.celular?.isBlank()==false) user.celular else "Sin celular"
            binding.profileOrganization.text = if (user.organizacion?.isBlank()==false) user.organizacion else "Sin organizacion"
            if (user.roles == null || user.roles!!.isEmpty())
                cancel()
            val role = user.roles?.get(0)?.nombre
            binding.profileRole.text = if (role == "ROLE_ADMIN") "Administrador" else "Usuario"
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
        preferences["LIST_VISITS"] = ""
        preferences["LAST_UPDATE"] = 0L
        preferences[PROFILE] = ""
        preferences[LAST_UPDATE_PROFILE] = 0L
        preferences["PRINCIPLES"] = ""
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

    private fun updatePreferences(user: AppUser) {
        val currentDate = Date().time
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val gson = Gson()
        val userGson = gson.toJson(user)
        preferences[PROFILE] = userGson
        preferences[LAST_UPDATE_PROFILE] = currentDate
    }

    fun getPreferences(context: Context): AppUser {
        val preferences = PreferenceHelper.defaultPrefs(context)
        val gson = Gson()
        val userGson = preferences[PROFILE, ""]
        if (userGson == ""){
            binding.profileData.visibility = View.GONE
            return AppUser(null, "", "", "", "", "", 1, null, null, null)
        }
        val type = object : TypeToken<AppUser>() {}.type
        return gson.fromJson(userGson, type)
    }

    suspend fun getProfile(header: String): AppUser {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val lastUpdate = PreferenceHelper.defaultPrefs(requireContext())[LAST_UPDATE_PROFILE, 0L]
        val currentDate = Date().time

        if (currentDate - lastUpdate < 600000) {// 10mins
            Log.i("ProfileFragment", "getProfile: last update less than 10 mins")
            user = getPreferences(requireContext())
            return user
        }

        try {
            val response = userService.getUsers(header)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val email = preferences["email", ""]
                user = body!!.filter { it.email == email }[0]
                updatePreferences(user)
                Log.i("ProfileFragment", "getUser: made api call and was successful")
            } else if (response.code() == 401 || response.code() == 403) {
                user = getPreferences(requireContext())
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", e.message.toString())
            user = getPreferences(requireContext())
        }
        return user
    }
}