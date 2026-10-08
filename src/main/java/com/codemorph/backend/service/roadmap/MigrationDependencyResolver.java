package com.codemorph.backend.service.roadmap;


import java.util.Set;

public interface MigrationDependencyResolver {

    Set<String> getComponents();

    Set<String> getDependencies(String component);

    Set<String> getDependents(String component);
}