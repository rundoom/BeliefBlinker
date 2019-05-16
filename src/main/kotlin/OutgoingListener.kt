import io.ktor.http.cio.websocket.Frame
import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.channels.SendChannel

private val registeredChannels = ConcurrentSet<SendChannel<Frame>>()
val prevMsg = ConcurrentSet<String>()


fun registerChannel(chanel: SendChannel<Frame>) {
    registeredChannels.add(chanel)
}

suspend fun sendAll(msg: String){
    prevMsg.add(msg)
    registeredChannels.forEach {
        try {
            it.send(Frame.Text(msg))
        } catch (e: Exception){
            registeredChannels.remove(it)
        }
    }
}