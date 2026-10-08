package com.codemorph.backend.service.roadmap;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DependencyCycleDetectorImpl
        implements DependencyCycleDetector {

    private final Graph<String, DefaultEdge> graph;

    public DependencyCycleDetectorImpl(
            Graph<String, DefaultEdge> graph) {

        this.graph = graph;
    }

    @Override
    public List<List<String>> findCycles() {

        KosarajuStrongConnectivityInspector<
                String,
                DefaultEdge
                > inspector =
                new KosarajuStrongConnectivityInspector<>(
                        graph
                );

        List<Set<String>> components =
                inspector.stronglyConnectedSets();

        List<List<String>> cycles =
                new ArrayList<>();

        for (Set<String> component :
                components) {

            if (component.size() > 1) {

                cycles.add(
                        new ArrayList<>(component)
                );

                continue;
            }

            String node =
                    component.iterator().next();

            if (!graph.getAllEdges(node, node)
                    .isEmpty()) {

                cycles.add(
                        new ArrayList<>(component)
                );
            }
        }

        return cycles;
    }
}
