package com.example.financialplannerapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentSettingsBinding
import com.example.financialplannerapp.utils.NetworkUtils

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set all texts programmatically
        binding.settingsTitle.text = "Pengaturan"

        // Card 1: Profil Pengguna
        binding.profileSettingsTitle.text = "Profil Pengguna"
        binding.profileSettingsDescription.text = "Lihat dan ubah profil pengguna Anda"
        binding.profileSettingsSubdesc.text = "[Offline SQLite] Melihat data, [Online Required] untuk sinkronisasi perubahan"

        // Card 2: Profil Pengguna (duplicate card)
        binding.profileSettingsCard2.findViewById<TextView>(R.id.profileSettingsTitle)?.text = "Profil Pengguna"
        binding.profileSettingsCard2.findViewById<TextView>(R.id.profileSettingsDescription)?.text = "Melihat data, sinkronisasi perubahan"
        binding.profileSettingsCard2.findViewById<TextView>(R.id.profileSettingsSubdesc)?.text = "[Offline SQLite] Melihat data, [Online Required] untuk sinkronisasi"

        // Card 3: Keamanan
        binding.securitySettingsCard.findViewById<TextView>(R.id.securitySettingsTitle)?.text = "Keamanan"
        binding.securitySettingsCard.findViewById<TextView>(R.id.securitySettingsDescription)?.text = "Atur PIN/Biometrik lokal"
        binding.securitySettingsCard.findViewById<TextView>(R.id.securitySettingsSubdesc)?.text = "[Offline SQLite] Atur PIN/Biometrik lokal"

        // Card 4: Pengaturan Aplikasi
        binding.appSettingsCard.findViewById<TextView>(R.id.appSettingsTitle)?.text = "Pengaturan Aplikasi"
        binding.appSettingsCard.findViewById<TextView>(R.id.appSettingsDescription)?.text = "Tema, Bahasa, Mata Uang Default, Notifikasi"
        binding.appSettingsCard.findViewById<TextView>(R.id.appSettingsSubdesc)?.text = "[Offline SQLite] Tema, Bahasa, Mata Uang Default, Notifikasi Lokal"

        // Card 5: Sinkronisasi Data
        binding.dataSyncCard.findViewById<TextView>(R.id.dataSyncTitle)?.text = "Sinkronisasi Data"
        binding.dataSyncCard.findViewById<TextView>(R.id.dataSyncDescription)?.text = "Status Sinkronisasi dan Sinkronkan Sekarang"
        binding.dataSyncCard.findViewById<TextView>(R.id.dataSyncSubdesc)?.text = "[Online Required] Cek koneksi dan sinkronisasi data"

        // Card 6: Backup & Restore Lokal
        binding.backupRestoreCard.findViewById<TextView>(R.id.backupRestoreTitle)?.text = "Backup & Restore Lokal"
        binding.backupRestoreCard.findViewById<TextView>(R.id.backupRestoreDescription)?.text = "Backup dan restore data dari berbagai sumber"
        binding.backupRestoreCard.findViewById<TextView>(R.id.backupRestoreSubdesc)?.text = "[Offline SQLite] Lokal, [Online Required] untuk cloud"

        // Card 7: Pusat Bantuan / FAQ
        binding.helpCenterCard.findViewById<TextView>(R.id.helpCenterTitle)?.text = "Pusat Bantuan / FAQ"
        binding.helpCenterCard.findViewById<TextView>(R.id.helpCenterDescription)?.text = "Informasi bantuan dan pertanyaan umum"
        binding.helpCenterCard.findViewById<TextView>(R.id.helpCenterSubdesc)?.text = "[Offline SQLite/Online Required] Konten bisa diunduh"

        // Card 8: Hubungi Kami / Laporkan Masalah
        binding.contactUsCard.findViewById<TextView>(R.id.contactUsTitle)?.text = "Hubungi Kami / Laporkan Masalah"
        binding.contactUsCard.findViewById<TextView>(R.id.contactUsDescription)?.text = "Kirim feedback atau laporkan masalah"
        binding.contactUsCard.findViewById<TextView>(R.id.contactUsSubdesc)?.text = "[Online Required] Mengirim feedback ke server"

        // App version
        binding.root.findViewById<TextView>(R.id.appVersionText)?.text = "Versi Aplikasi: 1.0.0"

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // User Profile settings card
        binding.profileSettingsCard.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_userProfileFragment)
        }

        // Security settings card
        binding.securitySettingsCard.setOnClickListener {
            // TODO: Navigate to security settings when implemented
            showFeatureNotImplementedToast("Pengaturan Keamanan")
        }

        // App settings card
        binding.appSettingsCard.setOnClickListener {
            // TODO: Navigate to app settings when implemented
            showFeatureNotImplementedToast("Pengaturan Aplikasi")
        }

        // Data sync card
        binding.dataSyncCard.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                // TODO: Navigate to data sync screen when implemented
                showFeatureNotImplementedToast("Sinkronisasi Data")
            } else {
                Toast.makeText(requireContext(), "Koneksi internet diperlukan untuk sinkronisasi data", Toast.LENGTH_SHORT).show()
            }
        }

        // Backup & restore card
        binding.backupRestoreCard.setOnClickListener {
            // TODO: Navigate to backup & restore screen when implemented
            showFeatureNotImplementedToast("Backup & Restore")
        }

        // Help center card
        binding.helpCenterCard.setOnClickListener {
            // TODO: Navigate to help center when implemented
            showFeatureNotImplementedToast("Pusat Bantuan")
        }

        // Contact us card
        binding.contactUsCard.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                // TODO: Navigate to contact screen when implemented
                showFeatureNotImplementedToast("Hubungi Kami")
            } else {
                Toast.makeText(requireContext(), "Koneksi internet diperlukan untuk mengirim feedback", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFeatureNotImplementedToast(featureName: String) {
        Toast.makeText(requireContext(), "$featureName akan segera hadir!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}