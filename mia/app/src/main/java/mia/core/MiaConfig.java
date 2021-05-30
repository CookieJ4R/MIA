package mia.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * This class provides access to the config which allows simple declaration of values like ips, token and settings
 */
public class MiaConfig {

    private Map<String, String> propertyMap = new HashMap<>();

    public MiaConfig(){
        loadConfig();
    }

    /***
     * Loads the Config file or creates an empty file if the file doesnt exist yet.
     */
    private void loadConfig(){

        try {
            File logFile = new File("../mia.cfg");
            logFile.createNewFile();
            List<String> lines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);

            lines.forEach(line -> {String[] splittedLine = line.split("=");propertyMap.put(splittedLine[0], splittedLine[1]);});

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * Return the value of passed property defined in the config file
     * @param property the property to lookup
     * @return the value of the passed property
     */
    public String getProperty(String property) {
        return propertyMap.get(property);
    }
}
