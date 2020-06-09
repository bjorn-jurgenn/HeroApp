package cz.jiricerveny.heroapp

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import cz.jiricerveny.heroapp.databinding.FragmentCustomBinding
import cz.jiricerveny.heroapp.databinding.FragmentDialogsBinding

/**
 * Fragment used for exercise with dialogs
 * TextView, EditText and three Buttons
 * first shows alert dialog and changes text in text view to "hello + EditText"
 * second shows custom dialog and merges text in EditText and dialogs EditText
 * third starts new activity with EditText and Button and merges EditText and EditText in new activity a returns it back to TextView
 */
class DialogsFragment : Fragment() {
    private lateinit var binding: FragmentDialogsBinding

    /**
     * sets listeners to buttons
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogsBinding.inflate(inflater, container, false)

        binding.buttonDialog1.setOnClickListener {
            alertDialogView(binding.fragmentDialogEditText.text.toString())
        }
        binding.buttonDialog2.setOnClickListener {
            showDialogNr2()
        }
        binding.buttonDialog3.setOnClickListener {
            launchNewActivity()
        }
        binding.buttonDialogReset.setOnClickListener {
            binding.fragmentDialogEditText.text = null
            binding.fragmentDialogText.text = getString(R.string.no_text)
        }
        return binding.root
    }

    /**
     * Adds returned text from finished activity to TextView
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.fragmentDialogText.text = data?.getStringExtra(ACTIVITY_RESULT).toString()
        super.onActivityResult(requestCode, resultCode, data)
    }


    /**
     * Launches new activity for result
     */
    private fun launchNewActivity() {
        val intent = Intent(context, ThirdDialogActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, binding.fragmentDialogEditText.text.toString())
        }
        startActivityForResult(intent, 0)
    }

    private fun alertDialogView(title: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
            .setMessage("Hello prefix will be added")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                val newText = "Hello $title"
                binding.fragmentDialogText.text = newText
            }
            .show()
    }

    /**
     * Shows custom dialog
     * When in portrait mode - displays fullscreen dialog
     * When in landscape mode - displays classic dialog
     */
    private fun showDialogNr2() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val builder = AlertDialog.Builder(context!!)
            val customLayout = layoutInflater.inflate(R.layout.fragment_custom, null)
            builder.setView(customLayout)
            val dialogsBinding = FragmentCustomBinding.bind(customLayout)
            val dialog = builder.create()
            dialogsBinding.buttonCustomDialog.setOnClickListener {
                mergeText(dialogsBinding)
                dialog.dismiss()
            }
            dialog.show()
        } else {
            // show the fragment fullscreen
            val view = layoutInflater.inflate(R.layout.fragment_custom, null)
            val dialog =
                Dialog(context!!, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
            dialog.setContentView(view)
            val dialogsBinding = FragmentCustomBinding.bind(view)
            dialogsBinding.buttonCustomDialog.setOnClickListener {
                mergeText(dialogsBinding)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun mergeText(dialogsBinding: FragmentCustomBinding) {
        val newText =
            "${binding.fragmentDialogEditText.text} ${dialogsBinding.customDialogEditText.text}"
        binding.fragmentDialogText.text = newText
    }

    /*override fun applyText(text: String) {
        binding.fragmentDialogText.text = "${binding.fragmentDialogEditText.text} $text"
    }*/
}