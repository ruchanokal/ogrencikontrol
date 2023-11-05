package com.ogrenci.kontrol.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ogrenci.kontrol.activity.MainActivity
import com.ogrenci.kontrol.databinding.FragmentAdminSignInBinding
import com.ogrenci.kontrol.databinding.LayoutCustomDialogBinding


class AdminSignInFr : Fragment() {

    private var _binding : FragmentAdminSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var reference: ListenerRegistration? = null
    var reference2: ListenerRegistration? = null
    private var emailList = arrayListOf<String>()
    private val TAG = "AdminSignInFr"
    private var dialog : Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminSignInBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (mAuth.currentUser != null) {

            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("definite", 2)
            startActivity(intent)
            requireActivity().finish()

        }

        binding.girisYapButton.setOnClickListener { signIn() }

        binding.kayitOlText.setOnClickListener {

            val action = AdminSignInFrDirections.actionAdminSignInFrToAdminSignUpFr()
            Navigation.findNavController(it).navigate(action)

        }

        binding.sifremiUnuttumText.setOnClickListener {

            val action = AdminSignInFrDirections.actionAdminSignInFrToForgetPasswordFr()
            Navigation.findNavController(it).navigate(action)

        }


        backButton()


    }

    private fun signIn() {

        binding.progressBarSignIn.visibility = View.VISIBLE
        binding.progressBarSignIn.translationZ = 2F
        binding.progressBarSignIn.elevation = 10F

        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextSifre.text.toString()

        if (email.equals("") && password.equals("")) {

            Toast.makeText(
                activity, "Lütfen gerekli alanları doldurunuz!",
                Toast.LENGTH_LONG
            ).show()

            binding.progressBarSignIn.visibility = View.GONE

        } else if (password.equals("")) {

            Toast.makeText(
                activity, "Lütfen şifrenizi giriniz!",
                Toast.LENGTH_LONG
            ).show()

            binding.progressBarSignIn.visibility = View.GONE

        } else if (email.equals("")) {

            Toast.makeText(
                activity, "Lütfen kayıtlı e-posta adresinizi giriniz!",
                Toast.LENGTH_LONG
            ).show()

            binding.progressBarSignIn.visibility = View.GONE

        } else {

            Log.i(TAG,"emaill: " + email)

            reference2 = db.collection("Admin").document(email).addSnapshotListener { value, error ->

                if (error != null) {

                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    if (value != null) {

                        if (value.exists()){

                            val izinVerildiMi = value.get("izinVerildiMi") as Boolean?

                            Log.i(TAG,"izinVerildiMi: " + izinVerildiMi)

                            if (izinVerildiMi == true){

                                Log.i(TAG,"email: " + email)
                                Log.i(TAG,"password: " + password)

                                if (dialog != null && dialog!!.isShowing) {
                                    dialog!!.dismiss()
                                    emailListesiniKontrolEt(email,password)
                                } else {
                                    emailListesiniKontrolEt(email,password)
                                }
                            } else {
                                makeDialog()
                            }

                        } else {
                            Log.i(TAG,"value not exists")
                            emailListesiniKontrolEt(email,password)
                        }
                    }

                }

            }

        }
    }

    private fun makeDialog() {


        dialog = Dialog(requireContext())
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        val dialogBinding : LayoutCustomDialogBinding = LayoutCustomDialogBinding.inflate(layoutInflater)
        dialog!!.setContentView(dialogBinding.root)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()

    }

    private fun emailListesiniKontrolEt(email : String, password: String) {

        // email listesi buraya ait olup olmadığı için kontrol edilmiştir. Öğretmenden kayıt yapanlar
        // burdan giriş yapamaz

        reference2?.remove()

        val query = db.collection("Admin")

        reference = query.addSnapshotListener { value, error ->

            if (error != null) {

                Toast.makeText(
                    requireContext(),
                    "Error: ${error.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (value != null) {

                    if (!value.isEmpty) {

                        Log.i(TAG, "admin snapshotListener")

                        val documents = value.documents

                        for (document in documents) {

                            val testEmail = document.get("email") as String

                            emailList.add(testEmail)

                            if (testEmail.equals(email)) {

                                mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {

                                        if (it.isSuccessful) {

                                            reference?.remove()

                                            val intent = Intent(
                                                requireActivity(),
                                                MainActivity::class.java
                                            )
                                            intent.putExtra("definite", 2)
                                            startActivity(intent)
                                            requireActivity().finish()
                                            binding.progressBarSignIn.visibility = View.GONE

                                        } else {

                                            try {
                                                throw it.getException()!!
                                            } catch (e: FirebaseAuthUserCollisionException) {

                                                Log.e(TAG,"error1: " + e.localizedMessage)
                                                reference?.remove()

                                                Toast.makeText(
                                                    activity,
                                                    e.localizedMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                binding.progressBarSignIn.visibility =
                                                    View.GONE

                                            } catch (e: FirebaseAuthEmailException) {

                                                Log.e(TAG,"error2: " + e.localizedMessage)
                                                reference?.remove()

                                                Toast.makeText(
                                                    activity,
                                                    e.localizedMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                binding.progressBarSignIn.visibility =
                                                    View.GONE

                                            } catch (e: FirebaseAuthInvalidUserException) {

                                                Log.e(TAG,"error3: " + e.localizedMessage)
                                                reference?.remove()

                                                Toast.makeText(
                                                    activity,
                                                    "Bu e-posta ile eşleşen bir kullanıcı yok. Lütfen tekrar deneyin!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                binding.progressBarSignIn.visibility =
                                                    View.GONE

                                            } catch (e: FirebaseNetworkException) {

                                                Log.e(TAG,"error4: " + e.localizedMessage)
                                                reference?.remove()

                                                Toast.makeText(
                                                    activity,
                                                    "Lütfen internet bağlantınızı kontrol ediniz",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                binding.progressBarSignIn.visibility =
                                                    View.GONE

                                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                                Log.e(TAG,"error5: " + e.localizedMessage)

                                                reference?.remove()

                                                Toast.makeText(
                                                    activity,
                                                    e.localizedMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                binding.progressBarSignIn.visibility =
                                                    View.GONE

                                            } catch (e: Exception) {

                                                Log.e(TAG,"error6: " + e.localizedMessage)
                                                reference?.remove()

                                                kayitOl(email, password)
                                                binding.progressBarSignIn.visibility =
                                                    View.GONE

                                            }

                                        }

                                    }

                                break
                            } else
                                Log.i(TAG, "böyle bir email bulunamadı")


                        }

                        val distinct = emailList.toSet().toList()

                        Log.i(TAG, "distinct: " + distinct)
                        Log.i(TAG, "email: " + email)

                        if (distinct.size > 0 && !distinct.contains(email)) {

                            Log.i(TAG, "email-2: " + email)

                            reference?.remove()

                            binding.progressBarSignIn.visibility = View.GONE

                            Toast.makeText(
                                activity,
                                "Bu e-posta ile eşleşen bir kullanıcı yok. Lütfen tekrar deneyin!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } else {

                        reference?.remove()

                        Log.i(TAG, "email bulunamadı")

                        binding.progressBarSignIn.visibility = View.GONE
                        Toast.makeText(
                            activity,
                            "Bu e-posta ile eşleşen bir kullanıcı yok, lütfen tekrar deneyin!",
                            Toast.LENGTH_SHORT
                        ).show()


                    }


                } else {

                    reference?.remove()

                    Log.i(TAG, "data null")

                    binding.progressBarSignIn.visibility = View.GONE
                    Toast.makeText(
                        activity, "Böyle bir kullanıcı yok. Lütfen tekrar deneyin!",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }

        }

    }

    private fun kayitOl(email: String,sifre :String) {

        mAuth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->

            if (task.isSuccessful){

                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("definite", 2)
                startActivity(intent)
                requireActivity().finish()

                Toast.makeText(activity,"Hoşgeldiniz",Toast.LENGTH_LONG).show()

            }

        }.addOnFailureListener { exception ->

            Log.e(TAG,"exception: " + exception.localizedMessage)

        }

    }


    private fun backButton() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}