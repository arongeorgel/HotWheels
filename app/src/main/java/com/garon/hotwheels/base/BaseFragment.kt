package com.garon.hotwheels.base

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.garon.hotwheels.base.BaseFragment.Companion.ARGUMENT_KEY

inline fun <Argument : Parcelable, reified Fragment : BaseFragment<Argument>> newFragment(arg: Argument): Fragment {
    val fragment = Fragment::class.java.newInstance()
    val bundle = Bundle()
    bundle.putParcelable(ARGUMENT_KEY, arg)
    fragment.arguments = bundle
    return fragment
}

abstract class BaseFragment<Argument : Parcelable> : Fragment() {

    companion object {
        const val ARGUMENT_KEY = "argument.key"
    }

    protected lateinit var arg: Argument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arg = it.getParcelable(ARGUMENT_KEY)!!
        }
    }
}
