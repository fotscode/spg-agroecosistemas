package com.example.spgunlp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigator
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentProfileBinding
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.active.ActiveFragment
import com.example.spgunlp.ui.login.LoginFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : BaseFragment() {

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

        val textView: TextView = binding.textProfile
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnCerrarSesion.setOnClickListener(){
            performLogout()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performLogout(){
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        preferences["jwt"] = ""
        preferences["email"] = ""
        preferences["LIST_VISITS"] = ""
        preferences["LAST_UPDATE"] = 0L
        goToLoginFragment()
    }

    private fun goToLoginFragment(){
        val bottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_active
        val newFragment = LoginFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.commit()
    }
}