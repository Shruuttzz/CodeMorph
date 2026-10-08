package com.codemorph.backend.service.roadmap;


import com.codemorph.backend.model.roadmap.MigrationStep;
import com.codemorph.backend.model.roadmap.MigrationWave;

import java.util.List;
import java.util.Map;

public interface MigrationRoadmapScheduler {

    List<MigrationWave> schedule(
            MigrationDependencyResolver resolver,
            Map<String, MigrationStep> components);
}
