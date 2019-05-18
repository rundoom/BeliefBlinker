import com.google.gson.JsonParser
import java.io.File


var parser = JsonParser()

val props = parser.parse(File("properties.json").readText()).asJsonObject

val internalPort = props["internalPort"].asInt
val respondSecret = props["respondSecret"].asString
val filteredPosts = props["avaliablePosts"].asJsonArray.toList().map { it.asLong }
val startSet = props["startingSet"].asJsonArray.toList().map {
    VkMsg(VkMsgBody(it.asString), MsgType.NO_VK_MSG)
}