package com.example.qrcatchermacc.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.qrcatchermacc.Catch
import com.example.qrcatchermacc.LoginScreen
import com.example.qrcatchermacc.R
import com.example.qrcatchermacc.SavedPreference
import com.example.qrcatchermacc.databinding.FragmentNotificationsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //val button = view.findViewById<Button>(R.id.button)
        binding.startGame.setOnClickListener {
            // Code to execute when the button is clicked
            // Create an Intent to start the target activity
            val intent = Intent(requireActivity(), Catch::class.java)

            // Start the target activity
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)
        binding.usernameShown.text = SavedPreference.getEmail(requireActivity())
        val acct = GoogleSignIn.getLastSignedInAccount(requireActivity())

    
        val imageUrl = acct?.photoUrl.toString()
        SavedPreference.setImage(requireContext(), imageUrl)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.immagine)

        binding.logout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent= Intent(requireActivity(), LoginScreen::class.java)
                Toast.makeText(requireActivity(),"Logging Out", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                requireActivity().finish()
            }
        }
        
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}