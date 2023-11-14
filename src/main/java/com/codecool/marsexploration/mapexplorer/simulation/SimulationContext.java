package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationContext {
    private int numberOfSteps;
    private int timeoutSteps;
    private List<MarsRover> rovers;
    private Coordinate spaceshipLocation;
    private String map;
    private Map<MarsRover, HashMap<String, List<Coordinate>>> monitoredResources;
    private Map<MarsRover, ExplorationOutcome> explorationOutcome = new HashMap<>();

    private Map<MarsRover, CommandCenter> commandCenterMap;

    public SimulationContext(int numberOfSteps, int timeoutSteps, List<MarsRover> rovers,
                             Coordinate spaceshipLocation, String map,
                             Map<MarsRover, HashMap<String, List<Coordinate>>> monitoredResources,
                             Map<MarsRover, CommandCenter> commandCenterMap) {
        this.numberOfSteps = numberOfSteps;
        this.timeoutSteps = timeoutSteps;
        this.rovers = rovers;
        this.spaceshipLocation = spaceshipLocation;
        this.map = map;
        this.monitoredResources = monitoredResources;
        this.commandCenterMap = commandCenterMap;
    }

    public Map<MarsRover, CommandCenter> getCommandCenterMap() {
        return commandCenterMap;
    }

    public void setCommandCenterMap(MarsRover rover, CommandCenter commandCenter) {
        getCommandCenterMap().put(rover, commandCenter);
        this.commandCenterMap = getCommandCenterMap();
    }

    public Map<MarsRover, ExplorationOutcome> getExplorationOutcome() {
        return explorationOutcome;
    }

    public void setExplorationOutcome(MarsRover rover, ExplorationOutcome explorationOutcome) {
        getExplorationOutcome().put(rover, explorationOutcome);
        this.explorationOutcome = getExplorationOutcome();
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public int getTimeoutSteps() {
        return timeoutSteps;
    }

    public List<MarsRover> getRover() {
        return rovers;
    }

    public Coordinate getSpaceshipLocation() {
        return spaceshipLocation;
    }

    public String getMap() {
        return map;
    }

    public Map<MarsRover, HashMap<String, List<Coordinate>>> getMonitoredResources() {
        return monitoredResources;
    }

    public int getNumberOfResources() {
        int amountOfResources = 0;
        for (Map.Entry<MarsRover, HashMap<String, List<Coordinate>>> entry : monitoredResources.entrySet()) {
            HashMap<String, List<Coordinate>> resourcesMap = entry.getValue();
            for (List<Coordinate> resourceList : resourcesMap.values()) {
                amountOfResources += resourceList.size();
            }
        }
        return amountOfResources;
    }

    public void setNumberOfSteps(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setMonitoredResources(MarsRover rover, HashMap<String, List<Coordinate>> monitoredResources) {
        this.monitoredResources.put(rover, monitoredResources);
    }
}
