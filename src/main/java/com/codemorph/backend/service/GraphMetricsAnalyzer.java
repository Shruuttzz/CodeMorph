package com.codemorph.backend.service;


import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;

import java.util.*;

@Service
public class GraphMetricsAnalyzer {

    public void analyze(
            Graph<String, DefaultEdge> graph,
            List<ComponentAnalysis> components) {

        if (graph == null || graph.vertexSet().isEmpty()) {
            return;
        }

        Map<String, ComponentAnalysis> componentMap =
                new HashMap<>();

        for (ComponentAnalysis component : components) {
            componentMap.put(
                    component.getClassName(),
                    component
            );
        }

        for (String node : graph.vertexSet()) {

            ComponentAnalysis component =
                    componentMap.get(node);

            if (component == null) {
                continue;
            }

            int fanIn =
                    graph.inDegreeOf(node);

            int fanOut =
                    graph.outDegreeOf(node);

            int blastRadius =
                    calculateBlastRadius(
                            graph,
                            node
                    );

            component.setFanIn(fanIn);
            component.setFanOut(fanOut);
            component.setBlastRadius(blastRadius);
        }
    }

    private int calculateBlastRadius(
            Graph<String, DefaultEdge> graph,
            String source) {

        Set<String> visited =
                new HashSet<>();

        Queue<String> queue =
                new LinkedList<>();

        /*
         * Important:
         *
         * If A depends on B:
         *
         * A → B
         *
         * and B changes, A can be affected.
         *
         * Therefore we need to traverse
         * REVERSE dependency direction.
         */

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {

            String current =
                    queue.poll();

            /*
             * predecessors = nodes with an edge
             * going INTO current.
             *
             * A → B
             *
             * predecessors(B) = A
             */

            for (String dependent :
                    org.jgrapht.Graphs.predecessorListOf(
                            graph,
                            current)) {

                if (visited.add(dependent)) {
                    queue.add(dependent);
                }
            }
        }

        /*
         * Do not count the source itself.
         */
        return Math.max(
                0,
                visited.size() - 1
        );
    }
}