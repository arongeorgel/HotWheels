package com.garon.hotwheels

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.garon.hotwheels.list.VehicleListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_content, VehicleListFragment.create())
            .commit()

    }
}
