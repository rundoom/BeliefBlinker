import com.google.gson.GsonBuilder
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.slf4j.LoggerFactory.getLogger
import java.io.File

val log = getLogger(System::class.java)

fun initServer() {
    embeddedServer(Netty, port = 8282) {
        install(CORS) {
            anyHost()
        }
        install(AutoHeadResponse)
        install(WebSockets)
        install(ContentNegotiation) {
            gson {
                register(ContentType.Application.Json, GsonConverter(GsonBuilder().create()))
                disableHtmlEscaping()
                disableInnerClassSerialization()
                enableComplexMapKeySerialization()
                generateNonExecutableJson()
            }
        }

        routing {
            static("static") {
                files(File("static\\web"))
            }
            get("/initContent") {
                call.respond(prevMsg.toList())
            }
            post("/callbackVk") {
                val msg = call.receive<VkMsg>().msgBody
                if (msg.text.length < 50) sendAll(msg.text)
                call.respond(HttpStatusCode.OK, "ok")
            }
            webSocket("/msgChannel") {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        when (frame.readText()) {
                            "startReceiving" -> {
                                registerChannel(outgoing)
                            }
                            else -> close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}