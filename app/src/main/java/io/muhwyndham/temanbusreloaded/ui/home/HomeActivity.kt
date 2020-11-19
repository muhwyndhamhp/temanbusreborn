package io.muhwyndham.temanbusreloaded.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.muhwyndham.temanbusreloaded.R
import io.muhwyndham.temanbusreloaded.base.MainActivity

class HomeActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}