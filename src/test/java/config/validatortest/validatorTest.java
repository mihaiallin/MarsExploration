package config.validatortest;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidatorImpl;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class validatorTest {
    ConfigurationValidator configurationValidator = new ConfigurationValidatorImpl();

    //    @ParameterizedTest
//    @CsvSource({
//            "src/main/resources/exploration-0.map"
////            "src/main/resources/exploration-1.map",
////            "src/main/resources/exploration-2.map"
//    })
    @Test
    public void validateConfigObject() {
        Configuration configurationTrue = new Configuration("src/main/resources/exploration-1.map", new Coordinate(1, 2), List.of("#", "&", "*", "%"), 5);
        Configuration configurationFalse = new Configuration("src/main/resources/exploration-1.map", new Coordinate(1, 20), List.of("#", "&", "*", "%"), 5);

        assertTrue(configurationValidator.validateConfigurationObject(configurationTrue));
        assertFalse(configurationValidator.validateConfigurationObject(configurationFalse));
    }
}
