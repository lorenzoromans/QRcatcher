package com.example.qrcatchermacc.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
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

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)

        binding.usernameShown.text = SavedPreference.getUsername(requireActivity())
        binding.emailShown.text = SavedPreference.getEmail(requireActivity())

        val acct = GoogleSignIn.getLastSignedInAccount(requireActivity())

    
        val imageUrl = acct?.photoUrl.toString()
        SavedPreference.setImage(requireContext(), imageUrl)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.immagine)


        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        getWinnedGames(0)

        return root
    }
    


    fun getWinnedGames(rec:Int){
        if (rec>=5){ return  }

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

                        gameIds.add(valori[1].slice(2 until valori[1].length-1))
                        nameObject.add(valori[2].slice(2 until valori[2].length-1))
                        description.add(valori[3].slice(2 until valori[3].length-1))
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
                getWinnedGames(rec+1)
                Log.d("error",error.toString())
            })
        //stringRequest.retryPolicy = DefaultRetryPolicy(10, 5, 2F)
        queue.add(stringRequest)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.logout -> {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent= Intent(requireActivity(), LoginScreen::class.java)
                Toast.makeText(requireActivity(),"Logging Out", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                requireActivity().finish()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
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