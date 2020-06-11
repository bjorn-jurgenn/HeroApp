package cz.jiricerveny.heroapp.basic


import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentAnimationDrawablesBinding


class AnimationDrawablesFragment : Fragment() {
    private lateinit var moodAnimation: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAnimationDrawablesBinding.inflate(inflater, container, false)
        val moodImage = binding.animatedImage.apply {
            setBackgroundResource(R.drawable.animation_moods)
            moodAnimation = background as AnimationDrawable
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        moodAnimation.start()
    }

}