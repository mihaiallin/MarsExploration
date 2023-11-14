package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidatorImpl;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class InitializeRover {
    private final ConfigurationValidator configurationValidator = new ConfigurationValidatorImpl();

    public MarsRover initializeRover(Coordinate coordinate, int sight, HashMap<String, List<Coordinate>> resources, Configuration configuration) {
        List<Coordinate> emptySpots = configurationValidator.checkAdjacentCoordinate(coordinate, configuration);
        Random randomPosition = new Random();
        if (!emptySpots.isEmpty()) {
            Coordinate randomCoordinate = emptySpots.get(randomPosition.nextInt(emptySpots.size()));
            return new MarsRover(randomCoordinate, sight, resources);
        }
        return null;
    }
}