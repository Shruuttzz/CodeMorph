package com.codemorph.backend.service.roadmap;


import java.util.List;

public interface DependencyCycleDetector {

    List<List<String>> findCycles();
}