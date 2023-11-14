package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.List;

public interface CommandCenter {
    String getName();

    Coordinate getLocation();

    int getStatus();

    void setStatus(int status);

    HashMap<String, List<Coordinate>> getResourcesOnStock();

    void setResourcesOnStock(HashMap<String, List<Coordinate>> resourcesOnStock);

    void incrementStatus();
}
