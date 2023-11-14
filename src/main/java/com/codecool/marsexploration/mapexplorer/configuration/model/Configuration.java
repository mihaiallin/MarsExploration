package com.codecool.marsexploration.mapexplorer.configuration.model;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.List;

public record Configuration(String map, Coordinate landingSpot, List<String> symbols, int steps) {
}