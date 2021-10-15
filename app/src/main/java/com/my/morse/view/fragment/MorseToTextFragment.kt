package com.my.morse.view.fragment

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.my.morse.R
import com.my.morse.view.Constants
import kotlinx.android.synthetic.main.fragment_morse_to_text.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MorseToTextFragment : Fragment() {
    private var flashAllowed = true
    private var soundAllowed = true
    private var vibrationAllowed = true

    private var morseToText = true

    private var numbers = listOf<String>("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private var letters =
        listOf<String>(".", ",", "?", "-", "/", "$", "@", "+", "(", ")", "&", "'", ":", ";", "=")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_morse_to_text, container, false)

        val dotSound = MediaPlayer.create(requireContext(), R.raw.dot)
        val lineSound = MediaPlayer.create(requireContext(), R.raw.line)
        val vibration = activity?.getSystemService<Vibrator>()
        val patternShort = longArrayOf(50, 100)
        val patternLong = longArrayOf(50, 240)

        view.textView.visibility = View.GONE
        view.editText.isEnabled = false

        view.toogleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnFlash -> {
                        view.btnFlash.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_flash_off,
                            0,
                            0,
                            0
                        )
                    }
                    R.id.btnVibration -> {
                        view.btnVibration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_vibration_off,
                            0,
                            0,
                            0
                        )

                    }
                    R.id.btnSound -> {
                        view.btnSound.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_sound_off,
                            0,
                            0,
                            0
                        )
                    }
                }
            } else {
                when (checkedId) {
                    R.id.btnFlash -> {
                        view.btnFlash.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_flash_on,
                            0,
                            0,
                            0
                        )
                    }
                    R.id.btnVibration -> {
                        view.btnVibration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_vibration_on,
                            0,
                            0,
                            0
                        )

                    }
                    R.id.btnSound -> {
                        view.btnSound.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_sound_on,
                            0,
                            0,
                            0
                        )
                    }
                }
            }
        }

        view.dot_imageButton.setOnClickListener {
            if (!view.textView.isVisible) {
                view.textView.visibility = View.VISIBLE
            }

            lifecycleScope.launch {
                if (soundAllowed) {
                    dotSound.start()
                }

                if (flashAllowed) {
                    openFlashLight(Constants.DIT)
                }

                if (vibrationAllowed) {
                    vibration?.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibration.vibrate(VibrationEffect.createWaveform(patternShort, -1))
                        } else {
                            //deprecated in API 26
                            vibration.vibrate(patternShort, -1)
                        }
                    }
                }
                val currentText = view.editText.text.toString()
                view.editText.setText(currentText + ".")
            }

        }

        view.line_imageButton.setOnClickListener {

            if (!view.textView.isVisible) {
                view.textView.visibility = View.VISIBLE
            }

            lifecycleScope.launch {
                if (soundAllowed) {
                    lineSound.start()
                }

                if (flashAllowed) {
                    openFlashLight(Constants.DAH)
                }


                if (vibrationAllowed) {
                    vibration?.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibration.vibrate(VibrationEffect.createWaveform(patternLong, -1))
                        } else {
                            //deprecated in API 26
                            vibration.vibrate(patternLong, -1)
                        }
                    }
                }
                val currentText = view.editText.text.toString()
                view.editText.setText(currentText + "-")

            }
        }

        view.editText.doOnTextChanged { text, start, before, count ->
            if (!morseToText) {
                view.textView.setText(morseToTextConverter(text))
            } else {
                view.textView.setText(textToMorseConverter(text.toString()))
            }
        }

        view.btnSpace.setOnClickListener {
            val word = view.editText.text.toString()
            view.editText.setText(view.editText.text.toString() + " ")
            view.editText.setSelection(word.length + 1)
        }

        view.btnBackspace.setOnClickListener {
            if (!view.editText.text.isNullOrBlank()) {
                val word = view.editText.text.toString()
                view.editText.setText(word.substring(0, word.length - 1))
                view.editText.setSelection(word.length - 1)
            }

        }

        view.btnFlash.setOnClickListener {
            flashAllowed = !flashAllowed
        }

        view.btnSound.setOnClickListener {
            soundAllowed = !soundAllowed
        }

        view.btnVibration.setOnClickListener {
            vibrationAllowed = !vibrationAllowed
        }

        view.changeBtn.setOnClickListener {
            if (morseToText) {
                morseToText = false

                view.textText.text = "Morse"
                view.morseText.text = "Text"

                view.morseLayout.visibility = View.GONE
                view.textView.visibility = View.VISIBLE
                view.editText.isEnabled = true

                view.editText.setText("")
                view.textView.setText("")
            } else {
                morseToText = true

                view.textText.text = "Text"
                view.morseText.text = "Morse"

                view.morseLayout.visibility = View.VISIBLE
                view.textView.visibility = View.GONE
                view.editText.isEnabled = false

                view.editText.setText("")
                view.textView.setText("")
            }
        }

        return view
    }

    private fun openFlashLight(ditDah: String) {
        val cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ditDah == Constants.DIT) {
                    cameraManager.setTorchMode(cameraId, true)
                    lifecycleScope.launch {
                        delay(100)
                        cameraManager.setTorchMode(cameraId, false)
                    }
                } else {
                    cameraManager.setTorchMode(cameraId, true)
                    lifecycleScope.launch {
                        delay(300)
                        cameraManager.setTorchMode(cameraId, false)
                    }
                }

            }
        } catch (e: CameraAccessException) {
        }
    }

    fun textToMorseConverter(text: String): String {
        Log.d("ConverterText", "fun in")
        var textSum = ""
        if (text.contains("  ")) {
            val words = text.split("  ")
            for (word in words) {
                if (word != null) {
                    val letters = word.split(" ")+ "ç"
                    letters.forEachIndexed { index, letter ->

                        val newMorse = allCharacters().get(letter) ?: ""
                        textSum += newMorse
                        if (index >= letters.size) textSum += " "
                    }
                }
            }
            //birden fazla kelime
            //1 kelime + iki boşluk

        } else {
            val letters = text.split(" ")
            Log.d("ConverterText", "no word if")

            for (letter in letters) {
                textSum += allCharacters().get(letter) ?: ""
            }

        }
        return textSum
    }


    fun morseToTextConverter(text: CharSequence?): String {
        var textSum = ""
        if (text != null) {
            for (character in text) {
                if (character == ' ') {
                    textSum += "  "
                } else {
                    try {
                        if (letters.contains(character.toString())) {
                            Log.d("MorseToText", "isLetterOrDigit")
                            val value = specialCharacters().get(character.toString())
                            textSum += "$value "

                        } else {
                            if (numbers.contains(character.toString())) {
                                Log.d("MorseToText", "digit")
                                val value = digitMap().get(character.digitToInt()) ?: ""
                                textSum += "$value "

                            } else {
                                Log.d("MorseToText", "letter")
                                val value = letterMap().get(character.toString()) ?: ""
                                textSum += "$value "
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("MorseToTextFragment", e.localizedMessage)
                    }
                }

            }
        }
        return textSum
    }

    fun letterMap(): HashMap<String, String> {
        val letters = hashMapOf<String, String>()
        letters["a"] = ".-"
        letters["b"] = "-..."
        letters["c"] = "-.-."
        letters["d"] = "-.."
        letters["e"] = "."
        letters["f"] = "..-."
        letters["g"] = "--."
        letters["h"] = "...."
        letters["i"] = ".."
        letters["j"] = ".---"
        letters["k"] = "-.-"
        letters["l"] = ".-.."
        letters["m"] = "--"
        letters["n"] = "-."
        letters["o"] = "---"
        letters["p"] = ".--."
        letters["q"] = "--.-"
        letters["r"] = ".-."
        letters["s"] = "..."
        letters["t"] = "-"
        letters["u"] = "..-"
        letters["v"] = "...-"
        letters["w"] = ".--"
        letters["x"] = "-..-"
        letters["y"] = "-.--"
        letters["z"] = "--.."

        return letters
    }

    fun digitMap(): HashMap<Int, String> {
        val digits = hashMapOf<Int, String>()
        digits[0] = "-----"
        digits[1] = ".----"
        digits[2] = "..---"
        digits[3] = "...--"
        digits[4] = "....-"
        digits[5] = "....."
        digits[6] = "-...."
        digits[7] = "--..."
        digits[8] = "---.."
        digits[9] = "----."

        return digits
    }

    fun specialCharacters(): HashMap<String, String> {
        val specialCharacters = hashMapOf<String, String>()
        specialCharacters["."] = ".-.-.-"
        specialCharacters[","] = "--..--"
        specialCharacters["?"] = "..--.."
        specialCharacters["-"] = "-...-"
        specialCharacters["/"] = "-..-."
        specialCharacters["'"] = ".---."
        specialCharacters["("] = "-.--."
        specialCharacters[")"] = "-.--.-"
        specialCharacters["&"] = ".-..."
        specialCharacters[":"] = "---..."
        specialCharacters[";"] = "-.-.-."
        specialCharacters["="] = "-...-"
        specialCharacters["+"] = ".-.-."
        specialCharacters["$"] = "...-..-"
        specialCharacters["@"] = ".--.-."

        return specialCharacters
    }

    fun allCharacters(): HashMap<String, String> {
        val allCharacters = hashMapOf<String, String>()
        allCharacters[".-"] = "a"
        allCharacters["ç"] = " "
        allCharacters["-..."] = "b"
        allCharacters["-.-."] = "c"
        allCharacters["-.."] = "d"
        allCharacters["."] = "e"
        allCharacters["..-."] = "f"
        allCharacters["--."] = "g"
        allCharacters["...."] = "h"
        allCharacters[".."] = "i"
        allCharacters[".---"] = "j"
        allCharacters["-.-"] = "k"
        allCharacters[".-.."] = "l"
        allCharacters["--"] = "m"
        allCharacters["-."] = "n"
        allCharacters["---"] = "o"
        allCharacters[".--."] = "p"
        allCharacters["--.-"] = "q"
        allCharacters[".-."] = "r"
        allCharacters["..."] = "s"
        allCharacters["-"] = "t"
        allCharacters["..-"] = "u"
        allCharacters["...-"] = "v"
        allCharacters[".--"] = "w"
        allCharacters["-..-"] = "x"
        allCharacters["-.--"] = "y"
        allCharacters["--.."] = "z"

        allCharacters["-----"] = "0"
        allCharacters[".----"] = "1"
        allCharacters["..---"] = "2"
        allCharacters["...--"] = "3"
        allCharacters["....-"] = "4"
        allCharacters["....."] = "5"
        allCharacters["-...."] = "6"
        allCharacters["--..."] = "7"
        allCharacters["---.."] = "8"
        allCharacters["----."] = "9"

        allCharacters[".-.-.-"] = "."
        allCharacters["--..--"] = ","
        allCharacters["..--.."] = "?"
        allCharacters["-...-"] = "-"
        allCharacters["-..-."] = "/"
        allCharacters[".---."] = "'"
        allCharacters["-.--."] = "("
        allCharacters["-.--.-"] = ")"
        allCharacters[".-..."] = "&"
        allCharacters["---..."] = ":"
        allCharacters["-.-.-."] = ";"
        allCharacters["-...-"] = "="
        allCharacters[".-.-."] = "+"
        allCharacters["...-..-"] = "$"
        allCharacters[".--.-."] = "@"

        return allCharacters
    }

}