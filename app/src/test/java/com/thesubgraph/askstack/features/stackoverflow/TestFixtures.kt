package com.thesubgraph.askstack.features.stackoverflow

import com.thesubgraph.askstack.features.stackoverflow.domain.model.Owner
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question
import kotlinx.datetime.LocalDateTime
object TestFixtures {

    val mockOwner = Owner(
        id = 123L,
        accountId = 456L,
        reputation = 1000L,
        userType = "registered",
        profileImage = "https://example.com/profile.jpg",
        displayName = "Test User",
        link = "https://stackoverflow.com/users/123"
    )

    val mockOwner2 = Owner(
        id = 789L,
        accountId = 101L,
        reputation = 500L,
        userType = "registered",
        profileImage = "https://example.com/profile2.jpg",
        displayName = "Jane Smith",
        link = "https://stackoverflow.com/users/789"
    )

    val mockQuestion1 = Question(
        id = 1L,
        title = "How to use Kotlin coroutines?",
        body = "I want to learn about Kotlin coroutines",
        creationDate = LocalDateTime(2023, 1, 1, 12, 0),
        lastActivityDate = LocalDateTime(2023, 1, 1, 12, 0),
        lastEditDate = LocalDateTime(2023, 1, 1, 12, 0),
        score = 15,
        answerCount = 3,
        viewCount = 150,
        link = "https://stackoverflow.com/questions/1",
        isAnswered = true,
        tags = listOf("kotlin", "coroutines"),
        owner = mockOwner
    )

    val mockQuestion2 = Question(
        id = 2L,
        title = "Android lifecycle best practices",
        body = "What are the best practices for Android lifecycle?",
        creationDate = LocalDateTime(2023, 1, 2, 12, 0),
        lastActivityDate = LocalDateTime(2023, 1, 2, 12, 0),
        lastEditDate = LocalDateTime(2023, 1, 2, 12, 0),
        score = 8,
        answerCount = 1,
        viewCount = 80,
        link = "https://stackoverflow.com/questions/2",
        isAnswered = false,
        tags = listOf("android", "lifecycle"),
        owner = mockOwner2
    )

    val mockQuestions = listOf(mockQuestion1, mockQuestion2)

    val singleMockQuestion = listOf(mockQuestion1)
}
