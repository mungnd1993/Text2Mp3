package com.texttomp3.texttospeech.ui.activities

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.texttomp3.texttospeech.adapters.ProjectAdapter
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.ui.fragments.MoreBottomSheet
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivitySearchBinding
import com.texttomp3.texttospeech.utils.Constants.CONTENT
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.FILE_PATH
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.utils.Constants.ID
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.MORE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import com.texttomp3.texttospeech.viewmodels.HomeViewModel
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>(), ProjectAdapter.OnClickListener {
    private val homeViewModel: HomeViewModel by lazy {
        getViewModel<HomeViewModel>()
    }

    private val ttsViewModel: TTSViewModel by lazy {
        getViewModel<TTSViewModel>()
    }

    private val list = mutableListOf<Project>()
    private lateinit var adapter: ProjectAdapter

    override fun createBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()
        handleOnBackPressed()
    }

    private fun initView() {
        with(binding) {
            homeViewModel.getProjects()

            adapter = ProjectAdapter(this@SearchActivity)
            rvProject.adapter = adapter
            rvProject.layoutManager = LinearLayoutManager(this@SearchActivity)

            lifecycleScope.launch {
                homeViewModel.projects.collect {
                    list.clear()
                    list.addAll(it)
                    adapter.submitData(list)
                }
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            etSearch.addTextChangedListener { editable ->
                val query = editable.toString().trim()
                if (query.isEmpty()) {
                    adapter.submitData(list)
                } else {
                    val filteredList = list.filter { project ->
                        project.name.contains(query, ignoreCase = true)
                    }
                    adapter.submitData(filteredList)
                }
            }


            ivDelete.setOnClickListener {
                etSearch.text?.clear()
            }
        }
    }

    override fun onClick(item: Project) {
        val intent = Intent(this, TTSActivity::class.java)
        intent.putExtra(MODE, item.mode)
        intent.putExtra(LANGUAGE, item.language)
        intent.putExtra(VOICE, item.voice)
        intent.putExtra(GENDER, item.gender)
        intent.putExtra(DISPLAY_INDEX, item.displayIndex)
        intent.putExtra(PITCH, item.pitch)
        intent.putExtra(SPEED, item.speed)
        intent.putExtra(VOLUME, item.volume)
        intent.putExtra(ID, item.id)
        intent.putExtra(CONTENT, item.content)
        intent.putExtra(FILE_PATH, item.filePath)
        startActivity(intent)
    }

    override fun onClickMore(item: Project) {
        val moreBottomSheet = MoreBottomSheet.newInstance(item)
        moreBottomSheet.show(supportFragmentManager, MORE_BOTTOM_SHEET)
    }

    private fun handleOnBackPressed() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}