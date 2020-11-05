package io.muhwyndham.temanbusreloaded.base

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class MainActivity : AppCompatActivity() {

    val mainViewModel: MainViewModel by viewModels()

}