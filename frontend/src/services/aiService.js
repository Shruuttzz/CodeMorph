export async function sendAiMessage(message, context = "") {
    const response = await fetch("/api/ai/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ message, context }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
            errorText || `AI request failed: ${response.status}`
        );
    }

    return response.json();
}