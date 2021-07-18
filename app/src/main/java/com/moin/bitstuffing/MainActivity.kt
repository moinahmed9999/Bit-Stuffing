package com.moin.bitstuffing

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.moin.bitstuffing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleErrors()

        setOnClickListeners()
    }

    private fun handleErrors() {
        binding.apply {
            tilMsg.apply {
                editText?.doOnTextChanged { text, _, _, _ ->
                    when {
                        text.toString().isEmpty() -> {
                            isErrorEnabled = true
                            error = "Message should not be empty"
                        }
                        !isValidInput() -> {
                            isErrorEnabled = true
                            error = "Message should only contain 0 and 1"
                        }
                        else -> {
                            isErrorEnabled = false
                        }
                    }
                }
            }
        }
    }

    private fun isValidInput(): Boolean {
        val msg = binding.tilMsg.editText?.text.toString()
        if (msg.isEmpty()) return false
        for (c in msg)
            if (c!='0' && c!='1') return false
        return true
    }

    private fun setOnClickListeners() {
        binding.apply {
            btnEncodeMsg.setOnClickListener {
                closeKeyBoard()

                if (!isValidInput()) {
                    showSnackbar("Invalid input")
                } else {
                    encode()
                }
            }

            btnDecodeMsg.setOnClickListener {
                decode()
            }

            btnReset.setOnClickListener {
                reset()
            }

            when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_NO -> switchDayNight.isChecked = false
                Configuration.UI_MODE_NIGHT_YES -> switchDayNight.isChecked = true
            }

            switchDayNight.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }

    private fun encode() {
        val msg = binding.tilMsg.editText?.text.toString()

        val encodedMsg = encodeMessage(msg)

        binding.apply {
            tilMsg.editText?.isEnabled = false
            btnEncodeMsg.isEnabled = false

            btnDecodeMsg.visibility = View.VISIBLE

            tilEncodedMessage.apply {
                editText?.setText(Html.fromHtml(encodedMsg))
                visibility = View.VISIBLE
            }
        }
    }

    private fun encodeMessage(msg: String): String {
        var encodedMsg = ""
        var count = 0
        for (c in msg) {
            encodedMsg += c
            if (c == '0') {
                count = 0
            } else if (c == '1') {
                count++
                if (count == 5) {
                    encodedMsg += "<b>0</b>"
                    count = 0
                }
            }
        }
        return encodedMsg
    }

    private fun decode() {
        val msg = binding.tilEncodedMessage.editText?.text.toString()

        val decodedMsg = decodeMessage(msg)

        binding.apply {
            btnDecodeMsg.isEnabled = false

            btnReset.visibility = View.VISIBLE

            tilDecodedMessage.apply {
                editText?.setText(decodedMsg)
                visibility = View.VISIBLE
            }
        }
    }

    private fun decodeMessage(msg: String): String {
        var decodedMsg = ""
        var count = 0
        for (c in msg) {
            if (c == '0') {
                if (count != 5) {
                    decodedMsg += c
                }
                count = 0
            } else if (c == '1') {
                count++
                decodedMsg += c
            }
        }
        return decodedMsg
    }

    private fun reset() {
        binding.apply {
            tilMsg.editText?.apply {
                isEnabled = true
                text = null
                showKeyBoard(this)
            }

            btnEncodeMsg.isEnabled = true

            tilEncodedMessage.apply {
                editText?.text = null
                visibility = View.GONE
            }

            btnDecodeMsg.apply {
                isEnabled = true
                visibility = View.GONE
            }

            tilDecodedMessage.apply {
                editText?.text = null
                visibility = View.GONE
            }

            btnReset.visibility = View.GONE
        }
    }

    private fun showKeyBoard(view: View) {
        view.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}