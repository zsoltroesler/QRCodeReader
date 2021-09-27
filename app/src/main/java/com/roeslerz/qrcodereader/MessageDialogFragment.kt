package com.roeslerz.qrcodereader

import android.R
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MessageDialogFragment : DialogFragment() {

    interface MessageDialogListener {
        fun onDialogClick(dialogFragment: DialogFragment?)
        fun showMessageDialog(title: String?, message: String?)
    }

    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mListener: MessageDialogListener? = null

    companion object {
        fun newInstance(
            mTitle: String?,
            mMessage: String?,
            mListener: MessageDialogListener?
        ): MessageDialogFragment {
            val messageDialogFragment = MessageDialogFragment()
            messageDialogFragment.mTitle = mTitle
            messageDialogFragment.mMessage = mMessage
            messageDialogFragment.mListener = mListener
            return messageDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(mMessage)
            .setTitle(mTitle)
            .setPositiveButton("OK") { dialog: DialogInterface?, id: Int ->
                mListener?.onDialogClick(this@MessageDialogFragment)
            }
            .show()
        val textView = alertDialog.findViewById<TextView>(R.id.message)
        textView!!.textSize = 18f
        return alertDialog
    }
}