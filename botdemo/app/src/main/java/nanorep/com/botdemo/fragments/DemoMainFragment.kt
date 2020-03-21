package nanorep.com.botdemo.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.service.chooser.ChooserTarget
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.dp
import kotlinx.android.synthetic.main.fragment_main.*
import nanorep.com.botdemo.ChatHandler
import nanorep.com.botdemo.ChatHandler.Companion.ConfiguredAccent
import nanorep.com.botdemo.ChatHandler.Companion.ReadOutEnabled
import nanorep.com.botdemo.R
import java.util.*


internal const val DemoMainFragment_TAG = "DemoMainFragment"

class Accent(val title: String, val locale: Locale){

    companion object {

        val DEFAULT = Accent("Default", Locale.US)

        val accentsList = mutableListOf(
            Accent("English India", Locale("en_IN")),
            Accent("Hindi India", Locale("hi_IN")),
            Accent("Bengali India", Locale("bn_IN")),
            Accent("Gujarati India", Locale("gu_IN")),
            Accent("Kannada India", Locale("kn_IN")),
            Accent("Kashmiri India", Locale("ks_IN")),
            Accent("Malayalam, India", Locale("ml_IN")),
            Accent("Marathi, India", Locale("mr_IN")),
            Accent("Oriya, India", Locale("or_IN")),
            Accent("Tamil, India", Locale("ta_IN")),
            Accent("Telugu, India", Locale("te_IN"))
        )
    }
}

class DemoMainFragment : Fragment() {

    private var chatHandler: ChatHandler? = null
    private lateinit var selectedAccent: Accent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        start_not_logged_in.setOnClickListener { onChatClick(it) }
        start_logged_in.setOnClickListener { onChatClick(it) }

        readoutCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                accentSpinner.setSelection(0)
            }
        }

        val adapter = AccentAdapter(view.context, Accent.accentsList)

        accentSpinner.adapter = adapter

        accentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedAccent = Accent.DEFAULT
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedAccent = adapter.getItem(position) ?: Accent.DEFAULT
            }
        }
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

            chatHandler?.onAccountReady(this,
                mutableMapOf(ReadOutEnabled to readoutCheckBox.isChecked, ConfiguredAccent to selectedAccent.locale))
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

    inner class AccentAdapter(context: Context, val list: MutableList<Accent>) : ArrayAdapter<Accent>(context, android.R.layout.simple_spinner_item, list) {

        override fun getCount(): Int {
            return list.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup?
        ): View? {
            return TextView(context).apply {

                textSize = 16f

                text = if (position == 0) {
                    "Choose accent (Default is US english)"
                } else {
                    list[position].title
                }

                setTextColor(Color.BLUE)
            }
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return TextView(context).apply {

                textSize = 14f

                text = if (position == 0) {
                    ""
                } else {
                    list[position].title
                }

                setPadding(10, 10, 0, 10)
            }
        }
    }
}

