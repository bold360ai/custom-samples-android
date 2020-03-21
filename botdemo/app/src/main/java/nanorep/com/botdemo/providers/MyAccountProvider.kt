package nanorep.com.botdemo.providers

import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.sdkcore.utils.Completion

class MyAccountProvider(var enableContinuity: Boolean = false) : AccountInfoProvider {

    private val accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        val account = if(enableContinuity) accounts[info.getApiKey()] else info
        //(account as? BoldAccount)?.skipPrechat()
        callback.onComplete(account ?: info)
    }

    override fun update(account: AccountInfo) {
        accounts[account.getApiKey()]?.run {
            update(account)
        } ?: kotlin.run {
            accounts[account.getApiKey()] = account
        }
    }
}