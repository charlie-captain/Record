package com.charlie.androidtweaks.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.charlie.androidtweaks.R
import com.charlie.androidtweaks.core.TweakManager
import com.charlie.androidtweaks.data.Tweak
import com.charlie.androidtweaks.utils.TweakPermissionUtil
import com.charlie.androidtweaks.window.TweakWindowService
import kotlinx.android.synthetic.main.tweaks_toolbar.*
private const val TITLE_TOOLBAR = "Tweaks"

class TweakActivity : AppCompatActivity() {

    private var baseFragmentFragment: TweakFragment? = null
    private var tweaks: ArrayList<Tweak>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweak)
        tweaks_toolbar.title = TITLE_TOOLBAR
        setSupportActionBar(tweaks_toolbar)
        tweaks_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tweaks = intent.getSerializableExtra(TweakManager.key) as ArrayList<Tweak>
        if (tweaks == null) {
            tweaks = arrayListOf()
        }
        baseFragmentFragment = TweakFragment.newInstance(tweaks!!)
        supportFragmentManager.inTransaction {
            replace(R.id.fl_tweak, baseFragmentFragment!!)
        }
    }

    fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val transaction = beginTransaction()
        transaction.func()
        transaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            tweaks_toolbar.title = TITLE_TOOLBAR
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tweaks_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_toolbar_tweak_dissmiss -> {
                if (TweakManager.isFloatWindow) {
                    if (TweakPermissionUtil.checkPermission(this)) {
                        startService(Intent(this, TweakWindowService::class.java))
                    } else {
                        TweakPermissionUtil.applyPermission(this)
                    }
                }
                finish()
            }
            R.id.menu_toolbar_tweak_reset -> {
                TweakManager.reset()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TweakPermissionUtil.onActivityResult(
            this,
            requestCode,
            resultCode,
            data,
            object : TweakPermissionUtil.OnPermissionListener {
                override fun onPermissionGranted(isGranted: Boolean) {
                    if (isGranted) {
                        startService(Intent(this@TweakActivity, TweakWindowService::class.java))
                    } else {
                        Toast.makeText(this@TweakActivity, "请授权悬浮窗权限", Toast.LENGTH_LONG)
                    }
                }
            })
    }
}
