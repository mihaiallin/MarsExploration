package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenter;
import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenterImpl;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.database.DatabaseManager;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.logger.ConsoleLogger;
import com.codecool.marsexploration.mapexplorer.logger.FileLogger;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

import java.util.*;

public class ExplorationSimulator {
    private SimulationContext simulationContext;
    private ConfigurationValidator configurationValidator;
    private Configuration configuration;

    private List<OutcomeAnalyzer> analyzers;
    private FileLogger fileLogger;

    public ExplorationSimulator(FileLogger fileLogger, SimulationContext simulationContext, ConfigurationValidator configurationValidator, Configuration configuration) {
        this.simulationContext = simulationContext;
        this.configuration = configuration;
        this.configurationValidator = configurationValidator;
        this.analyzers = new ArrayList<>();
        this.analyzers.add(new SuccessAnalyzer());
        this.analyzers.add(new LackOfResourcesAnalyzer());
        this.analyzers.add(new TimeoutAnalyzer());
        this.fileLogger = fileLogger;
    }

    public void startExploring() {
        Logger consoleLogger = new ConsoleLogger();
        String dbFile = "src/main/resources/ResourcesMars.db";
        DatabaseManager databaseManager = new DatabaseManager(dbFile, consoleLogger);
        fileLogger.clearLogFile();
        Map<MarsRover, List<Coordinate>> visitedCoordinate = new HashMap<>();
        for (int i = 0; i < simulationContext.getTimeoutSteps(); i++) {
            for (MarsRover rover : simulationContext.getRover()) {
                ExplorationOutcome simOutcomeRover = simulationContext.getExplorationOutcome().get(rover);
                int simulationStep = simulationContext.getNumberOfSteps();
                String roverName = rover.getName();
                CommandCenter commandCenterRover = simulationContext.getCommandCenterMap().get(rover);
                //todo: change if to switch and move to another method
                if (simOutcomeRover != null) {
                    switch (simOutcomeRover) {
                        case COLONIZABLE:
                            colonizationFaze(simulationStep, simOutcomeRover, roverName, rover, databaseManager);
                            break;

                        case CONSTRUCTION:
                            if (commandCenterRover.getStatus() >= 10) {
                                simulationContext.setExplorationOutcome(rover, ExplorationOutcome.EXTRACTIONS);
                                databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
                                databaseManager.addConstructionEvent(commandCenterRover.getName(), commandCenterRover.getStatus() + "/10", roverName, simulationStep);
                                continue;
                            }
                            constructionFaze(commandCenterRover, rover, simulationStep, simOutcomeRover, roverName, databaseManager);
//                            commandCenterRover.incrementStatus();
//                            commandCenterRover.setResourcesOnStock(rover.getResources());
//                            int status = commandCenterRover.getStatus();
//                            fileLogger.logInfo("STEP " + simulationStep + "STATUS " + status + "/10 " + "; EVENT " + simOutcomeRover + "; UNIT " + roverName + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
//                            fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + roverName);
//                            databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
//                            databaseManager.addConstructionEvent(commandCenterRover.getName(), commandCenterRover.getStatus() + "/10", roverName, simulationStep);
                            break;

                        case EXTRACTIONS:
//                            fileLogger.logInfo("STEP " + simulationStep + "; EVENT " + simOutcomeRover + "; UNIT " + roverName + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
//                            fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + roverName);
//                            databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
                            System.out.println(simOutcomeRover);
                            extractionFaze(simulationStep, simOutcomeRover, rover, roverName, databaseManager);

                        case DONE_EXTRACTION:
                            fileLogger.logInfo("STEP " + simulationStep + "; EVENT " + simOutcomeRover + "; UNIT " + roverName + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
                            fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + roverName);
                            rover.setCurrentPosition(commandCenterRover.getLocation());
                            simulationContext.setExplorationOutcome(rover, ExplorationOutcome.EXTRACTIONS);
                            commandCenterRover.setResourcesOnStock(rover.getResources());
                            rover.setResources(new HashMap<>());
                            databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
                            break;
                    }
                } else {
                    simOutcomeRover = ExplorationOutcome.TIMEOUT;
                }
                roverTravelSteps(rover, visitedCoordinate);
            }
            simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);
        }
        configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordinate, simulationContext.getCommandCenterMap());

    }

    private void roverTravelSteps(MarsRover rover, Map<MarsRover, List<Coordinate>> visitedCoordinate) {
        List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(rover.getCurrentPosition(), configuration);
        Random random = new Random();
        if (!adjacentCoordinate.isEmpty()) {
            Coordinate roverPosition = rover.getCurrentPosition();
            Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
            if (!visitedCoordinate.containsKey(rover)) {
                visitedCoordinate.put(rover, new ArrayList<>(Collections.singleton(roverPosition)));
            } else {
                visitedCoordinate.get(rover).add(roverPosition);
            }
            rover.setCurrentPosition(newRandomRoverPosition);
            if (configurationValidator.checkAdjacentCoordinate(roverPosition, configuration).size() < 8) {
                rover.setResources(findResources(configuration, rover.getCurrentPosition()));
            }

            if (simulationContext.getExplorationOutcome().get(rover) != ExplorationOutcome.EXTRACTIONS) {
                fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + rover.getName() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
                fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
                isOutcomeReached(rover, simulationContext, configuration);
            } else {
                if (rover.getResources().size() >= 3) {
                    simulationContext.setExplorationOutcome(rover, ExplorationOutcome.DONE_EXTRACTION);
                }
                fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT extraction; UNIT " + rover.getName() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
                fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
            }
        }
    }

    public HashMap<String, List<Coordinate>> findResources(Configuration configuration, Coordinate currentRoverPosition) {
        List<String> mapLoader = new MapLoaderImpl().readAllLines(configuration.map());
        String map = String.join("", mapLoader);
        HashMap<String, List<Coordinate>> resourcesMap = new HashMap<>();
        int startX = (currentRoverPosition.X() == 0 ? 0 : currentRoverPosition.X() - 1);
        int startY = (currentRoverPosition.Y() == 0 ? 0 : currentRoverPosition.Y() - 1);
        int stopX = (currentRoverPosition.X() == mapLoader.size() - 1 ? currentRoverPosition.X() : currentRoverPosition.X() + 1);
        int stopY = (currentRoverPosition.Y() == mapLoader.size() - 1 ? currentRoverPosition.Y() : currentRoverPosition.Y() + 1);
        for (int i = startX; i <= stopX; i++) {
            for (int j = startY; j <= stopY; j++) {
                try {
                    char symbol = map.charAt(i * (mapLoader.size() - 1) + j);
                    if (symbol != ' ' && symbol != '\n') {
                        String symbolKey = Character.toString(symbol);
                        Coordinate coordinate = new Coordinate(j, i);
                        resourcesMap.computeIfAbsent(symbolKey, k -> new ArrayList<>()).add(coordinate);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        return resourcesMap;
    }

    private boolean isOutcomeReached(MarsRover rover, SimulationContext context, Configuration configuration) {
        return analyzers.stream()
                .anyMatch(analyzer -> analyzer.hasReachedOutcome(rover, context, configuration));
    }

    private void colonizationFaze(int simulationStep, ExplorationOutcome simOutcomeRover, String roverName, MarsRover rover, DatabaseManager databaseManager) {
        fileLogger.logInfo("STEP " + simulationStep + "; EVENT " + simOutcomeRover + "; UNIT " + roverName + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
        fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + roverName);
        databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
        simulationContext.setExplorationOutcome(rover, ExplorationOutcome.CONSTRUCTION);
        simulationContext.setCommandCenterMap(rover, new CommandCenterImpl(rover.getId(), rover.getCurrentPosition(), 0, rover.getResources()));
        rover.setResources(new HashMap<>());
    }

    private void constructionFaze(CommandCenter commandCenterRover, MarsRover rover, int simulationStep, ExplorationOutcome simOutcomeRover, String roverName, DatabaseManager databaseManager) {
        commandCenterRover.incrementStatus();
        commandCenterRover.setResourcesOnStock(rover.getResources());
        int status = commandCenterRover.getStatus();
        fileLogger.logInfo("STEP " + simulationStep + "STATUS " + status + "/10 " + "; EVENT " + simOutcomeRover + "; UNIT " + roverName + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
        fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + roverName);
        databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
        databaseManager.addConstructionEvent(commandCenterRover.getName(), commandCenterRover.getStatus() + "/10", roverName, simulationStep);
    }

    private void extractionFaze(int simulationStep, ExplorationOutcome simOutcomeRover, MarsRover rover, String roverName, DatabaseManager databaseManager) {
        fileLogger.logInfo("STEP " + simulationStep + "; EVENT " + simOutcomeRover + "; UNIT " + roverName + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
        fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + roverName);
        databaseManager.addRover(roverName, simulationStep, rover.getResources().toString(), simOutcomeRover.toString());
    }
}

//string 1st line, string second line
//                if (simOutcomeRover == ExplorationOutcome.COLONIZABLE) {
//                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT " + simOutcomeRover + "; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
//                    fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
//                    databaseManager.addRover(rover.getName(), simulationContext.getNumberOfSteps(), rover.getResources().toString(), simOutcomeRover.toString());
//                    simulationContext.setExplorationOutcome(rover, ExplorationOutcome.CONSTRUCTION);
//                    simulationContext.setCommandCenterMap(rover, new CommandCenterImpl(rover.getId(), rover.getCurrentPosition(), 0, rover.getResources()));
//                    rover.setResources(new HashMap<>());
//                    continue;
//                }
//                if (simOutcomeRover == ExplorationOutcome.CONSTRUCTION) {
//                    if (simulationContext.getCommandCenterMap().get(rover).getStatus() >= 10) {
//                        simulationContext.setExplorationOutcome(rover, ExplorationOutcome.EXTRACTIONS);
//                        databaseManager.addRover(rover.getName(), simulationContext.getNumberOfSteps(), rover.getResources().toString(), simOutcomeRover.toString());
//                        databaseManager.addConstructionEvent(simulationContext.getCommandCenterMap().get(rover).getName(), simulationContext.getCommandCenterMap().get(rover).getStatus() + "/10", rover.getName(), simulationContext.getNumberOfSteps());
//                        continue;
//                    }
//                    simulationContext.getCommandCenterMap().get(rover).incrementStatus();
//                    simulationContext.getCommandCenterMap().get(rover).setResourcesOnStock(rover.getResources());
//                    int status = simulationContext.getCommandCenterMap().get(rover).getStatus();
//                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "STATUS " + status + "/10 " + "; EVENT " + simOutcomeRover + "; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
//                    fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + rover.getName());
//                    databaseManager.addRover(rover.getName(), simulationContext.getNumberOfSteps(), rover.getResources().toString(), simulationContext.getExplorationOutcome().get(rover).toString());
//                    databaseManager.addConstructionEvent(simulationContext.getCommandCenterMap().get(rover).getName(), simulationContext.getCommandCenterMap().get(rover).getStatus() + "/10", rover.getName(), simulationContext.getNumberOfSteps());
//
//                    continue;
//                }
//                if (simOutcomeRover == ExplorationOutcome.EXTRACTIONS) {
//                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT " + simOutcomeRover + "; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
//                    fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + rover.getName());
//                    databaseManager.addRover(rover.getName(), simulationContext.getNumberOfSteps(), rover.getResources().toString(), simOutcomeRover.toString());
//                }
//                if (simOutcomeRover == ExplorationOutcome.DONE_EXTRACTION) {
//                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT " + simOutcomeRover + "; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
//                    fileLogger.logInfo("OUTCOME " + simOutcomeRover + " for " + rover.getName());
//                    rover.setCurrentPosition(simulationContext.getCommandCenterMap().get(rover).getLocation());
//                    simulationContext.setExplorationOutcome(rover, ExplorationOutcome.EXTRACTIONS);
//                    simulationContext.getCommandCenterMap().get(rover).setResourcesOnStock(rover.getResources());
//                    rover.setResources(new HashMap<>());
//                    databaseManager.addRover(rover.getName(), simulationContext.getNumberOfSteps(), rover.getResources().toString(), simOutcomeRover.toString());
//
//                    continue;
//                }