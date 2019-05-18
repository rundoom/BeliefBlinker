import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.channels.ClosedSendChannelException

private val registeredSessions = ConcurrentSet<WebSocketSession>()

val prevMsgCache = ConcurrentSet<VkMsg>().also { it.addAll(startSet) }


fun registerSession(chanel: WebSocketSession) = registeredSessions.add(chanel)

fun unRegisterSession(chanel: WebSocketSession) = registeredSessions.remove(chanel)

suspend fun broadcast(msg: VkMsg) {
    prevMsgCache.add(msg)

    registeredSessions.forEach {
        try {
            it.send(Frame.Text(gson.toJson(msg)))
        } catch (e: Exception) {
            try {
                it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "${e.message}"))
            } catch (e: ClosedSendChannelException) {
            } finally {
                unRegisterSession(it)
            }
        }
    }
}
