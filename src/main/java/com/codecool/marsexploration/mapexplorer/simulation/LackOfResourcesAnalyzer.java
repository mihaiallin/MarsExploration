package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

public class LackOfResourcesAnalyzer implements OutcomeAnalyzer {
    private static final double EXPLORED_PERCENTAGE_THRESHOLD = 0.10; // Adjust this threshold as needed

    @Override
    public boolean hasReachedOutcome(MarsRover rover, SimulationContext context, Configuration configuration) {
        context.setExplorationOutcome(rover, ExplorationOutcome.ERROR);
        return isAlmostExplored(context) && noRightConditionsForColonization(rover, context);
    }

    private boolean isAlmostExplored(SimulationContext context) {
        int totalCoordinates = 33 * 33;
        int visitedCoordinates = context.getNumberOfSteps();
        return (double) visitedCoordinates / totalCoordinates >= EXPLORED_PERCENTAGE_THRESHOLD;
    }

    private boolean noRightConditionsForColonization(MarsRover rover, SimulationContext context) {
        return !(rover.getResources().containsKey("&")
                && rover.getResources().containsKey("#")
                && rover.getResources().containsKey("%")
                && rover.getResources().containsKey("*"));
    }
}

