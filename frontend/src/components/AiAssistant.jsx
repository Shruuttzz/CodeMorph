
import { useState } from "react";
import { sendAiMessage } from "../services/aiService";

export default function AiAssistant() {
    const [message, setMessage] = useState("");
    const [conversation, setConversation] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function handleSubmit(event) {
        event.preventDefault();

        const question = message.trim();
        if (!question || loading) return;

        setConversation((previous) => [
            ...previous,
            { role: "user", text: question },
        ]);
        setMessage("");
        setLoading(true);
        setError("");

        try {
            const result = await sendAiMessage(question);

            setConversation((previous) => [
                ...previous,
                {
                    role: "assistant",
                    text: result.answer,
                    provider: result.provider,
                    fallbackUsed: result.fallbackUsed,
                },
            ]);
        } catch (err) {
            setError(err.message || "Unable to contact the AI service.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <section style={{ maxWidth: 760, margin: "32px auto", padding: 20 }}>
            <h2>CodeMorph AI Assistant</h2>
            <p>Ask questions about Java dependencies and migration risks.</p>

            <div
                style={{
                    minHeight: 180,
                    maxHeight: 400,
                    overflowY: "auto",
                    border: "1px solid #ccc",
                    borderRadius: 8,
                    padding: 16,
                    marginBottom: 16,
                }}
            >
                {conversation.length === 0 && (
                    <p>Ask your first question to get started.</p>
                )}

                {conversation.map((item, index) => (
                    <div key={index} style={{ marginBottom: 16 }}>
                        <strong>
                            {item.role === "user" ? "You" : "CodeMorph AI"}
                        </strong>
                        <p style={{ whiteSpace: "pre-wrap" }}>{item.text}</p>

                        {item.provider && (
                            <small>
                                Provider: {item.provider}
                                {item.fallbackUsed ? " (fallback used)" : ""}
                            </small>
                        )}
                    </div>
                ))}

                {loading && <p>Thinking…</p>}
            </div>

            <form onSubmit={handleSubmit}>
        <textarea
            value={message}
            onChange={(event) => setMessage(event.target.value)}
            placeholder="Ask about dependency coupling or migration risk..."
            rows={3}
            style={{ width: "100%", boxSizing: "border-box", padding: 12 }}
            disabled={loading}
        />

                <button type="submit" disabled={loading || !message.trim()}>
                    {loading ? "Thinking..." : "Ask CodeMorph AI"}
                </button>
            </form>

            {error && <p role="alert" style={{ color: "crimson" }}>{error}</p>}
        </section>
    );
}
