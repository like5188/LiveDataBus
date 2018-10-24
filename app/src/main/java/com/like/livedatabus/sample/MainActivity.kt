package com.like.livedatabus.sample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import com.like.livedatabus.LiveDataBus
import com.like.livedatabus.sample.databinding.ActivityMainBinding
import com.like.livedatabus_annotations.BusObserver
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        MainViewModel(this)
    }

    @BusObserver(["like1"])
    fun observer1(i: Int) {
        Log.e("LiveDataBus", "MainActivity onChanged tag1=like1")
        mBinding.tv1.text = i.toString()
    }

    @BusObserver(["like2"])
    fun observer2(s: String) {
        Log.e("LiveDataBus", "MainActivity onChanged tag1=like2")
        mBinding.tv2.text = s
    }

    fun changeData1(view: View) {
        val text = mBinding.tv1.text.toString()
        val oldValue = if (text.isEmpty()) 0 else text.toInt()
        val newValue = oldValue + 1
        LiveDataBus.post("like1", newValue)
    }

    fun changeData2(view: View) {
        val text = mBinding.tv2.text.toString()
        val oldValue = if (text.isEmpty()) 0 else text.toInt()
        val newValue = oldValue + 1
        thread {
            LiveDataBus.post("like4", newValue)
        }
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
