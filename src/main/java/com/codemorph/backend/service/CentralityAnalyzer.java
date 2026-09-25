package com.codemorph.backend.service;


import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CentralityAnalyzer {

    public void analyze(
            Graph<String, DefaultEdge> graph,
            List<ComponentAnalysis> components) {

        if (graph == null || graph.vertexSet().isEmpty()) {
            return;
        }

        PageRank<String, DefaultEdge> pageRank =
                new PageRank<>(graph);

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

            double score =
                    pageRank.getVertexScore(node);

            component.setCentrality(score);
        }
    }
}