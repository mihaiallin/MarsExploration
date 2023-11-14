package com.codecool.marsexploration.mapexplorer.maploader;

import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class MapLoaderImplTest {
    @ParameterizedTest
    @CsvSource({
            "src/main/resources/exploration-0.map",
            "src/main/resources/exploration-1.map",
            "src/main/resources/exploration-2.map"
    })
    public void loadTest(String mapFile){
        MapLoader mapLoader = new MapLoaderImpl();
        Map map = mapLoader.load(mapFile);
        assertEquals(map.getDimension(), 32);
        int numberOfWhiteSpaces = map.getDimension();
        int totalNumberOfElements = (int) Math.pow(map.getDimension(),2)+numberOfWhiteSpaces;
        assertEquals(totalNumberOfElements, map.toString().length());
    }
}
