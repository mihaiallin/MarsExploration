package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

public class TimeoutAnalyzer implements OutcomeAnalyzer {
    @Override
    public boolean hasReachedOutcome(MarsRover rover, SimulationContext context, Configuration configuration) {

        context.setExplorationOutcome(rover, ExplorationOutcome.TIMEOUT);
        return context.getNumberOfSteps() >= context.getTimeoutSteps();
    }
}
