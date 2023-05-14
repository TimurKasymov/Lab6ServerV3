package src.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import src.settings.SettingsModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsContainer {

    private static SettingsModel settings;
    public static void loadSettings(String fileName) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        var inputStream = classloader.getResourceAsStream(fileName);
        if (inputStream == null){
            System.out.println("settings file not found");
            System.exit(-1);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        var str = sb.toString();
        ObjectMapper mapper = new ObjectMapper();
        settings = mapper.readValue(str, SettingsModel.class);
    }
    public static SettingsModel getSettings(){
        return settings;
    }
}
