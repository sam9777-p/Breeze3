package com.example.breeze

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class pfp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var profileImageView: ImageView


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                // Save and Load the Profile Picture
                saveProfilePictureUri(currentUserId, it)
                loadProfilePicture(currentUserId, profileImageView)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pfp)

        auth = FirebaseAuth.getInstance()


        val btn = findViewById<Button>(R.id.btnx)
        profileImageView = findViewById(R.id.profileImageView)

        btn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }


        profileImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")  // Open the gallery to pick an image
        }

        // Load the profile picture when the activity is created
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let {
            loadProfilePicture(it, profileImageView)
        }
    }


    private fun saveProfilePictureUri(userId: String, profilePicUri: Uri) {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("${userId}_profilePicUri", profilePicUri.toString())
        editor.apply()  // Save asynchronously
    }


    private fun loadProfilePicture(userId: String, profileImageView: ImageView) {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val profilePicUri = sharedPreferences.getString("${userId}_profilePicUri", null)

        if (profilePicUri != null) {
            Glide.with(this)
                .load(Uri.parse(profilePicUri))
                .placeholder(R.drawable.baseline_person_24)
                .circleCrop()
                .into(profileImageView)
        }
    }
}
