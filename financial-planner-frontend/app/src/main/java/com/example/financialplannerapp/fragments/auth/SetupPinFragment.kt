package com.example.financialplannerapp.fragments.auth

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentSetupPinBinding
import com.example.financialplannerapp.utils.SecurityUtils
import java.lang.StringBuilder

class SetupPinFragment : Fragment() {

    private var _binding: FragmentSetupPinBinding? = null
    private val binding get() = _binding!!

    private enum class PinSetupState { ENTERING_NEW_PIN, CONFIRMING_NEW_PIN }
    private var currentState: PinSetupState = PinSetupState.ENTERING_NEW_PIN
    private var firstPin: String = ""
    private val currentPinEntry: StringBuilder = StringBuilder()

    private lateinit var pinIndicators: List<RadioButton>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pinIndicators = listOf(
            binding.pinIndicator1, binding.pinIndicator2, binding.pinIndicator3,
            binding.pinIndicator4, binding.pinIndicator5, binding.pinIndicator6
        )

        binding.setupPinTitle.text = getString(R.string.setup_pin_title) // Assuming you add this to strings.xml
        updateInstructionText()
        setupKeypadListeners()
    }

    private fun setupKeypadListeners() {
        val numericButtons = listOf<Button>(
            binding.button0, binding.button1, binding.button2, binding.button3, binding.button4,
            binding.button5, binding.button6, binding.button7, binding.button8, binding.button9
        )

        numericButtons.forEach { button ->
            button.setOnClickListener {
                if (currentPinEntry.length < 6) {
                    currentPinEntry.append(button.text)
                    updatePinIndicators(currentPinEntry.length)
                    if (currentPinEntry.length == 6) {
                        processPinEntry()
                    }
                }
            }
        }

        binding.buttonBackspace.setOnClickListener {
            if (currentPinEntry.isNotEmpty()) {
                currentPinEntry.deleteCharAt(currentPinEntry.length - 1)
                updatePinIndicators(currentPinEntry.length)
                binding.statusText.text = "" // Clear status on backspace
            }
        }
    }

    private fun updateInstructionText() {
        when (currentState) {
            PinSetupState.ENTERING_NEW_PIN -> {
                binding.instructionText.text = getString(R.string.enter_new_6_digit_pin) // Add to strings.xml
            }
            PinSetupState.CONFIRMING_NEW_PIN -> {
                binding.instructionText.text = getString(R.string.confirm_your_new_pin) // Add to strings.xml
            }
        }
    }

    private fun updatePinIndicators(count: Int) {
        for (i in pinIndicators.indices) {
            pinIndicators[i].isChecked = i < count
        }
    }

    private fun processPinEntry() {
        when (currentState) {
            PinSetupState.ENTERING_NEW_PIN -> {
                firstPin = currentPinEntry.toString()
                currentPinEntry.clear()
                updatePinIndicators(0)
                currentState = PinSetupState.CONFIRMING_NEW_PIN
                updateInstructionText()
                binding.statusText.text = ""
            }
            PinSetupState.CONFIRMING_NEW_PIN -> {
                val confirmationPin = currentPinEntry.toString()
                if (confirmationPin == firstPin) {
                    // In a real scenario, hash the PIN first (e.g., SHA-256)
                    // For now, passing firstPin directly as per instruction for SecurityUtils
                    SecurityUtils.saveEncryptedPinHash(requireContext(), firstPin)
                    SecurityUtils.setPinLockEnabled(requireContext(), true)

                    binding.statusText.text = getString(R.string.pin_set_successfully) // Add to strings.xml
                    binding.statusText.setTextColor(Color.GREEN)
                    disableKeypad()

                    // Navigate back after a delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 1500) // 1.5 seconds delay

                } else {
                    binding.statusText.text = getString(R.string.pin_mismatch_try_again) // Add to strings.xml
                    binding.statusText.setTextColor(Color.RED)
                    currentPinEntry.clear()
                    updatePinIndicators(0)
                    currentState = PinSetupState.ENTERING_NEW_PIN
                    updateInstructionText()
                    firstPin = ""
                }
            }
        }
    }

    private fun disableKeypad() {
        val allButtons = listOf<View>(
            binding.button0, binding.button1, binding.button2, binding.button3, binding.button4,
            binding.button5, binding.button6, binding.button7, binding.button8, binding.button9,
            binding.buttonBackspace
        )
        allButtons.forEach { it.isEnabled = false; it.alpha = 0.5f }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
