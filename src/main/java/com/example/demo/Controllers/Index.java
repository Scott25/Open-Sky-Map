package com.example.demo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.servlet.ModelAndView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

@Controller
public class Index {

    private BufferedReader readBuffer;
    private InputStream inputStream;
    private JsonObject jsonObject;
    private List<List<Float>> finalCoordinatesList;
    private List<Float> singleCoordinate;
    private List<String> flightsList;
    private List<String> flightStatus;
    private List<List<String>> flightStatusLists;

    private final String[] colorCodes = new String[]{"#fcba03", "#fc5203", "#b5fc03", "#03f4fc", "#0f03fc", "#e703fc", "#fc0324"};

    Index() throws IOException {

        int index = 0;
        String openSkyAPIUrl = "https://opensky-network.org/api/states/all";
        URL url;

        System.out.println("Please wait... Data is loading from API.");

        try {
            url = new URL(openSkyAPIUrl);
            inputStream = url.openStream();
            readBuffer = new BufferedReader(new InputStreamReader(inputStream));
            jsonObject = new JsonParser().parse(readBuffer.readLine()).getAsJsonObject();
            flightsList = new ArrayList(2);

            while(jsonObject.get("states").getAsJsonArray().size() > index) {
                if(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(1).toString() != " " &&
                        jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(1).toString().length() > 3) {
                    flightsList.add(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(1).toString());
                }
                index++;
            }
        } catch (ConnectException connectionException) {
            System.out.println("Connection Timeout .... ");
        }
    }

    @GetMapping("/singleFlight")
    public ModelAndView Index() {
        ModelAndView indexView = new ModelAndView("leaf");
        indexView.addObject("flightsList", this.flightsList);
        return indexView;
    }

    @PostMapping("/getData")
    @ResponseBody
    public String getOpenSkyData(@RequestParam("flight") String requestedFlightNumber) throws IOException {
        int index = 0;
        String openSkyAPIUrl = "https://opensky-network.org/api/states/all";

        URL url = new URL(openSkyAPIUrl);
        String flight_number = requestedFlightNumber;

        try {
            inputStream = url.openStream();
            readBuffer = new BufferedReader(new InputStreamReader(inputStream));
            jsonObject = new JsonParser().parse(readBuffer.readLine()).getAsJsonObject();
            finalCoordinatesList = new ArrayList();
            singleCoordinate = new ArrayList(4);
            try {
                while(jsonObject.get("states").getAsJsonArray().size() > index) {
                    if(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(1).toString()
                            .contains(flight_number)) {
                        singleCoordinate = new ArrayList(4);
                        singleCoordinate.add(Float.valueOf(jsonObject.get("states").getAsJsonArray().get(index)
                                .getAsJsonArray().get(5).toString()));
                        singleCoordinate.add(Float.valueOf(jsonObject.get("states").getAsJsonArray().get(index)
                                .getAsJsonArray().get(6).toString()));
                        singleCoordinate.add(Float.valueOf(jsonObject.get("states").getAsJsonArray().get(index)
                                .getAsJsonArray().get(9).toString()));
                        singleCoordinate.add(Float.valueOf(jsonObject.get("states").getAsJsonArray().get(index)
                                .getAsJsonArray().get(6).toString()));
                        singleCoordinate.add(Float.valueOf(jsonObject.get("states").getAsJsonArray().get(index)
                                .getAsJsonArray().get(7).toString()));
                        finalCoordinatesList.add(singleCoordinate);
                        break;
                    }
                    index++;
                }

            } catch (NumberFormatException numberFormatException) {
                System.out.println(numberFormatException);
            }
            String json = new Gson().toJson(finalCoordinatesList.toArray());
            return json;
        } catch (ConnectException connectionException) {
            return connectionException.getMessage();
        }
    }

    @GetMapping("/allData")
    @ResponseBody
    public String getAllDataForMapChart() throws IOException{

        int index = 1;
        int limit = 5;

        String openSkyAPIUrl = "https://opensky-network.org/api/states/all";
        URL url = new URL(openSkyAPIUrl);

        try {
            inputStream = url.openStream();
            readBuffer = new BufferedReader(new InputStreamReader(inputStream));
            jsonObject = new JsonParser().parse(readBuffer.readLine()).getAsJsonObject();
            flightStatusLists = new ArrayList();
            flightStatus = new ArrayList(6);

            while(jsonObject.get("states").getAsJsonArray().size() > index) {
                flightStatus = new ArrayList(6);
                flightStatus.add(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(1).toString()
                        .replace('"', ' '));
                flightStatus.add(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(9).toString());
                flightStatus.add(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(11).toString());
                flightStatus.add(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(5).toString());
                flightStatus.add(jsonObject.get("states").getAsJsonArray().get(index).getAsJsonArray().get(6).toString());
                flightStatus.add(colorCodes[index]);
                flightStatusLists.add(flightStatus);
                if(index == limit) {
                    break;
                }
                index++;
            }
            String json = new Gson().toJson(flightStatusLists.toArray());
            return json;
        } catch (ConnectException connectionException) {
            return connectionException.getMessage();
        }
    }

    @GetMapping("/")
    public ModelAndView allView() {
        return new ModelAndView("chart");
    }
}
