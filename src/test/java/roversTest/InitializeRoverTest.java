package roversTest;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidatorImpl;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.InitializeRover;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitializeRoverTest {
    InitializeRover initializeRover = new InitializeRover();

    @ParameterizedTest
    @CsvSource({"7,5","12,1","3,18","18,0","4,2","23,7","30,30", "17,2", "0,2"})
    void roverInit(int x, int y) {
        String mapFileName = "src/main/resources/exploration-0.map";
        Configuration mapConfiguration = new Configuration(mapFileName, new Coordinate(x, y), List.of("#", "&", "*", "%"), 5);
        ConfigurationValidator configurationValidator = new ConfigurationValidatorImpl();
        List<Coordinate> emptySpots = configurationValidator.checkAdjacentCoordinate(new Coordinate(x, y),mapConfiguration );
        HashMap<String, Coordinate> resources = new HashMap<>();

//        resources.put("#",new Coordinate(2, 2) );
        HashMap<String, List<Coordinate>> resourcesMap = new HashMap<>();
        List<Coordinate> hashCoordinates = new ArrayList<>();
        hashCoordinates.add(new Coordinate(2, 2));
        resourcesMap.put("#", hashCoordinates);

        MarsRover actual = initializeRover.initializeRover(new Coordinate(x, y), 2, resourcesMap, mapConfiguration);

//        System.out.println("Actual " + actual.getCurrentPosition().toString());
//        System.out.println("Empty spots: "+emptySpots);
        assertNotNull(actual);
        assertTrue(emptySpots.contains(actual.getCurrentPosition()));
    }
}