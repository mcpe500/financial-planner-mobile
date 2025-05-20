package com.example.financialplannerapp.fragments.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import com.example.financialplannerapp.utils.SecurityUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentPasscodeBinding

class PasscodeFragment : Fragment() {
    private var _binding: FragmentPasscodeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    private var passcode = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasscodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passcodeButtons: List<Button> = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )

        val passcodeRadios: List<RadioButton> = listOf(
            binding.radio1,
            binding.radio2,
            binding.radio3,
            binding.radio4,
            binding.radio5,
            binding.radio6
        )

        for (button: Button in passcodeButtons) {
            button.setOnClickListener {
                if (passcode.length < 6) {
                    passcode += button.text.toString()
                    passcodeRadios[passcode.length - 1].isChecked = true
                }
                if (passcode.length == 6) {
                    // Handle passcode submission
                    handleSubmitPasscode(passcode)
                }
            }
        }

        for (radio: RadioButton in passcodeRadios) {
            radio.setOnTouchListener { _, _ -> true } // Make radios not directly touchable
        }

        binding.backspace.setOnClickListener {
            if (passcode.isNotEmpty()) {
                passcodeRadios[passcode.length - 1].isChecked = false // Uncheck current
                passcode = passcode.take(passcode.length - 1)
                // No need to call clearPinIndicators() here as it resets the whole string
            }
        }

        // Example: Navigate back or to another screen
        // binding.someOtherButton.setOnClickListener {
        //     findNavController().navigate(R.id.action_passcodeFragment_to_anotherFragment)
        // }
    }

// In PasscodeFragment.kt where you handle passcode verification:
private fun handleSubmitPasscode(enteredPasscode: String) {
    val storedPinHash = SecurityUtils.getDecryptedPinHash(requireContext())
    val isPinLockEnabled = SecurityUtils.isPinLockEnabled(requireContext())

    if (isPinLockEnabled && storedPinHash != null && storedPinHash == enteredPasscode) {
        // PIN is correct and PIN lock is enabled
        findNavController().navigate(R.id.action_passcodeFragment_to_dashboardFragment)
        // clearPinIndicators() will be called below, which also clears 'passcode'
    } else {
        // Invalid PIN or PIN lock not enabled or no PIN set
        Toast.makeText(requireContext(), "Invalid passcode", Toast.LENGTH_SHORT).show()
    }
    clearPinIndicators() // Reset input fields regardless of success or failure
}

private fun clearPinIndicators() {
    val passcodeRadios: List<RadioButton> = listOf(
        binding.radio1, binding.radio2, binding.radio3,
        binding.radio4, binding.radio5, binding.radio6
    )
    for (radio in passcodeRadios) {
        radio.isChecked = false
    }
    passcode = "" // Also reset the internal passcode variable here
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}