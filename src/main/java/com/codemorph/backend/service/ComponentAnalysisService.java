package com.codemorph.backend.service;


import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.model.ParsedClass;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentAnalysisService {

    private final ComplexityAnalyzer complexityAnalyzer;

    public ComponentAnalysisService(
            ComplexityAnalyzer complexityAnalyzer) {

        this.complexityAnalyzer =
                complexityAnalyzer;
    }

    public List<ComponentAnalysis> analyze(
            List<ParsedClass> parsedClasses) {

        List<ComponentAnalysis> results =
                new ArrayList<>();

        if (parsedClasses == null) {
            return results;
        }

        for (ParsedClass parsed :
                parsedClasses) {

            try {

                ComponentAnalysis analysis =
                        complexityAnalyzer.analyze(
                                parsed.getDeclaration(),
                                parsed.getCompilationUnit(),
                                parsed.getSourceFile()
                        );

                results.add(analysis);

            }
            catch (Exception e) {

                /*
                 * One component failing should not
                 * destroy the entire repository analysis.
                 */

                System.err.println(
                        "Component analysis failed for "
                                + parsed.getClassName()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        return results;
    }
}