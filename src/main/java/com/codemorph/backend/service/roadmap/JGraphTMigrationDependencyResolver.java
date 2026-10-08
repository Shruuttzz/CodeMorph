package com.codemorph.backend.service.roadmap;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class JGraphTMigrationDependencyResolver
        implements MigrationDependencyResolver {

    private final Graph<String, DefaultEdge> graph;

    public JGraphTMigrationDependencyResolver(
            Graph<String, DefaultEdge> graph) {

        this.graph = graph;
    }

    @Override
    public Set<String> getComponents() {

        return new LinkedHashSet<>(
                graph.vertexSet()
        );
    }

    @Override
    public Set<String> getDependencies(
            String component) {

        if (!graph.containsVertex(component)) {
            return Collections.emptySet();
        }

        Set<String> dependencies =
                new LinkedHashSet<>();

        for (DefaultEdge edge :
                graph.outgoingEdgesOf(component)) {

            dependencies.add(
                    graph.getEdgeTarget(edge)
            );
        }

        return dependencies;
    }

    @Override
    public Set<String> getDependents(
            String component) {

        if (!graph.containsVertex(component)) {
            return Collections.emptySet();
        }

        Set<String> dependents =
                new LinkedHashSet<>();

        for (DefaultEdge edge :
                graph.incomingEdgesOf(component)) {

            dependents.add(
                    graph.getEdgeSource(edge)
            );
        }

        return dependents;
    }
}