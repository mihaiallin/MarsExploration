package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

public interface OutcomeAnalyzer {
    boolean hasReachedOutcome(MarsRover rover, SimulationContext context, Configuration configuration);
}

