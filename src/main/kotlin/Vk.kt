import com.google.gson.annotations.SerializedName

data class VkMsg(
    val type: MsgType?,
    val groupId: Long,
    val secret: String,
    @SerializedName("object") val msgBody: VkMsgBody
)

enum class MsgType {
    @SerializedName("message_new")
    MESSAGE_NEW,

    @SerializedName("wall_reply_new")
    WALL_REPLY_NEW,

    @SerializedName("confirmation")
    CONFIRMATION
}

data class VkMsgBody(
    @SerializedName("from_id")
    val fromId: Long,
    val text: String,
    @SerializedName("post_id")
    val postId: Long?
)