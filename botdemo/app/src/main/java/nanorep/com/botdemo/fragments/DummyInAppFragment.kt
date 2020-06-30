package nanorep.com.botdemo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_in_app.*
import nanorep.com.botdemo.R

internal const val DummyInAppFragment_TAG = "DummyInAppFragment"

class DummyInAppFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        return inflater.inflate(R.layout.fragment_in_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_to_sdk.setOnClickListener { onBackToSDK(it) }

        view.requestFocus()
    }

    private fun onBackToSDK(view: View) {

        activity?.onBackPressed()

    }

    companion object {
        const val TAG = "DummyInAppFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment DummyInAppFragment.
         */
        @JvmStatic
        fun newInstance() = DummyInAppFragment()
    }
}
