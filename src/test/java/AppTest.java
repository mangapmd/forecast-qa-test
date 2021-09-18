import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AppTest {
    private static final String NEWLINE = System.getProperty("line.separator");
    private static ByteArrayOutputStream consoleText;
    private static int systemExitCode = -1;

    static WireMockServer wireMockServer;

    @BeforeAll
    static void setUpWM() {
        // Start the WireMockServer
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        // configure host and port as per the ones specified in the App main method
        configureFor("localhost", 8080);

        // ==========Mock response for Location Search - ex: api/location/search/?query=dubai=============== //
        stubFor(get(urlMatching("/api/location/search/.*"))
                .willReturn(aResponse()
                        .withBody("[" +
                                "    {" +
                                "        \"title\": \"Dubai\"," +
                                "        \"location_type\": \"City\"," +
                                "        \"woeid\": 1940345," +
                                "        \"latt_long\": \"25.269440,55.308651\"" +
                                "    }" +
                                "]")));

        // ==========Mock response for Location Day - ex: api/location/(woeid)/(date)=============== //
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        stubFor(get(urlMatching("/api/location/[0-9]+.*"))
                .willReturn(aResponse()
                        .withBody("[" +
                                "    {" +
                                "        \"id\": 4710221864763392," +
                                "        \"weather_state_name\": \"Heavy Cloud\"," +
                                "        \"weather_state_abbr\": \"hc\"," +
                                "        \"wind_direction_compass\": \"SE\"," +
                                "        \"created\": \"2021-09-17T15:59:02.346517Z\"," +
                                "        \"applicable_date\": \"+tomorrow+\"," +
                                "        \"min_temp\": 14.495000000000001," +
                                "        \"max_temp\": 22.39," +
                                "        \"the_temp\": 20.845," +
                                "        \"wind_speed\": 4.482044872613272," +
                                "        \"wind_direction\": 143.5168057849848," +
                                "        \"air_pressure\": 1014.5," +
                                "        \"humidity\": 61," +
                                "        \"visibility\": 11.346237970253718," +
                                "        \"predictability\": 71" +
                                "    }," +
                                "    {" +
                                "        \"id\": 4845204667367424," +
                                "        \"weather_state_name\": \"Heavy Cloud\"," +
                                "        \"weather_state_abbr\": \"hc\"," +
                                "        \"wind_direction_compass\": \"SE\"," +
                                "        \"created\": \"2021-09-17T12:59:02.779531Z\"," +
                                "        \"applicable_date\": \"+tomorrow+\"," +
                                "        \"min_temp\": 14.535," +
                                "        \"max_temp\": 22.5," +
                                "        \"the_temp\": 20.799999999999997," +
                                "        \"wind_speed\": 4.623303005299716," +
                                "        \"wind_direction\": 142.8352051823654," +
                                "        \"air_pressure\": 1014.5," +
                                "        \"humidity\": 62," +
                                "        \"visibility\": 11.966366420106578," +
                                "        \"predictability\": 71" +
                                "    }]")));

    }

    @BeforeAll
    static void setUp() {
        // Store output to verify the expected response from the command line
        consoleText = new ByteArrayOutputStream();
        System.setOut(new PrintStream(consoleText));
    }


    /*
     * SUCCESS scenario: When a city value(any string) is sent
     * Verify System exit code is 0 (no error), and
     * Response contains 'Weather on (<tomorrow's date in yyyy/MM/dd format>)'
     */
    @Test
    public void testAppWithValidCity() throws Exception {

        // call the main method for weather app, with a city parameter
        systemExitCode = catchSystemExit(() -> {
            App.main(new String[]{"dubai"});
        });

        //check for exit code 0 which indicates no error
        assertEquals(0, systemExitCode);
        //check for output contains 'Weather on' with tomorrow' date, indicates success response
        String tomoDateInRsp = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String expectedResponse = String.format("Weather on (%s)", tomoDateInRsp);
        assertTrue(consoleText.toString().contains(expectedResponse));
    }


    /*
     * ERROR scenario: When no city input parameter is given
     * Verify System exit code is 1 (error), and
     * Response equals 'Pass city name as an argument<new line>'
     */
    @Test
    public void testAppWithNoArg() throws Exception {
        // call to the main method for the weather app
        systemExitCode = catchSystemExit(() -> {
            App.main(new String[]{});
        });
        //check for the system exit code 1, indicating error
        assertEquals(1, systemExitCode);

        //check for the valid error message
        String expectedErrorResponse = String.format("%s", "Pass city name as an argument");
        assertTrue(consoleText.toString().equals(expectedErrorResponse + NEWLINE));
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

}