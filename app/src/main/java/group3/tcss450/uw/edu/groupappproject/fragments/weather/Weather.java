package group3.tcss450.uw.edu.groupappproject.fragments.weather;

public class Weather {
    public String temp;
    public String date;
    public String description;
    public String wind;
    public String pressure;
    public String humidity;
    public String icon;

    public Weather(String temp, String date, String description, String wind, String pressure, String humidity, String icon) {
        this.temp = temp;
        this.date = date;
        this.description = description;
        this.wind = wind;
        this.pressure = pressure;
        this.humidity = humidity;
        this.icon = icon;

    }
}
