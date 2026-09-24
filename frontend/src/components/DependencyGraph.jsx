import { useEffect, useRef } from "react";
import { Network } from "vis-network";

function DependencyGraph({ graphData }) {
    const containerRef = useRef(null);
    const networkRef = useRef(null);

    useEffect(() => {
        if (!graphData || !containerRef.current) return;

        const nodes = graphData.nodes.map((n) => ({
            id: n.id,
            label: n.label,
            shape: "box",
            color: {
                background: "#eff6ff",
                border: "#2563eb",
                highlight: {
                    background: "#dbeafe",
                    border: "#1d4ed8"
                }
            },
            font: {
                color: "#1e3a8a",
                size: 14
            },
            margin: 10
        }));

        const edges = graphData.edges.map((e) => ({
            from: e.from,
            to: e.to,
            arrows: "to",
            color: {
                color: "#94a3b8",
                highlight: "#2563eb"
            },
            smooth: {
                type: "cubicBezier",
                roundness: 0.4
            }
        }));

        const data = {
            nodes,
            edges
        };

        const options = {
            layout: {
                hierarchical: {
                    enabled: true,
                    direction: "UD",
                    sortMethod: "directed",
                    levelSeparation: 100,
                    nodeSpacing: 150
                }
            },

            physics: false,

            interaction: {
                dragNodes: true,
                zoomView: true,
                hover: true
            },

            edges: {
                width: 2
            }
        };

        networkRef.current = new Network(
            containerRef.current,
            data,
            options
        );

        return () => {
            if (networkRef.current) {
                networkRef.current.destroy();
                networkRef.current = null;
            }
        };
    }, [graphData]);

    if (!graphData || graphData.nodes.length === 0) {
        return (
            <p className="graph-empty">
                No dependency graph data available.
            </p>
        );
    }

    return (
        <div className="graph-wrapper">
            <div
                ref={containerRef}
                className="graph-canvas"
            />
        </div>
    );
}

export default DependencyGraph;