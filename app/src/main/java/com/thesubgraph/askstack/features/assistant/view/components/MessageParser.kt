package com.thesubgraph.askstack.features.assistant.view.components

import androidx.compose.runtime.Immutable

@Immutable
sealed class MessageElement {
    @Immutable
    data class Text(val content: String) : MessageElement()
    
    @Immutable
    data class CodeBlock(val code: String, val language: String = "") : MessageElement()
    
    @Immutable
    data class InlineCode(val code: String) : MessageElement()
}

object MessageParser {
    fun parseMessage(content: String): List<MessageElement> {
        val elements = mutableListOf<MessageElement>()
        var currentIndex = 0
        
        val codeBlockPattern = Regex("```(\\w*)?\\n([\\s\\S]*?)```", RegexOption.MULTILINE)
        val inlineCodePattern = Regex("`([^`\n]+)`")
        
        val codeBlockMatches = codeBlockPattern.findAll(content).toList()
        val inlineCodeMatches = inlineCodePattern.findAll(content).toList()
        
        val allMatches = (codeBlockMatches.map { match ->
            Triple(match.range.first, match.range.last, "block")
        } + inlineCodeMatches.map { match ->
            Triple(match.range.first, match.range.last, "inline")
        }).sortedBy { it.first }
        
        for (match in allMatches) {
            val (start, end, type) = match
            
            if (currentIndex < start) {
                val textContent = content.substring(currentIndex, start)
                if (textContent.isNotEmpty()) {
                    elements.add(MessageElement.Text(textContent))
                }
            }
            
            when (type) {
                "block" -> {
                    val codeMatch = codeBlockMatches.find { it.range.first == start }
                    if (codeMatch != null) {
                        val language = codeMatch.groupValues[1]
                        val code = codeMatch.groupValues[2].trim()
                        elements.add(MessageElement.CodeBlock(code, language))
                    }
                }
                "inline" -> {
                    val inlineMatch = inlineCodeMatches.find { it.range.first == start }
                    if (inlineMatch != null) {
                        val code = inlineMatch.groupValues[1]
                        elements.add(MessageElement.InlineCode(code))
                    }
                }
            }
            
            currentIndex = end + 1
        }
        
        if (currentIndex < content.length) {
            val remainingText = content.substring(currentIndex)
            if (remainingText.isNotEmpty()) {
                elements.add(MessageElement.Text(remainingText))
            }
        }
        
        if (elements.isEmpty() && content.isNotEmpty()) {
            elements.add(MessageElement.Text(content))
        }
        
        return elements
    }
}
