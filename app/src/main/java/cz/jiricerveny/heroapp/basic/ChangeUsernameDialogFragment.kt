package cz.jiricerveny.heroapp.basic


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import cz.jiricerveny.heroapp.R

/**
 * Fragment used to display dialog which edits username displayed in NavigationDrawer
 */
class ChangeUsernameDialogFragment : DialogFragment() {
    private lateinit var listener: ChangeUsernameDialogListener

    /**
     * Interface for Listener
     */
    interface ChangeUsernameDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, input: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ChangeUsernameDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        " must implement ChangeUsernameDialogListener"
            )
        }
    }


    /**
     * creates dialog with message and buttons
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_message)
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    listener.onDialogPositiveClick(this, input.text.toString())
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
