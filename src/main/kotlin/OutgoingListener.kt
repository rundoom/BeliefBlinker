import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.netty.util.internal.ConcurrentSet

private val registeredSessions = ConcurrentSet<WebSocketSession>()

val prevMsg = ConcurrentSet<String>()


fun registerSession(chanel: WebSocketSession) = registeredSessions.add(chanel)

fun unRegisterSession(chanel: WebSocketSession) = registeredSessions.remove(chanel)

suspend fun broadcast(msg: String) {
    prevMsg.add(msg)
    registeredSessions.forEach {
        try {
            it.send(Frame.Text(msg))
        } catch (e: Exception) {
            try {
                it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "${e.message}"))
            } catch (e: Exception){
                log.info(e.message)
            } finally {
                unRegisterSession(it)
            }
        }
    }
}