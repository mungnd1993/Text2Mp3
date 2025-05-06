package com.texttomp3.texttospeech.base

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding>: AppCompatActivity() {
    lateinit var binding: VB

    abstract fun createBinding(): VB

    abstract fun initMain()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = createBinding()
        setContentView(binding.root)
        setDecorView()
        hideNavigationBar()
        initMain()
    }

    private fun setDecorView(view: View? = null) {
        val root = view ?: binding.root
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
    }

    private fun hideNavigationBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideNavigationBar()
        }
    }

    protected fun addFragment(
        containerViewId: Int,
        fragment: Fragment,
        isReplace: Boolean = false,
        isAddToBackStack: Boolean = true
    ) {
        val tag = fragment::class.java.simpleName

        val isInBackStack = supportFragmentManager.findFragmentByTag(tag) != null

        supportFragmentManager.commit {
            if (isReplace) {
                replace(containerViewId, fragment, tag)
            } else {
                add(containerViewId, fragment, tag)
            }

            // Chỉ addToBackStack nếu chưa có fragment này trong backstack
            if (isAddToBackStack && !isInBackStack) {
                addToBackStack(tag)
            }
        }
    }

}