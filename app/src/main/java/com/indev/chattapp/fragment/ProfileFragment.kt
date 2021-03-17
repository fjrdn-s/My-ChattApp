package com.indev.chattapp.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.indev.chattapp.model.Users
import com.indev.chattapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    var userReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageReference: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        storageReference = FirebaseStorage.getInstance().reference.child("user images")

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)

                    if (context != null) {
                        view.username_setting.text = user!!.getUsername()
                        Picasso.get().load(user.getProfile()).into(view.profile_img_setting)
                        Picasso.get().load(user.getCover()).into(view.cover_img_setting)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //view for cover and photo user
        view.profile_img_setting.setOnClickListener {
            coverChecker = "profile"
            pickImage()
        }

        view.cover_img_setting.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        //view for social media
        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLink()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLink()
        }

        view.set_url.setOnClickListener {
            socialChecker = "url"
            setSocialLink()
        }

        return view
    }

    private fun setSocialLink() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker == "facebook") {
            builder.setTitle("Add your link facebook : ")
        }
        else if (socialChecker == "instagram") {
            builder.setTitle("Add your link instgram : ")
        }
        else {
            builder.setTitle("Add your link website : ")
        }

        val editText = EditText(context)
        if (socialChecker == "url") {
            editText.hint = "e.g www.google.com"
        }
        else if (socialChecker == "facebook") {
            editText.hint = "e.g www.facebook.com/..."
        }
        else {
            editText.hint = "e.g www.instagram.com/..."
        }

        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val string = editText.text.toString()
            if (string == "") {
                Toast.makeText(context, "Please write something...", Toast.LENGTH_LONG).show()
            }else{
                saveSocialLink(string)
            }
        })
        
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(string: String) {
        val mapSocial = HashMap<String, Any>()

        when(socialChecker) {
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$string"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$string"
            }
            "url" -> {
                mapSocial["url"] = "https://$string"
            }
        }

        userReference!!.updateChildren(mapSocial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "update Successfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "uploading...", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()

        if (imageUri != null) {
            val fileReferences = storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileReferences.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri> >{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileReferences.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {

                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    } else {

                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

}
