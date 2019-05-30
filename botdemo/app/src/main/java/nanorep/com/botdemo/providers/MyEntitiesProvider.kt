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
            "USER_ACCOUNTS" -> {
               Entity(Entity.PERSISTENT, Entity.NUMBER, "123", entityName, "1").apply {
                    for (i in 0..2) {

                        val property = Property(Entity.TEXT, i.toString() + "234", "ACCOUNT")
                        property.name = property.value
                        property.addProperty(Property(Entity.TEXT, "Some Value", "TYPE"))
                        property.addProperty(Property(Entity.TEXT, "$", "Currency"))
                        property.addProperty(Property(Entity.TEXT, "ID", i.toString() + "234"))

                        addProperty(property)
                    }
                }
            }
            else -> null
        }
    }
}