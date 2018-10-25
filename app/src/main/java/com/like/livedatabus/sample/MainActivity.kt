package com.like.livedatabus.sample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
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

    @BusObserver(["like1", "like2"], requestCode = "1")
    fun observer1(i: Int) {
        Log.e("LiveDataBus", "MainActivity observer1 tag1=like1，数据：$i")
    }

    @BusObserver(["like1"])
    fun observer2(s: Int) {
        Log.e("LiveDataBus", "MainActivity observer2 tag1=like1，数据：$s")
    }

    fun changeData1(view: View) {
        LiveDataBus.post("like1", 1)
    }

    fun changeData2(view: View) {
        thread {
            LiveDataBus.post("like2", "1", 2)
        }
    }

    fun changeData3(view: View) {
        LiveDataBus.post("like3", 3)
    }

    fun changeData4(view: View) {
        LiveDataBus.post("like4", 4)
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
