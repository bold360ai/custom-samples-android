package nanorep.com.botdemo.providers

import com.nanorep.nanoengine.Entity
import com.nanorep.nanoengine.NRConversationMissingEntities
import com.nanorep.nanoengine.PersonalInfoRequest
import com.nanorep.nanoengine.Property
import com.nanorep.nanoengine.nonbot.EntitiesProvider
import com.nanorep.sdkcore.utils.Completion
import java.util.*

class MyEntitiesProvider : EntitiesProvider {

    private val random = Random()

    override fun provide(entities: ArrayList<String>, onReady: Completion<ArrayList<Entity>>) {
        val missingEntities = NRConversationMissingEntities()

        for (missingEntity in entities) {
               missingEntities.addEntity(createEntity(missingEntity))
        }

        (missingEntities.entities as? ArrayList<Entity>)?.apply { onReady.onComplete(this) }
    }

    override fun provide(personalInfoRequest: PersonalInfoRequest, callback: PersonalInfoRequest.Callback) {
        when (personalInfoRequest.id) {
            "getAccountBalance" -> {
                val balance = (random.nextInt(10000)).toString()
                callback.onInfoReady(balance, null)
                return
            }
        }
        callback.onInfoReady("1,000$", null)
    }

    private fun createEntity(entityName: String): Entity? {
        return when (entityName) {
            "CREDIT_CARD" -> {
                Entity(Entity.PERSISTENT, Entity.NUMBER, (random.nextInt(100 - 10) + 10).toString(), entityName, "1")
            }
            "ACCOUNT" -> {
                Entity(Entity.PERSISTENT, Entity.NUMBER, "123", entityName, "1").apply {
                    addProperty(Property(Entity.TEXT, (random.nextInt(10000 - 1000) + 1000).toString(), "1").apply { name = "ID" })
                    addProperty(Property(Entity.TEXT, "$", "2").apply { name = "CURRENCY" })
                    addProperty(Property(Entity.TEXT, "PRIVATE", "2").apply { name = "TYPE" })
                }
            }
            else -> null
        }
    }
}