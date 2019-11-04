package co.id.ramadanrizky.todofirebasefix3.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherForecast {

    private String description;
    private int date;
    private String icon;
    private String temperature;

    public WeatherForecast(JSONObject object){

        try {
            String desc = object.getJSONArray("weather").getJSONObject(0).getString("description");
            String icon = object.getJSONArray("weather").getJSONObject(0).getString("icon");
            String temp = object.getJSONObject("main").getString("temp");
            int date = Integer.parseInt(object.getString("dt"));

            this.description = desc;
            this.icon = icon;
            this.temperature = temp;
            this.date = date;
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public WeatherForecast() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
