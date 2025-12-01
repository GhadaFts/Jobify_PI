package com.example.jobify.model
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object ChatHelper {

    /**
     * Détecte si l'utilisateur montre des signes de frustration
     */
    fun detectFrustration(lastUserMessage: String): Boolean {
        val message = lastUserMessage.lowercase()
        val frustrationKeywords = listOf(
            "ahhh", "argh", "pourquoi", "encore", "stop",
            "arrête", "arrêtez", "assez", "j'ai déjà dit"
        )

        return frustrationKeywords.any { message.contains(it) }
    }

    /**
     * Vérifie si le bot répète les mêmes questions
     */
    fun detectRepetition(messages: List<ChatMessage>): Boolean {
        val lastBotMessages = messages
            .filter { !it.isUser }
            .takeLast(3)
            .map { it.text }

        if (lastBotMessages.size < 2) return false

        return lastBotMessages[0] == lastBotMessages[1]
    }

    /**
     * Formate le texte markdown basique pour l'affichage
     */
    fun formatMarkdown(text: String): String {
        var formatted = text

        // Gras **texte**
        formatted = formatted.replace(Regex("\\*\\*(.*?)\\*\\*")) { matchResult ->
            "<b>${matchResult.groupValues[1]}</b>"
        }

        // Italique *texte*
        formatted = formatted.replace(Regex("\\*(.*?)\\*")) { matchResult ->
            "<i>${matchResult.groupValues[1]}</i>"
        }

        // Titres avec #
        formatted = formatted.replace(Regex("^### (.+)$", RegexOption.MULTILINE)) { matchResult ->
            "<h3>${matchResult.groupValues[1]}</h3>"
        }

        formatted = formatted.replace(Regex("^## (.+)$", RegexOption.MULTILINE)) { matchResult ->
            "<h2>${matchResult.groupValues[1]}</h2>"
        }

        // Listes avec -
        formatted = formatted.replace(Regex("^- (.+)$", RegexOption.MULTILINE)) { matchResult ->
            "• ${matchResult.groupValues[1]}"
        }

        // Numéros avec 1.
        formatted = formatted.replace(Regex("^\\d+\\. (.+)$", RegexOption.MULTILINE)) { matchResult ->
            "→ ${matchResult.groupValues[1]}"
        }

        return formatted
    }
}