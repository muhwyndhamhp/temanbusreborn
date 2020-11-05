package io.muhwyndham.temanbusreloaded.base

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import io.muhwyndham.temanbusreloaded.remote.ListRepository

class MainViewModel @ViewModelInject constructor(private val listRepository : ListRepository) : ViewModel() {
}

