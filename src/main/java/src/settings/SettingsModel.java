package src.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class SettingsModel {
    @JsonProperty("local_port")
    public Integer localPort;
}
