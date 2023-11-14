package com.codecool.marsexploration.mapexplorer.database;

public interface Resources {
    void add(String name, int steps, int amountOfResources, String outcome);

    void deleteAll();
}

