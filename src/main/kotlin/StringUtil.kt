fun chunkSym(str: String, charNum: Int = 8) =
    str.split(Regex("\\s")).map { string ->
        string.chunked(charNum).joinToString("\n") { it }
    }.joinToString("\n") { it }
