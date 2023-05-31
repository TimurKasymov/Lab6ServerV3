package src.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class SettingsModel {
    @JsonProperty("local_port")
    public Integer localPort;
    @JsonProperty("db_url")
    public String dbUrl;
    @JsonProperty("db_user")
    public String dbUser;
    @JsonProperty("db_psw")
    public String dbPsw;
    @JsonProperty("package_bytes_size")
    public Integer packageSize;
    @JsonProperty("host")
    public String host;
}
