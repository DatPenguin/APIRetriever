package net.ddns.dankest.apiretriever;

import net.ddns.dankest.apiretriever.util.CityInfos;
import net.ddns.dankest.apiretriever.util.CityWeather;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

class APIRetriever {

    private static final String CITY = "Pontoise";

    public static void main(String[] args) {
        try {
            System.out.println(generateJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject generateJSON() throws JSONException, IOException {
        JSONObject output = new JSONObject();
        CityWeather cw;
        CityInfos ci;
        ci = retrieveFromWikipedia();
        cw = retrieveFromOWM();
        output.put("name", ci.name);
        output.put("desc", ci.desc);
        output.put("weather", cw.weather);
        output.put("temp", cw.temp);
        return output;
    }

    private static CityWeather retrieveFromOWM() throws IOException, JSONException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + APIRetriever.CITY + "&appid=60098300f6125b63aa03fa46379c0b72&units=metric");
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();

        String body = IOUtils.toString(in);
        JSONObject obj = new JSONObject(body);

        String weather = obj.getJSONArray("weather").getJSONObject(0).getString("description");
        String temp = obj.getJSONObject("main").getString("temp");

        return new CityWeather(weather, temp);
    }

    private static CityInfos retrieveFromWikipedia() throws IOException, JSONException {
        URL url = new URL("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + APIRetriever.CITY);
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();

        String body = IOUtils.toString(in);
        JSONObject obj = new JSONObject(body);
        Iterator keys = obj.getJSONObject("query").getJSONObject("pages").keys();
        String pageID = (String) keys.next();

        String name = obj.getJSONObject("query").getJSONObject("pages").getJSONObject(pageID).getString("title");

        String desc = obj.getJSONObject("query").getJSONObject("pages").getJSONObject(pageID).getString("extract");
        return new CityInfos(name, desc);
    }
}
