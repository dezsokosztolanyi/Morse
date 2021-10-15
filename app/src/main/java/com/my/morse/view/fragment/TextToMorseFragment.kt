package com.my.morse.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.my.morse.R
import kotlinx.android.synthetic.main.fragment_text_to_morse.*
import kotlinx.android.synthetic.main.fragment_text_to_morse.view.*

class TextToMorseFragment : Fragment() {
    private var startIndex = 0
    private var endIndex = 1
    private var text: String = ""
    private var textLength = 0
    private var currentLetter = ""
    private var correctMorse = ""

    private var numbers = listOf<String>("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private var letters =
        listOf<String>(".", ",", "?", "-", "/", "$", "@", "+", "(", ")", "&", "'", ":", ";", "=")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_text_to_morse, container, false)

        view.button.setOnClickListener {
            text = view.editTextTextPersonName.text.toString().lowercase()
            textLength = text.length

            if (textLength > 0) {
                currentLetter = text[startIndex].toString()
                view.button.text = currentLetter

                correctMorse = morseToTextConverter(currentLetter)
                view.ttm_morse.text = correctMorse

                val spanColor = ForegroundColorSpan(Color.GREEN)
                val ss = SpannableString(text)
                ss.setSpan(spanColor, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                view!!.ttm_wholeText.setText(ss)

                view.linearLayoutCompat.visibility = View.GONE
            }

        }

        view.ttm_btnNextLetter.setOnClickListener {
            if (endIndex + 1 > textLength) {
                Toast.makeText(requireContext(), "Bitti", Toast.LENGTH_LONG).show()
            } else {
                startIndex += 1
                endIndex += 1

                currentLetter = text[startIndex].toString()

                view.ttm_morse.text = morseToTextConverter(currentLetter)

                val spanColor = ForegroundColorSpan(Color.GREEN)
                val ss = SpannableString(text)
                ss.setSpan(spanColor, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                view!!.ttm_wholeText.setText(ss)
            }
        }

        view.ttm_btnSpace.setOnClickListener {
            view.ttm_usersInput.setText(view.ttm_usersInput.text.toString() + " ")
        }

        view.ttm_btnBackspace.setOnClickListener {
            if (!view.ttm_usersInput.text.isNullOrBlank()) {
                val word = view.ttm_usersInput.text.toString()
                view.ttm_usersInput.setText(word.substring(0, word.length - 1))
            }

        }

        view.ttm_dot_imageButton.setOnClickListener {
            val currentText = view.ttm_usersInput.text.toString()
            view.ttm_usersInput.setText(currentText + ".")

        }

        view.ttm_line_imageButton.setOnClickListener {
            val currentText = view.ttm_usersInput.text.toString()
            view.ttm_usersInput.setText(currentText + "-")

        }



        view.ttm_usersInput.doOnTextChanged { currentText, start, before, count ->

            if (view.ttm_usersInput.text.toString().replace("\\s".toRegex(), "") == correctMorse.replace("\\s".toRegex(), "")){
                Toast.makeText(requireContext(),"Doğru ulan",Toast.LENGTH_LONG).show()
                if (endIndex + 1 > textLength) {
                    Toast.makeText(requireContext(), "Bitti", Toast.LENGTH_LONG).show()

                    view.linearLayoutCompat.visibility = View.VISIBLE

                    view.ttm_usersInput.text = ""
                    view.ttm_morse.text = ""
                    view.ttm_wholeText.text = "Morse alfabesiyle yazın!"
                    view.ttm_usersInput.visibility = View.GONE

                    startIndex = 0
                    endIndex = 1
                    text = ""
                    textLength = 0

                } else {
                    startIndex += 1
                    endIndex += 1

                    currentLetter = text!![startIndex]!!.toString()

                    correctMorse = morseToTextConverter(currentLetter)
                    view.ttm_morse.text = correctMorse

                    view.ttm_usersInput.text = ""

                    val spanColor = ForegroundColorSpan(Color.GREEN)
                    val ss = SpannableString(text)
                    ss.setSpan(spanColor, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    view!!.ttm_wholeText.setText(ss)


                }

            }



        }

        return view
    }

    fun morseToTextConverter(character: String): String {
        var textSum = ""
        if (character == " ") {
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
                        val value = digitMap().get(character.toInt()) ?: ""
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


}