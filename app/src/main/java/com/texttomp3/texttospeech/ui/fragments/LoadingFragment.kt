package com.texttomp3.texttospeech.ui.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.texttomp3.texttospeech.databinding.FragmentLoadingBinding
import com.texttomp3.texttospeech.utils.Coroutines
import kotlinx.coroutines.delay

class LoadingFragment : Fragment() {
    private lateinit var binding: FragmentLoadingBinding
    private lateinit var rotateAnimator: ObjectAnimator
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            text = it.getString(ARG_TEXT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingBinding.inflate(inflater, container, false)

        rotateAnimator = ObjectAnimator.ofFloat(binding.ivLoading, "rotation", 0f, 360f).apply {
            duration = 2500
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            start()
        }

        binding.tvDesc.text = text

        binding.root.alpha = 0f
        binding.root.animate()
            .alpha(1f)
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())
            .start()

        return binding.root
    }

    fun showSuccessAndDismiss() {
        rotateAnimator.cancel()

        binding.llLoading.visibility = View.GONE
        binding.llSuccess.visibility = View.VISIBLE

        binding.root.postDelayed({
            binding.root.animate()
                .alpha(0f)
                .setDuration(500)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    parentFragmentManager.beginTransaction()
                        .remove(this@LoadingFragment)
                        .commitAllowingStateLoss()
                }
                .start()
        }, 1500)
    }

    companion object {
        private const val ARG_TEXT = "text"

        @JvmStatic
        fun newInstance(text: String) =
            LoadingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TEXT, text)
                }
            }
    }
}
