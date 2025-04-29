package com.texttomp3.texttospeech.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.texttomp3.texttospeech.databinding.IntroOneLayoutBinding
import com.texttomp3.texttospeech.databinding.IntroTwoLayoutBinding
import com.texttomp3.texttospeech.viewmodels.SettingViewModel

class IntroAdapter(private val settingViewModel: SettingViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val PAGE_ONE = 0
    private val PAGE_TWO = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PAGE_ONE -> {
                val binding = IntroOneLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PageOneViewHolder(binding)
            }
            PAGE_TWO -> {
                val binding = IntroTwoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PageTwoViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PageOneViewHolder -> {
                // Bind data for Page One
            }
            is PageTwoViewHolder -> {
                if (!settingViewModel.proVersion.value) {
                    val screenHeightDp = holder.itemView.resources.displayMetrics.heightPixels / Resources.getSystem().displayMetrics.density
                    if (screenHeightDp < 850) {
                        with(holder.binding.ivLanguage) {
                            layoutParams.height = (340 * Resources.getSystem().displayMetrics.density).toInt()
                            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                bottomMargin = (16 * Resources.getSystem().displayMetrics.density).toInt()
                            }
                            requestLayout()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> PAGE_ONE
            1 -> PAGE_TWO
            else -> throw IllegalArgumentException("Invalid Position")
        }
    }

    class PageOneViewHolder(val binding: IntroOneLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    class PageTwoViewHolder(val binding: IntroTwoLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}