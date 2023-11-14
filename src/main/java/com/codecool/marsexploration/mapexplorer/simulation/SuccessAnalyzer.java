package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

public class SuccessAnalyzer implements OutcomeAnalyzer {
    @Override
    public boolean hasReachedOutcome(MarsRover rover, SimulationContext context, Configuration configuration) {
        context.setExplorationOutcome(rover, ExplorationOutcome.COLONIZABLE);
        int sizeOfMineral = rover.getResources().get("%") != null ? rover.getResources().get("%").size() : 0;
        int sizeOfWater = rover.getResources().get("*") != null ? rover.getResources().get("*").size() : 0;
        boolean checkIfWeHaveOneMountainAndOnePit = false;
        if (rover.getResources().containsKey("#") && rover.getResources().containsKey("&")) {
            checkIfWeHaveOneMountainAndOnePit = (rover.getResources().get("#").size() >= 1 && rover.getResources().get("&").size() >= 1);
        }
        boolean checkIfWeHaveMinimOneResourceFromEach = rover.getResources().containsKey("&")
                && rover.getResources().containsKey("#")
                && rover.getResources().containsKey("%")
                && rover.getResources().containsKey("*");

        boolean checkIfTheSizeOfMineralIsBiggerThan4AndTheSizeOfWaterIsBiggerThan3 = (sizeOfMineral > 4 && sizeOfWater > 3);

        context.setExplorationOutcome(rover, ExplorationOutcome.COLONIZABLE);
        return (checkIfWeHaveOneMountainAndOnePit
                || checkIfWeHaveMinimOneResourceFromEach
                || checkIfTheSizeOfMineralIsBiggerThan4AndTheSizeOfWaterIsBiggerThan3);
    }
}
