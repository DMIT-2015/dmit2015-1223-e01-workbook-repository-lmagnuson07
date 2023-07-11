package dmit2015.faces;

import dmit2015.restclient.ipgeolocation.IpGeoLocationApiResponseMpRestClient;
import dmit2015.restclient.openweathermap.CurrentWeatherApiResponse;
import dmit2015.restclient.openweathermap.CurrentWeatherApiResponseMpRestClient;
import dmit2015.restclient.ipgeolocation.IpGeoLocationApiResponse;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import java.io.Serializable;

@Named("currentOpenWeatherApiClientView")
@SessionScoped
public class OpenWeatherApiClientView implements Serializable {

    @Inject
    @ConfigProperty(name = "ORG_OPENWEATHERMAP_API_APIKEY") // The name is defined in src/main/resources/META-INF/microprofile-config.properties file or an O/S environment variable
    private String openweatherApiKey;

    @Inject
    @ConfigProperty(name = "org.openweathermap.api.DefaultCity") // The name is defined in src/main/resources/META-INF/microprofile-config.properties file or an O/S environment variable
//    @ConfigProperty(name = "org.openweathermap.api.DefaultCity", defaultValue = "Calgary") // The name is defined in src/main/resources/META-INF/microprofile-config.properties file or an O/S environment variable
    @Getter @Setter
    private String city;    // The city to get the weather for

    @Inject
    @ConfigProperty(name = "org.openweathermap.api.Units") // The name is defined in src/main/resources/META-INF/microprofile-config.properties file or an O/S environment variable
    private String units;    // // The units of measurement: standard, metric, imperial

    @Inject
    @RestClient // Inject the MP Rest Client
    private CurrentWeatherApiResponseMpRestClient _weatherRestClient;

    @Getter
    private CurrentWeatherApiResponse currentWeatherApiResponse;

    @Inject
    @ConfigProperty(name = "ORG_IPGEOLOCATION_APIKEY")
    private String ipGeoLocationApiKey;

    @Inject
    @RestClient
    private IpGeoLocationApiResponseMpRestClient _ipGeoLocationRestClient;

    @Getter
    private IpGeoLocationApiResponse ipGeoLocationApiResponse;

    @PostConstruct
    public void init() {
        String ipAddress = Faces.getRemoteAddr();
//        ipGeoLocationApiResponse = _ipGeoLocationRestClient.getGeoLocation(ipGeoLocationApiKey, ipAddress);
//        city = ipGeoLocationApiResponse.getCity();

        try {
            currentWeatherApiResponse = _weatherRestClient.getCurrentWeatherByGeographicalCoordinates(
                    ipGeoLocationApiResponse.getLatitude(),
                    ipGeoLocationApiResponse.getLongitude(),
                    openweatherApiKey,
                    units);
        } catch(Exception ex) {
            ex.printStackTrace();
            Messages.addGlobalWarn("There are no weather data for {0}", city);
        }
        doSearchByCity();
    }

    public void doSearchByCity() {
        try {
            currentWeatherApiResponse = _weatherRestClient.getCurrentWeatherByCity(city, openweatherApiKey, units);
            String message = String.format("The current weather in %s is %.2f", currentWeatherApiResponse.getName(), currentWeatherApiResponse.getMain().getTemp());
            Messages.addGlobalInfo(message);
        } catch (Exception ex) {
            ex.printStackTrace();
            Messages.addGlobalWarn("There are no weather data for {0}", city);
        }
    }

}