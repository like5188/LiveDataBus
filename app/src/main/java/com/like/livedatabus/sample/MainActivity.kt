package com.like.livedatabus.sample

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.like.livedatabus.LiveDataBus
import com.like.livedatabus.sample.databinding.ActivityMainBinding
import com.like.livedatabus_annotations.BusObserver

class MainActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    @BusObserver(["like1"])
    fun observer1(int: Int) {
        Log.e("LiveDataBus", "MainActivity onChanged")
        mBinding.tv1.text = int.toString()
    }

    fun register1(view: View) {
//        LiveDataBus.with<Int>("like1")
//                .observe(this, Observer<Int> {
//                    Log.e("LiveDataBus", "MainActivity onChanged")
//                    mBinding.tv1.text = it.toString()
//                })
    }

    fun changeData1(view: View) {
//        val text = mBinding.tv1.text.toString()
//        val oldValue = if (text.isEmpty()) 0 else text.toInt()
//        val newValue = oldValue + 1
//        LiveDataBus.with<Int>("like1").setValue(newValue)
    }

    fun register2(view: View) {
//        LiveDataBus.with<Int>("like2")
//                .observe(this, Observer<Int> {
//                    mBinding.tv2.text = it.toString()
//                })
    }

    fun changeData2(view: View) {
//        val text = mBinding.tv2.text.toString()
//        val oldValue = if (text.isEmpty()) 0 else text.toInt()
//        val newValue = oldValue + 1
//        LiveDataBus.with<Int>("like2").setValue(newValue)
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
