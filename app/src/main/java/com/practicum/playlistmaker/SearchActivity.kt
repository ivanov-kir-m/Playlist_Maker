package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    companion object{
        const val SEARCH_TEXT = "search_text"
    }

    private var searchEditText : String = ""

    private lateinit var inputEditText  : EditText
    private lateinit var clearBtn : ImageView

    private val searchTextWatcher = object  : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            clearBtn.visibility = clearBtnVisibility(s)
            searchEditText = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {}

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditText = savedInstanceState.getString(SEARCH_TEXT).toString()
        inputEditText.setText(searchEditText)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        inputEditText.addTextChangedListener(searchTextWatcher)

        clearBtn = findViewById(R.id.clearIcon)
        clearBtn.visibility = clearBtnVisibility(inputEditText.text)
        clearBtn.setOnClickListener{
            clearSearch()
        }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.asBtnBack).setOnClickListener {
            finish()
        }

    }

    private fun clearBtnVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearSearch() {
        inputEditText.setText("")
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}