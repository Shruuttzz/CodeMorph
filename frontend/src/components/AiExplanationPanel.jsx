
import { useState } from "react";
import { sendAiMessage } from "../services/aiService";

export default function AiExplanationPanel({ context = "" }) {
    const [open, setOpen] = useState(false);
    const [question, setQuestion] = useState("");
    const [answer, setAnswer] = useState("");
    const [provider, setProvider] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function askAI(prompt) {
        setLoading(true);
        setError("");
        setAnswer("");

        try {
            const result = await sendAiMessage(prompt, context);
            setAnswer(result.answer ?? "No answer was returned.");
            setProvider(result.provider ?? "AI");
        } catch (err) {
            setError(err.message || "Could not contact CodeMorph AI.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <section className="ai-explanation">
            <button type="button" onClick={() => setOpen(!open)}>
                ✨ {open ? "Hide AI Assistant" : "Explain with AI"}
            </button>

            {open && (
                <div className="ai-explanation-panel">
                    <h3>Understand your migration</h3>
                    <p>
                        Ask about migration risks, roadmap phases, dependencies,
                        or recommendations in simple language.
                    </p>

                    <div className="ai-quick-actions">
                        <button
                            type="button"
                            disabled={loading}
                            onClick={() =>
                                askAI(
                                    "Explain these migration analysis results in simple English. " +
                                    "Identify the biggest risks and explain why they matter."
                                )
                            }
                        >
                            Explain my results
                        </button>

                        <button
                            type="button"
                            disabled={loading}
                            onClick={() =>
                                askAI(
                                    "Recommend practical steps to reduce migration risks. " +
                                    "Prioritize the recommendations and explain why."
                                )
                            }
                        >
                            Get recommendations
                        </button>

                        <button
                            type="button"
                            disabled={loading}
                            onClick={() =>
                                askAI(
                                    "Explain the migration roadmap step by step in simple English. " +
                                    "Explain what to do first and what to verify before proceeding."
                                )
                            }
                        >
                            Explain roadmap
                        </button>
                    </div>

                    <form
                        onSubmit={(event) => {
                            event.preventDefault();
                            if (question.trim()) askAI(question.trim());
                        }}
                    >
                        <label htmlFor="ai-question">
                            Ask your own question
                        </label>

                        <textarea
                            id="ai-question"
                            value={question}
                            onChange={(event) => setQuestion(event.target.value)}
                            placeholder="Why is this component high risk?"
                            rows={3}
                        />

                        <button type="submit" disabled={loading || !question.trim()}>
                            Ask AI
                        </button>
                    </form>

                    {loading && <p>Analyzing your results...</p>}
                    {error && <p role="alert">{error}</p>}

                    {answer && (
                        <div className="ai-answer">
                            <h4>CodeMorph AI's explanation</h4>
                            <p style={{ whiteSpace: "pre-wrap" }}>{answer}</p>
                            <small>Provider: {provider}</small>
                        </div>
                    )}
                </div>
            )}
        </section>
    );
}
