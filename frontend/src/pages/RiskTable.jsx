import React from "react";

export default function RiskTable({
                                      components,
                                      onSelect
                                  }) {

    if (!components || components.length === 0) {
        return <p>No component analysis available.</p>;
    }

    const sorted =
        [...components]
            .sort(
                (a, b) =>
                    b.riskScore - a.riskScore
            );

    return (
        <div>
            <h2>Component Migration Risk</h2>

            <table>
                <thead>
                <tr>
                    <th>Component</th>
                    <th>Difficulty</th>
                    <th>Impact</th>
                    <th>Risk</th>
                    <th>Fan-in</th>
                    <th>Blast Radius</th>
                    <th>Deprecated APIs</th>
                </tr>
                </thead>

                <tbody>
                {sorted.map((component) => (

                    <tr
                        key={component.className}
                        onClick={() =>
                            onSelect(component)
                        }
                        style={{
                            cursor: "pointer"
                        }}
                    >

                        <td>
                            {component.className}
                        </td>

                        <td>
                            {component.difficultyScore}
                        </td>

                        <td>
                            {component.impactScore}
                        </td>

                        <td>
                            {component.riskScore}
                        </td>

                        <td>
                            {component.fanIn}
                        </td>

                        <td>
                            {component.blastRadius}
                        </td>

                        <td>
                            {component.deprecatedApiCount}
                        </td>

                    </tr>

                ))}
                </tbody>
            </table>
        </div>
    );
}