package cz.jiricerveny.heroapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import cz.jiricerveny.heroapp.databinding.ActivityThirdDialogBinding

/**
 * Activity launched by third button in [DialogsFragment]
 */
class ThirdDialogActivity : AppCompatActivity() {
    lateinit var binding: ActivityThirdDialogBinding

    /**
     * Takes text from intent and merges it with text from EditText
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val message = intent.getStringExtra(EXTRA_MESSAGE)

        binding.apply {

        }

        val textView = binding.newActivityText
        val editText = binding.newActivityEditText
        val button = binding.buttonNewActivity
        val newText = "Insert some text to be merged with \"${message}\""
        textView.text = newText
        button.setOnClickListener {
            returnBack("$message ${editText.text}")
        }
        editText.setOnClickListener { animateToKeyframeTwo() }
    }

    /**
     * Animation of confirm button which is called after clicking on EditText
     */
    private fun animateToKeyframeTwo() {
        val constraint2 = ConstraintSet()
        constraint2.clone(this, R.layout.activity_third_dialog_final)
        TransitionManager.beginDelayedTransition(binding.conLay)
        constraint2.applyTo(binding.conLay)
    }

    /**
     * sends new text back to previous activity
     */
    private fun returnBack(message: String) {
        val returnIntent = Intent()
        returnIntent.putExtra(ACTIVITY_RESULT, message)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
