import java.io.File

fun main() {
    File("D:\\IdeaProjects\\BeliefBlinker\\src\\main\\web\\fonts").walk()
        .forEach {
            print("\"${it.nameWithoutExtension}\",")
        }
}