
    package com.example.financialplannerapp.ui.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.example.financialplannerapp.data.repository.WalletRepository

    class WalletViewModelFactory(
        private val walletRepository: WalletRepository,
        private val userId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(walletRepository, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }