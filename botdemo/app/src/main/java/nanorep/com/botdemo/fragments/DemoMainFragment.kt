package nanorep.com.botdemo.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nanorep.nanoengine.BotAccount
import kotlinx.android.synthetic.main.fragment_main.*
import nanorep.com.botdemo.ChatHandler
import nanorep.com.botdemo.R

internal const val DemoMainFragment_TAG = "DemoMainFragment"

class DemoMainFragment : Fragment() {

    private var chatHandler: ChatHandler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        start_not_logged_in.setOnClickListener { onChatClick(it) }
        start_logged_in.setOnClickListener { onChatClick(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        chatHandler = (context as? ChatHandler) ?: kotlin.run {
            Log.e(TAG, "$context must implement ChatFlowHandler")
            null
        }
    }

    override fun onDetach() {
        super.onDetach()
        chatHandler = null
    }

    private fun onChatClick(view: View) {

        BotAccount(getString(R.string.api_key), getString(R.string.account),
            getString(R.string.knowledge_base), getString(R.string.domain), null).apply {

            contexts = when (view.id) {
                R.id.start_logged_in -> {
                    mapOf("Login" to "LoggedIn")
                }

                else -> {
                    mapOf("Login" to "NotLoggedIn")
                }
            }

            view.isEnabled = false
            chatHandler?.onAccountReady(this)
        }
    }

    companion object {
        const val TAG = "DemoMainFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment DemoMainFragment.
         */
        @JvmStatic
        fun newInstance() = DemoMainFragment()
    }
}
