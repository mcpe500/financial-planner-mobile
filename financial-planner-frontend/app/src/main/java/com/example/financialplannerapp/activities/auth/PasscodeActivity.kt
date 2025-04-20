package com.example.financialplannerapp.activities.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.ActivityLoginBinding
import com.example.financialplannerapp.databinding.ActivityPasscodeBinding

class PasscodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasscodeBinding
    private var passcode = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            button.setOnClickListener() {
                if (passcode.length < 6) {
                    passcode += button.text.toString()
                    passcodeRadios[passcode.length - 1].isChecked = true
                }
            }
        }

        for (radio: RadioButton in passcodeRadios) {
            radio.setOnTouchListener { _, _ -> true }
        }

        binding.backspace.setOnClickListener() {
            if (passcode.isNotEmpty()) {
                passcode = passcode.take(passcode.length - 1)
                passcodeRadios[passcode.length].isChecked = false
            }
        }

    }


}