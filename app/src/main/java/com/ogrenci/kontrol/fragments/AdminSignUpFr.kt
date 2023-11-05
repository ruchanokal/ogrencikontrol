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
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ogrenci.kontrol.activity.MainActivity
import com.ogrenci.kontrol.databinding.FragmentAdminSignInBinding
import com.ogrenci.kontrol.databinding.FragmentAdminSignUpBinding
import com.ogrenci.kontrol.databinding.LayoutCustomDialogBinding


class AdminSignUpFr : Fragment() {

    private var _binding : FragmentAdminSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val TAG = "AdminSignUpFr"
    private lateinit var mAuth: FirebaseAuth
    var reference: ListenerRegistration? = null
    var reference2 : ListenerRegistration? = null
    var userUid = ""
    private lateinit var email: String
    private lateinit var kullaniciAdi: String
    private var sifre: String = ""
    private var sifre2: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminSignUpBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.kayitOlButton.setOnClickListener {

            binding.progressBarSignUp.visibility = View.VISIBLE

            email = binding.editTextEmailKayit.text.toString()
            kullaniciAdi = binding.editTextKullaniciAdiKayit.text.toString()
            sifre = binding.editTextSifreKayit.text.toString()
            sifre2 = binding.editTextSifreKayit2.text.toString()

            databaseCollection()

        }
    }

    private fun databaseCollection() {

        reference = db.collection("Admin")
            .whereEqualTo("kullaniciAdi", kullaniciAdi).addSnapshotListener { value, error ->

                Log.i(TAG, "kullaniciAdi: " + kullaniciAdi)

                if (value != null) {

                    Log.i(TAG, "null değil")

                    if (!value.isEmpty) {
                        Log.i(TAG, "empty değil")

                        Toast.makeText(
                            context, "Lütfen başka bir kullanıcı adı deneyin!",
                            Toast.LENGTH_LONG
                        ).show()

                        binding.progressBarSignUp.visibility = View.GONE

                        reference?.remove()

                    } else {
                        Log.i(TAG, "empty")
                        kontroller()
                    }

                } else {
                    Log.i(TAG, "null")
                    kontroller()
                }

                if (error != null)
                    Log.i(TAG, "error: " + error)

            }
    }

    private fun kontroller() {


        if (email.equals("")
            || kullaniciAdi.equals("")
            || sifre.equals("")
        ) {

            reference?.remove()

            Toast.makeText(activity, "Lütfen gerekli alanları doldurunuz", Toast.LENGTH_LONG).show()

            binding.progressBarSignUp.visibility = View.GONE

        } else if (!sifre.equals(sifre2)) {

            reference?.remove()

            Toast.makeText(activity, "Şifreler aynı olmalıdır!", Toast.LENGTH_LONG).show()

            binding.progressBarSignUp.visibility = View.GONE

        } else {

            binding.progressBarSignUp.visibility = View.GONE
            val hashMap = hashMapOf<Any, Any>()

            email.let { hashMap.put("email", it) }
            kullaniciAdi.let { hashMap.put("kullaniciAdi", it) }
            hashMap.put("izinVerildiMi",false)

            reference?.remove()

            db.collection("Admin").document(email).set(hashMap).addOnSuccessListener {

                Log.i(TAG,"admin database'e eklendi")
                makeDialog()

            }.addOnFailureListener {

                Log.e(TAG,"kullanıcı oluşturulamadı yeniden deneyin!")

            }



        }
    }

    private fun makeDialog() {


        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val dialogBinding : LayoutCustomDialogBinding = LayoutCustomDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        reference2 = db.collection("Admin").document(email).addSnapshotListener { value, error ->

            if (value !=null) {

                if (value.exists()) {

                    val izinVerildiMi = value.get("izinVerildiMi") as Boolean?
                    Log.i(TAG,"izinVerildiMi: " + izinVerildiMi)

                    if (izinVerildiMi == true){
                        if (dialog != null && dialog.isShowing) {
                            dialog.dismiss()
                            kayitOl()
                        }
                    }


                } else {
                    Log.i(TAG,"empty")
                    reference2?.remove()
                }

            } else {
                Log.i(TAG,"null")
                reference2?.remove()
            }

            if (error!= null)
                Log.i(TAG,"error: " + error)

        }

    }

    private fun kayitOl() {

        mAuth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                reference2?.remove()

                binding.progressBarSignUp.visibility = View.GONE

                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("kullaniciAdi", kullaniciAdi)
                intent.putExtra("definite", 1)
                startActivity(intent)
                requireActivity().finish()

                Toast.makeText(activity, "Hoşgeldin ${kullaniciAdi}", Toast.LENGTH_LONG)
                    .show()

            }

        }.addOnFailureListener { exception ->


            try {
                throw exception
            } catch (e: FirebaseAuthUserCollisionException) {

                reference2?.remove()

                Toast.makeText(
                    activity,
                    "Bu e-posta adresi zaten başka bir hesap tarafından kullanılıyor",
                    Toast.LENGTH_LONG
                ).show()
                binding.progressBarSignUp.visibility = View.GONE

            } catch (e: FirebaseAuthWeakPasswordException) {

                reference2?.remove()

                Toast.makeText(
                    activity,
                    "Lütfen en az 6 haneli bir şifre giriniz",
                    Toast.LENGTH_LONG
                ).show()
                binding.progressBarSignUp.visibility = View.GONE

            } catch (e: FirebaseNetworkException) {

                reference2?.remove()

                Toast.makeText(
                    activity,
                    "Lütfen internet bağlantınızı kontrol edin",
                    Toast.LENGTH_LONG
                ).show()
                binding.progressBarSignUp.visibility = View.GONE

            } catch (e: FirebaseAuthInvalidCredentialsException) {

                reference2?.remove()

                Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
                binding.progressBarSignUp.visibility = View.GONE

            } catch (e: Exception) {

                reference2?.remove()

                Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
                binding.progressBarSignUp.visibility = View.GONE
            }

        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}