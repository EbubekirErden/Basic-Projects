public class City {
    public String cityName; //data field that contains data of city name
    public int x; //data field that contains data of x-coordinate of city
    public int y; //data field that contains data of y-coordinate of city

    /**
     * Constructor method
     * @param cityName //data input that contains data of city name
     * @param x //data input that contains data of x-coordinate of city
     * @param y //data input that contains data of y-coordinate of city
     */
    public City(String cityName, int x, int y) {
        this.cityName = cityName;
        this.x = x;
        this.y = y;
    }
}