package com.codemorph.backend.service.roadmap;

import com.codemorph.backend.model.roadmap.MigrationRoadmap;
import com.codemorph.backend.model.ComponentAnalysis;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public interface MigrationRoadmapService {

    MigrationRoadmap generateRoadmap(
            Graph<String, DefaultEdge> dependencyGraph,
            List<ComponentAnalysis> components);
}
