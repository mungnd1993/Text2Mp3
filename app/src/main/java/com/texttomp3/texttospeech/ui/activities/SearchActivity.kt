package com.texttomp3.texttospeech.ui.activities

import android.content.Intent
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.texttomp3.texttospeech.adapters.ProjectAdapter
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.ui.fragments.MoreBottomSheet
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivitySearchBinding
import com.texttomp3.texttospeech.utils.Constants.MORE_BOTTOM_SHEET
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
            ivBack.setOnClickListener {
                finish()
            }

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
        ttsViewModel.init(item.mode, item.language, Voice(item.voice, "", isSelected = true, isPlaying = false, item.displayIndex), item.pitch, item.speed, item.volume)
        ttsViewModel.setTextAndPath(item.id, item.content, item.filePath)
        val intent = Intent(this, TTSActivity::class.java)
        startActivity(intent)
    }

    override fun onClickMore(item: Project) {
        val moreBottomSheet = MoreBottomSheet.newInstance(item)
        moreBottomSheet.show(supportFragmentManager, MORE_BOTTOM_SHEET)
    }
}