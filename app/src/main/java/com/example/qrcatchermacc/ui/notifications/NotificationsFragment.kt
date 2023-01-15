package com.example.qrcatchermacc.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
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
    private val binding get() = _binding!!

    private var layoutManager: RecyclerView.LayoutManager?= null
    private var adapter: RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder>?= null

    var gameIds : MutableList<String?> = ArrayList<String?>()
    var nameObject : MutableList<String?> = ArrayList<String?>()
    var description : MutableList<String?> = ArrayList<String?>()
    var qrImages : MutableList<String?> = ArrayList<String?>()



    private val auth by lazy {
        FirebaseAuth.getInstance()
    }


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

        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        getWinnedGames()

        return root
    }

    fun getWinnedGames(){
        val username = SavedPreference.getUsername(requireContext())

        var url= "https://bbooss97.pythonanywhere.com/retrieve?data=$username"
        Log.d("zzzzzzzzzzz",url)
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Do something with the response
                Log.d("ZZZZZZZZZZZZZZZZZZZ",response.toString())
                if (response.length!=2){
                    var newResp = response.slice(2 until response.length-2)
                    var lista = newResp.split("), (")

                    for (partita in lista){
                        var valori=partita.split(',')
                        gameIds.add(valori[1])
                        nameObject.add(valori[2])
                        description.add(valori[3])
                        qrImages.add("https://cdn.pixabay.com/photo/2016/03/31/14/37/check-mark-1292787__340.png")

                    }
                    Log.d("vvv",gameIds.toString())
                    Log.d("vvv",nameObject.toString())
                    Log.d("vvv",description.toString())

                    try {
                            adapter = ProfileRecyclerAdapter(gameIds, nameObject, description, qrImages)
                            binding.recyclerView.adapter = adapter
                        }catch(e: Exception){    }
                    }


            },
            {error ->
                // Handle error
                Log.d("error",error.toString())
            })


        queue.add(stringRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        gameIds.clear()
        nameObject.clear()
        description.clear()
        qrImages.clear()
    }
}