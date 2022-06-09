/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 9/6/2022
 */
/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 9/6/2022
 */
package com.example.fakecall.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fakecall.R

class NumpadFragment : Fragment() {
    private lateinit var textView: TextView
    private lateinit var line: View
    private lateinit var numpad0: GridLayout
    private lateinit var numpad1: GridLayout
    private lateinit var numpad2: GridLayout
    private lateinit var numpad3: GridLayout
    private lateinit var numpad4: GridLayout
    private lateinit var numpad5: GridLayout
    private lateinit var numpad6: GridLayout
    private lateinit var numpad7: GridLayout
    private lateinit var numpad8: GridLayout
    private lateinit var numpad9: GridLayout
    private lateinit var numpadHash: GridLayout
    private lateinit var numpadAsterisk: GridLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_numpad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var text = ""
        var isVisible = View.GONE
        super.onViewCreated(view, savedInstanceState)
        textView = view.findViewById(R.id.text_view)
        line = view.findViewById(R.id.line)
        numpad0 = view.findViewById(R.id.btn_numpad_0)
        numpad1 = view.findViewById(R.id.btn_numpad_1)
        numpad2 = view.findViewById(R.id.btn_numpad_2)
        numpad3 = view.findViewById(R.id.btn_numpad_3)
        numpad4 = view.findViewById(R.id.btn_numpad_4)
        numpad5 = view.findViewById(R.id.btn_numpad_5)
        numpad6 = view.findViewById(R.id.btn_numpad_6)
        numpad7 = view.findViewById(R.id.btn_numpad_7)
        numpad8 = view.findViewById(R.id.btn_numpad_8)
        numpad9 = view.findViewById(R.id.btn_numpad_9)
        numpadAsterisk = view.findViewById(R.id.btn_numpad_asterisk)
        numpadHash = view.findViewById(R.id.btn_numpad_hash)
        textView.text = text
        line.visibility = isVisible
        numpad0.setOnClickListener {
            text += "0"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad1.setOnClickListener {
            text += "1"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad2.setOnClickListener {
            text += "2"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad3.setOnClickListener {
            text += "3"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad4.setOnClickListener {
            text += "4"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad5.setOnClickListener {
            text += "5"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad6.setOnClickListener {
            text += "6"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad7.setOnClickListener {
            text += "7"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad8.setOnClickListener {
            text += "8"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpad9.setOnClickListener {
            text += "9"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpadAsterisk.setOnClickListener {
            text += "*"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
        numpadHash.setOnClickListener {
            text += "#"
            textView.text = text
            if (isVisible == View.GONE) {
                line.visibility = View.VISIBLE
                isVisible = View.VISIBLE
            }
        }
    }
}