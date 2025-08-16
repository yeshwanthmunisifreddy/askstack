## Android RAG Assistant (OpenAI Assistants API + Jetpack Compose)

Build a fully client-side Android chat assistant that streams responses from OpenAI Assistants API v2 and retrieves grounded answers via File Search / Vector Stores. Written in Kotlin with Jetpack Compose, Hilt, Retrofit/OkHttp (SSE), and Room. No backend server required.

### Highlights
- **Direct device → OpenAI**: Retrofit client calls OpenAI Assistants API directly.
- **Streaming UX**: Smooth, cancellable token-by-token typing effect via SSE.
- **RAG-ready**: Uses your OpenAI Assistant configured with File Search/Vector Stores for retrieval-augmented answers and citations.
- **Local persistence**: Conversations and messages stored with Room.
- **Modern Android**: Kotlin, Compose, Hilt, Coroutines/Flow.

---

## Architecture

```
UI (Jetpack Compose)            ChatScreen, MessageBlock, ChatInput
        ↓
Presentation (ViewModel)        ChatViewModel (MVVM, StateFlow)
        ↓
Domain                          UseCases (CreateConversation, SendMessage, GetMessages)
        ↓
Data                            ChatRepositoryImpl (OpenAI + Room)
        ├─ Remote               OpenAIApiService (Assistants v2, SSE streaming)
        └─ Local                Room (conversations/messages), Encrypted prefs (API key)
```

Key components:
- `OpenAIApiService`: minimal Assistants v2 surface (create thread, add message, create run + SSE).
- `OpenAIStreamHandler`: parses server-sent events into domain `StreamEvent`s (queued, delta, completed, citations, etc.).
- `ChatRepositoryImpl`: orchestrates sending messages, handling streaming updates, persistence, cancellations.
- `ChatDatabase` + DAOs: Room storage for conversations and messages.
- `SecurePreferences`: stores API key and default assistant id securely (EncryptedSharedPreferences) with BuildConfig fallbacks.

---

## How RAG is integrated

The app defers RAG to your OpenAI Assistant configuration:
- Enable the "File Search" tool and attach a **Vector Store** with your documents in the OpenAI dashboard.
- The app simply references the `assistant_id` and streams answers. Citations (file quotes) are parsed and can be displayed in the UI.

Note: This app does not upload files or manage vector stores. Prepare the Assistant and vector store outside the app, then plug the `assistant_id` into the app.

---

## Setup

### Prerequisites
- Android Studio Koala+ (or latest stable)
- JDK 11+
- Android SDK 27+
- An OpenAI API key and an Assistant (v2) with File Search enabled (optional but recommended for RAG)

### Configure secrets
Add the following to your project-level `local.properties` (not committed):

```properties
OPENAI_API_KEY=sk-xxxx...            # required
OPENAI_ASSISTANT_ID=asst_xxxx...     # optional (can be set in-app)
```

These are injected into `BuildConfig` in `app/build.gradle.kts` and read by `SecurePreferences`. You can also set/change them at runtime via encrypted storage (see `SecurePreferences`).

### Build & run

```bash
./gradlew :app:installDebug
```

Open the app and start a new chat. If no `OPENAI_ASSISTANT_ID` is provided in `local.properties`, the UI can be wired to let users set it (code supports reading/storing a default id).

---

## Configuration

The following flags are defined in `BuildConfig` via `app/build.gradle.kts`:

- `ENABLE_MOCK_STREAMING` (default: `false`) – Use a fake, local streaming generator for offline/dev demos.
- `ENABLE_SMOOTH_TYPING` (default: `true`) – Enables word-by-word streaming for a natural typing effect.
- `TYPING_SPEED_MULTIPLIER` (default: `4.0f`) – Scales delays between token/word updates.

Adjust values in `defaultConfig` and rebuild.

---

## Networking & Streaming

- Base URL: `https://api.openai.com/`
- Headers: `Authorization: Bearer <API_KEY>`, `OpenAI-Beta: assistants=v2`
- Endpoints used:
  - `POST /v1/threads` – create a thread per conversation
  - `POST /v1/threads/{thread_id}/messages` – add user message
  - `POST /v1/threads/{thread_id}/runs` (with `Accept: text/event-stream`) – start run and stream events
  - `GET /v1/threads/{thread_id}/runs/{run_id}` – optional polling (not required when streaming)

`OpenAIStreamHandler` parses SSE lines of form `data: {json}` and maps them to `StreamEvent`:
- `RunQueued`, `RunInProgress`, `RunCompleted`, `RunFailed`
- `MessageDelta(content)` – partial text updates
- `MessageCompleted(content, citations)` – final assistant message with citations
- `Done` – `[DONE]` marker received

OkHttp timeouts are tuned for streaming (no read timeout). Logging is set to `HEADERS` to avoid huge bodies.

---

## Persistence

Room database `ChatDatabase` stores:
- `ConversationEntity`: id, title, threadId, assistantId, timestamps, last message
- `MessageEntity`: id, conversationId, role, content, status, `is_streaming`, optional `sources` (json)

DAOs expose reactive `Flow` for UI updates. Messages are updated as deltas arrive; streaming flags are toggled appropriately, and content plus citations are persisted.

---

## UI & Usage

- `ChatScreen` wires Compose UI to `ChatViewModel`.
- `ChatViewModel.initializeChat(conversationId?, assistantId?, initialMessage?)` creates a new thread or loads an existing one.
- `sendMessage()` triggers `ChatRepositoryImpl.sendMessage`, which:
  1. saves the user message,
  2. creates/streams the run,
  3. updates the assistant message incrementally,
  4. persists citations and final state.
- `stopStreaming()` cancels the active job and closes the SSE body.

If your Assistant has File Search enabled and a vector store attached, the app will receive citations in `MessageCompleted` that you can render (see `MessageParser`/`SourcesSection`).

---

## Security & Privacy

- API keys are loaded from `BuildConfig.OPENAI_API_KEY` or stored securely via `EncryptedSharedPreferences` (`SecurePreferences`).
- There is no app-hosted backend; requests go directly to OpenAI.
- Be mindful of data you send; it will be transmitted to OpenAI’s API.

---

## Troubleshooting

- 401 Unauthorized: Verify `OPENAI_API_KEY` and that the key has Assistants API access enabled.
- 404/400 when creating runs: Ensure your `assistant_id` is valid and points to an Assistants v2 assistant with File Search enabled (if you expect RAG behavior).
- No streaming output: Check network/proxy for SSE support; confirm `Accept: text/event-stream` and timeouts.
- Empty citations: Your Assistant might not have access to a vector store or relevant files.

---

## Extending

- Uploading files and managing vector stores in-app (currently handled outside the app).
- Tool invocation (function calling) support when Assistants require actions.
- Attachments and images (Vision models) via Assistants API.
- Paging and search across conversations; message editing and re-run.

---

## License

MIT (see project root `LICENSE`).


