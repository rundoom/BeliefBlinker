import com.google.gson.annotations.SerializedName

data class VkMsg(
    @SerializedName("object") val msgBody: VkMsgBody,
    val type: MsgType? = null,
    val groupId: Long? = null
)

enum class MsgType {
    @SerializedName("no_vk_msg")
    NO_VK_MSG,

    @SerializedName("message_new")
    MESSAGE_NEW,

    @SerializedName("wall_reply_new")
    WALL_REPLY_NEW,

    @SerializedName("confirmation")
    CONFIRMATION,

    @SerializedName("wall_reply_delete")
    WALL_REPLY_DELETE
}

data class VkMsgBody(
    val text: String,
    val id: Long? = null,
    @SerializedName("from_id")
    val fromId: Long? = null,
    @SerializedName("post_id")
    val postId: Long? = null
)