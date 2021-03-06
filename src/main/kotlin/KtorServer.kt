import com.google.gson.Gson
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
val gson = Gson()

fun initServer() {
    embeddedServer(Netty, port = internalPort) {
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
            get("/allFonts") {
                call.respond(File("static\\web\\fonts").list())
            }
            post("/callbackVk") {
                val msg = call.receive<VkMsg>()
                when (msg.type) {
                    MsgType.CONFIRMATION -> call.respond(HttpStatusCode.OK, respondSecret)
                    MsgType.MESSAGE_NEW -> {
                        call.respond(HttpStatusCode.OK, "ok")
                        if (msg.msgBody.text.length <= 50) broadcast(msg)
                    }
                    MsgType.WALL_REPLY_NEW -> {
                        call.respond(HttpStatusCode.OK, "ok")
                        if (msg.msgBody.text.length <= 50 && (msg.msgBody.postId in filteredPosts))
                            broadcast(msg)
                    }
                    MsgType.WALL_REPLY_DELETE -> {
                        call.respond(HttpStatusCode.OK, "ok")
                        broadcast(msg)
                    }
                    MsgType.NO_VK_MSG -> {
                        call.respond(HttpStatusCode.OK, "ok")
                        broadcast(msg)
                    }
                    null -> {
                    }
                }
            }
            webSocket("/msgChannel") {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        when (frame.readText()) {
                            "startReceiving" -> {
                                registerSession(this)
                                prevMsgCache.forEach {
                                    outgoing.send(Frame.Text(gson.toJson(it)))
                                }
                            }
                            else -> close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
                        }
                    } else if (frame is Frame.Close) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
                        unRegisterSession(this)
                    }
                }
            }
        }
    }.start(wait = true)
}