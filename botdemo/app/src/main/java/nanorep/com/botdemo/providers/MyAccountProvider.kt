package nanorep.com.botdemo.providers

import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.sdkcore.utils.Completion

class MyAccountProvider : AccountInfoProvider {

    private val accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        val account = accounts[info.getApiKey()]
        callback.onComplete(account?:info)
    }

    override fun updateAccountInfo(account: AccountInfo) {
        accounts[account.getApiKey()]?.run {
            updateInfo(account.getInfo())
        } ?: kotlin.run {
            accounts[account.getApiKey()] = account
        }
    }
}