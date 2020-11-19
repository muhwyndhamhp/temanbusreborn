package io.muhwyndham.temanbusreloaded.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.material.snackbar.Snackbar
import io.muhwyndham.temanbusreloaded.base.MainActivity
import io.muhwyndham.temanbusreloaded.databinding.ActivityRegisterBinding
import io.muhwyndham.temanbusreloaded.models.data.RegisterRequest
import io.muhwyndham.temanbusreloaded.ui.auth.AuthViewModel
import io.muhwyndham.temanbusreloaded.ui.home.HomeActivity
import io.muhwyndham.temanbusreloaded.utils.Constants.LOADING
import io.muhwyndham.temanbusreloaded.utils.Constants.NOTHING
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertConfirm
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertEmail
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertName
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertPassword
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertPhone
import io.muhwyndham.temanbusreloaded.utils.Extensions.trimPhoneNumber

class RegisterActivity : MainActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityRegisterBinding

    private var emailValid = false
    private var passwordValid = false
    private var confirmPasswordValid = false
    private var phoneValid = false
    private var nameValid = false
    private var profileImage: Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        attachEditTextAssertion()
        attachLiveDataObserver()
        attachButtonListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            profileImage = ImagePicker.getFirstImageOrNull(data)
            Glide.with(this).load(profileImage?.uri).into(binding.profileLayout.ivProfilePict)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun attachLiveDataObserver() {
        authViewModel.appState.observe(this, {
            if (it.stateCode == LOADING) {
                binding.overlayProgress.visibility = View.VISIBLE
                binding.progressBar.visibility = View.VISIBLE
                toggleAllElement(false)
            } else {
                binding.overlayProgress.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                toggleAllElement(true)
            }

            if (it.stateCode != NOTHING)
                Snackbar.make(binding.root, it.message as CharSequence, Snackbar.LENGTH_SHORT)
                    .show()
        })

        authViewModel.currentUser.observe(this, {
            if (it?.uid != null) startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                )
            ).also { finish() }
        })
    }

    private fun toggleAllElement(isEnable: Boolean) {
        binding.apply {
            etEmail.isEnabled = isEnable
            etPhone.isEnabled = isEnable
            etName.isEnabled = isEnable
            etPasswordConfirm.isEnabled = isEnable
            etPassword.isEnabled = isEnable
            btnRegister.isEnabled = isEnable
            ivBack.isEnabled = isEnable
            profileLayout.cvSelectPict.isEnabled = isEnable

        }
    }

    private fun attachButtonListener() {
        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.profileLayout.cvSelectPict.setOnClickListener {
            ImagePicker.create(this)
                .toolbarFolderTitle("Pilih foto profil")
                .single()
                .start()
        }
        binding.btnRegister.setOnClickListener {
            val isAllValidated = emailValid
                    && passwordValid
                    && nameValid
                    && confirmPasswordValid
                    && phoneValid

            if (isAllValidated) {
                val registerRequest = RegisterRequest(
                    binding.etName.text.toString(),
                    profileImage,
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etPhone.text.toString().trimPhoneNumber(),
                )
                authViewModel.registerWithEmail(registerRequest)
            }
        }
    }

    private fun attachEditTextAssertion() {
        binding.etEmail.assertEmail { isValid, reason, _ ->
            binding.tilEmail.error = if (!isValid) reason else null
            emailValid = isValid
            updateRegisterButton()
        }

        binding.etPassword.assertPassword { isValid, reason, _ ->
            binding.tilPassword.error = if (!isValid) reason else null
            passwordValid = isValid
            updateRegisterButton()
        }
        binding.etPasswordConfirm.assertConfirm(binding.etPassword) { isValid, reason ->
            binding.tilPasswordConfirm.error = if (!isValid) reason else null
            confirmPasswordValid = isValid
            updateRegisterButton()
        }
        binding.etName.assertName { isValid, reason, _ ->
            binding.tilName.error = if (!isValid) reason else null
            nameValid = isValid
            updateRegisterButton()
        }
        binding.etPhone.assertPhone { isValid, reason, _ ->
            binding.tilPhone.error = if (!isValid) reason else null
            phoneValid = isValid
            updateRegisterButton()
        }
    }

    private fun updateRegisterButton() {
        binding.btnRegister.isEnabled = emailValid
                && passwordValid
                && nameValid
                && confirmPasswordValid
                && phoneValid
    }
}