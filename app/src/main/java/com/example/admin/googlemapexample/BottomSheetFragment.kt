package com.example.admin.googlemapexample

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.admin.googlemapexample.R.id.bottom_sheet
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import kotlinx.android.synthetic.main.bottom_sheet_fragment.view.*

class BottomSheetFragment : Fragment() {

    companion object {
        lateinit var behavior: BottomSheetBehavior<FrameLayout>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        behavior = BottomSheetBehavior.from(bottom_sheet)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.e("onSlide", "onSlide")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
////                    BottomSheetBehavior.STATE_HIDDEN -> {
////                    }
////                    BottomSheetBehavior.STATE_EXPANDED ->
////                        bottomSheet.tv.text = bikeStations.toString()
////
////                    BottomSheetBehavior.STATE_COLLAPSED ->
////
////                    BottomSheetBehavior.STATE_DRAGGING -> {
////                    }
////                    BottomSheetBehavior.STATE_SETTLING -> {
////                    }
//                }
            }
        })

        behavior.peekHeight = 0
        behavior.isHideable = false

        bottom_sheet.setOnClickListener {
            expandCloseSheet()
        }
    }

    private fun expandCloseSheet() {
        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}