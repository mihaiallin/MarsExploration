package com.codecool.marsexploration.mapexplorer.maploader;

import com.codecool.marsexploration.mapexplorer.maploader.model.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapLoaderImpl implements MapLoader {
    @Override
    public Map load(String mapFile) {
        List<String> allLinesRead = readAllLines(mapFile);
        String[][] representation = getRepresentation(allLinesRead);

        return new Map(representation, true);
    }

    public List<String> readAllLines(String mapFile) {
        List<String> allLines = new ArrayList<>();
        try {
            File myObj = new File(mapFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                allLines.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return allLines;
    }

    private String[][] getRepresentation(List<String> allLinesRead) {
        String[] stringsLines = allLinesRead.toArray(new String[0]);
        String[][] representation = new String[allLinesRead.size()][allLinesRead.size()];
        for (int i = 0; i < stringsLines.length; i++) {
            String[] allCharFromLine = stringsLines[i].split("");
            System.arraycopy(allCharFromLine, 0, representation[i], 0, allCharFromLine.length);
        }
        return representation;
    }

}
