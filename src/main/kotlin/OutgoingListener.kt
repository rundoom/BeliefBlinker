import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.SendChannel

private val registeredChannels = mutableSetOf<SendChannel<Frame>>()

fun registerChannel(chanel: SendChannel<Frame>) {
    registeredChannels.add(chanel)
}

suspend fun sendAll(msg: String){
    registeredChannels.forEach {
        try {
            it.send(Frame.Text(msg))
        } catch (e: Exception){
            registeredChannels.remove(it)
        }
    }
}