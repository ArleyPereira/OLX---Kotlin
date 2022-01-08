package com.example.olx.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.Exception
import java.util.HashSet

class MaskText : TextWatcher {

    private val instance: MaskText? = null

    private var mMask: String? = null
    private var mEditText: EditText? = null
    private val symbolMask: MutableSet<String> = HashSet()
    private var isUpdating = false
    private var old = ""

    fun Mask(mask: String?, editText: EditText?) {
        mMask = mask
        mEditText = editText
        initSymbolMask()
    }

    private fun initSymbolMask() {
        for (element in mMask!!) {
            if (element != '#') symbolMask.add(element.toString())
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val str = unmask(s.toString(), symbolMask)
        if (isUpdating) {
            old = str
            isUpdating = false
            return
        }
        val mascara: String = if (str.length > old.length) mask(mMask, str) else s.toString()
        isUpdating = true
        mEditText!!.setText(mascara)
        mEditText!!.setSelection(mascara.length)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {}

    fun unmask(s: String): String {
        var s = s
        val replaceSymbols: MutableSet<String> = HashSet()
        replaceSymbols.add("-")
        replaceSymbols.add(".")
        replaceSymbols.add("/")
        replaceSymbols.add("_")
        replaceSymbols.add("(")
        replaceSymbols.add(")")
        replaceSymbols.add(" ")
        for (symbol in replaceSymbols) s = s.replace("[" + symbol + "]".toRegex(), "")
        return s
    }

    fun unmask(s: String, replaceSymbols: Set<String>): String {
        var s = s
        for (symbol in replaceSymbols) s = s.replace("[" + symbol + "]".toRegex(), "")
        return s
    }

    companion object {
        fun mask(format: String?, text: String): String {
            var maskedText = ""
            var i = 0
            for (m in format!!.toCharArray()) {
                if (m != '#') {
                    maskedText += m
                    continue
                }
                maskedText += try {
                    text[i]
                } catch (e: Exception) {
                    break
                }
                i++
            }
            return maskedText
        }
    }

    fun mask(text: String): String? {
        var maskedText: String? = ""
        var i = 0
        for (m in mMask!!.toCharArray()) {
            if (m != '#') {
                maskedText += m
                continue
            }
            maskedText += try {
                text[i]
            } catch (e: Exception) {
                break
            }
            i++
        }
        return maskedText
    }

}