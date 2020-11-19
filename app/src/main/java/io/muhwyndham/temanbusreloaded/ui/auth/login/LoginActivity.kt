package io.muhwyndham.temanbusreloaded.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import io.muhwyndham.temanbusreloaded.base.MainActivity
import io.muhwyndham.temanbusreloaded.databinding.ActivityLoginBinding
import io.muhwyndham.temanbusreloaded.ui.auth.AuthViewModel
import io.muhwyndham.temanbusreloaded.ui.auth.register.RegisterActivity
import io.muhwyndham.temanbusreloaded.ui.home.HomeActivity
import io.muhwyndham.temanbusreloaded.utils.Constants.LOADING
import io.muhwyndham.temanbusreloaded.utils.Constants.NOTHING
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertEmail
import io.muhwyndham.temanbusreloaded.utils.Extensions.assertPassword

class LoginActivity : MainActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private var emailValid = false
    private var passwordValid = false

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachEditTextAssertion()
        attachButtonListener()
        attachLiveDataObserver()
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

    private fun attachButtonListener() {
        binding.btnLogin.setOnClickListener {
            if (emailValid && passwordValid)
                authViewModel.authWithEmail(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            else {
                if (!emailValid) binding.tilEmail.error =
                    "Email harus valid dan tidak boleh kosong!"
                else binding.tilEmail.error = null

                if (!passwordValid) binding.tilPassword.error = "Password minimal 8 karakter!"
                else binding.tilPassword.error = null
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attachEditTextAssertion() {
        binding.etEmail.assertEmail { isValid, reason, email ->
            if (!isValid) binding.tilEmail.error = reason
            else binding.tilEmail.error = null

            emailValid = isValid
            updateLoginButton()
        }

        binding.etPassword.assertPassword { isValid, reason, password ->
            if (!isValid) binding.tilPassword.error = reason
            else binding.tilPassword.error = null

            passwordValid = isValid
            updateLoginButton()
        }
    }

    private fun toggleAllElement(isEnable: Boolean) {
        binding.apply {
            btnLogin.isEnabled = isEnable
            btnRegister.isEnabled = isEnable
            etEmail.isEnabled = isEnable
            etPassword.isEnabled = isEnable
        }
    }

    private fun updateLoginButton() {
        binding.btnLogin.isEnabled = emailValid && passwordValid
    }
}
