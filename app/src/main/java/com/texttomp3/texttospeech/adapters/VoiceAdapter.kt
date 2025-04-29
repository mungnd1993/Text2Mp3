package com.texttomp3.texttospeech.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseRecyclerView
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.databinding.ItemVoiceBinding
import com.texttomp3.texttospeech.helpers.LanguageHelper
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.FEMALE
import com.texttomp3.texttospeech.utils.Constants.MALE

class VoiceAdapter(private val mode: String,private val listener: OnClickListener) :
    BaseRecyclerView<ItemVoiceBinding, Voice>() {

    override fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): ItemVoiceBinding {
        return ItemVoiceBinding.inflate(inflater, parent, false)
    }

    override fun areContentsTheSame(oldItem: Voice, newItem: Voice): Boolean {
        return oldItem.name == newItem.name &&
                oldItem.gender == newItem.gender &&
                oldItem.isSelected == newItem.isSelected &&
                oldItem.isPlaying == newItem.isPlaying
    }

    override fun areItemsTheSame(oldItem: Voice, newItem: Voice): Boolean {
        return oldItem.name == newItem.name
    }

    @SuppressLint("SetTextI18n")
    override fun setData(binding: ItemVoiceBinding, item: Voice, layoutPosition: Int) {
        val languages = LanguageHelper(binding.root.context).getLanguageList()
        with(binding) {
            if (mode == ANDROID) {
                tvName.text = "Voice ${item.displayIndex}"
            } else {
                tvName.text = languages.find { it.voice == item.name }?.voiceName
            }

            if (item.isSelected) {
                clItem.setBackgroundResource(R.drawable.bg_blue_rounded)
                val typeface = ResourcesCompat.getFont(binding.root.context, R.font.roboto_semibold)
                tvName.typeface = typeface
            } else {
                clItem.setBackgroundResource(R.drawable.bg_white_rounded)
                val typeface = ResourcesCompat.getFont(binding.root.context, R.font.roboto_regular)
                tvName.typeface = typeface
            }

            ivDemo.setImageResource(
                if (item.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )

            when (item.gender) {
                MALE -> {
                    ivIcon.setImageResource(R.drawable.ic_male)
                }
                FEMALE -> {
                    ivIcon.setImageResource(R.drawable.ic_female)
                }
                else -> {
                    ivIcon.setImageResource(R.drawable.ic_speaker)
                }
            }

            ivDemo.setOnClickListener {
                listener.onDemo(item)
            }

            clItem.setOnClickListener {
                listener.onClick(item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(voice: Voice)
        fun onDemo(voice: Voice)
    }
}