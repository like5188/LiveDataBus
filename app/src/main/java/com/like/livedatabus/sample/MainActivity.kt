package com.like.livedatabus.sample

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.like.livedatabus.LiveDataBus
import com.like.livedatabus.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        LiveDataBus.get().with("like1", Int::class.java)
                .observe(this, Observer<Int> {
                    mBinding.tv1.text = it.toString()
                })
    }

    fun changeData(view: View) {
        val text = mBinding.tv1.text.toString()
        val oldValue = if (text.isEmpty()) 0 else text.toInt()
        val newValue = oldValue + 1
        LiveDataBus.get().with("like1").value = newValue
    }

    fun registerNew(view: View) {
        LiveDataBus.get().with("like2", Int::class.java)
                .observe(this, Observer<Int> {
                    mBinding.tv2.text = it.toString()
                })
    }
}
