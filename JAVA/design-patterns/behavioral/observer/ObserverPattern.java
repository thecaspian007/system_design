package com.systemdesign.designpatterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern Implementation
 * 
 * The Observer pattern defines a one-to-many dependency between objects
 * so that when one object changes state, all its dependents are notified
 * and updated automatically.
 */

// Observer interface
interface Observer {
    void update(String message);
}

// Subject interface
interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}

// Concrete Subject - News Agency
class NewsAgency implements Subject {
    private List<Observer> observers;
    private String news;
    
    public NewsAgency() {
        this.observers = new ArrayList<>();
    }
    
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
        System.out.println("Observer registered: " + observer.getClass().getSimpleName());
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("Observer removed: " + observer.getClass().getSimpleName());
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
    
    public String getNews() {
        return news;
    }
}

// Concrete Observers
class NewsChannel implements Observer {
    private String channelName;
    
    public NewsChannel(String channelName) {
        this.channelName = channelName;
    }
    
    @Override
    public void update(String message) {
        System.out.println(channelName + " received news: " + message);
    }
}

class MobileApp implements Observer {
    private String appName;
    
    public MobileApp(String appName) {
        this.appName = appName;
    }
    
    @Override
    public void update(String message) {
        System.out.println(appName + " app notification: " + message);
    }
}

class EmailSubscriber implements Observer {
    private String email;
    
    public EmailSubscriber(String email) {
        this.email = email;
    }
    
    @Override
    public void update(String message) {
        System.out.println("Email sent to " + email + ": " + message);
    }
}

// Weather Station Example
interface WeatherObserver {
    void update(float temperature, float humidity, float pressure);
}

class WeatherStation {
    private List<WeatherObserver> observers;
    private float temperature;
    private float humidity;
    private float pressure;
    
    public WeatherStation() {
        this.observers = new ArrayList<>();
    }
    
    public void registerObserver(WeatherObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers() {
        for (WeatherObserver observer : observers) {
            observer.update(temperature, humidity, pressure);
        }
    }
    
    public void measurementsChanged() {
        notifyObservers();
    }
    
    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }
}

class CurrentConditionsDisplay implements WeatherObserver {
    private float temperature;
    private float humidity;
    private WeatherStation weatherStation;
    
    public CurrentConditionsDisplay(WeatherStation weatherStation) {
        this.weatherStation = weatherStation;
        weatherStation.registerObserver(this);
    }
    
    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }
    
    public void display() {
        System.out.println("Current conditions: " + temperature + "°C and " + humidity + "% humidity");
    }
}

class StatisticsDisplay implements WeatherObserver {
    private float maxTemp = 0.0f;
    private float minTemp = 200;
    private float tempSum = 0.0f;
    private int numReadings;
    private WeatherStation weatherStation;
    
    public StatisticsDisplay(WeatherStation weatherStation) {
        this.weatherStation = weatherStation;
        weatherStation.registerObserver(this);
    }
    
    @Override
    public void update(float temperature, float humidity, float pressure) {
        tempSum += temperature;
        numReadings++;
        
        if (temperature > maxTemp) {
            maxTemp = temperature;
        }
        
        if (temperature < minTemp) {
            minTemp = temperature;
        }
        
        display();
    }
    
    public void display() {
        System.out.println("Avg/Max/Min temperature = " + (tempSum / numReadings) + "/" + maxTemp + "/" + minTemp);
    }
}

class ForecastDisplay implements WeatherObserver {
    private float currentPressure = 29.92f;
    private float lastPressure;
    private WeatherStation weatherStation;
    
    public ForecastDisplay(WeatherStation weatherStation) {
        this.weatherStation = weatherStation;
        weatherStation.registerObserver(this);
    }
    
    @Override
    public void update(float temperature, float humidity, float pressure) {
        lastPressure = currentPressure;
        currentPressure = pressure;
        display();
    }
    
    public void display() {
        System.out.print("Forecast: ");
        if (currentPressure > lastPressure) {
            System.out.println("Improving weather on the way!");
        } else if (currentPressure == lastPressure) {
            System.out.println("More of the same");
        } else if (currentPressure < lastPressure) {
            System.out.println("Watch out for cooler, rainy weather");
        }
    }
}

// Stock Market Example
class Stock {
    private String symbol;
    private double price;
    private List<StockObserver> observers;
    
    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.observers = new ArrayList<>();
    }
    
    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(symbol, price);
        }
    }
    
    public void setPrice(double price) {
        this.price = price;
        notifyObservers();
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public double getPrice() {
        return price;
    }
}

interface StockObserver {
    void update(String symbol, double price);
}

class StockTrader implements StockObserver {
    private String name;
    
    public StockTrader(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String symbol, double price) {
        System.out.println("Trader " + name + " notified: " + symbol + " is now $" + price);
    }
}

class StockDisplay implements StockObserver {
    @Override
    public void update(String symbol, double price) {
        System.out.println("Stock Display: " + symbol + " - $" + price);
    }
}

public class ObserverPattern {
    public static void main(String[] args) {
        System.out.println("=== Observer Pattern Demo ===");
        
        // News Agency Example
        System.out.println("\n--- News Agency Example ---");
        NewsAgency agency = new NewsAgency();
        
        NewsChannel cnn = new NewsChannel("CNN");
        NewsChannel bbc = new NewsChannel("BBC");
        MobileApp newsApp = new MobileApp("NewsApp");
        EmailSubscriber subscriber = new EmailSubscriber("john@example.com");
        
        agency.registerObserver(cnn);
        agency.registerObserver(bbc);
        agency.registerObserver(newsApp);
        agency.registerObserver(subscriber);
        
        agency.setNews("Breaking: Major earthquake hits Japan");
        
        System.out.println("\n--- Removing BBC ---");
        agency.removeObserver(bbc);
        agency.setNews("Update: Relief efforts underway");
        
        // Weather Station Example
        System.out.println("\n--- Weather Station Example ---");
        WeatherStation weatherStation = new WeatherStation();
        
        CurrentConditionsDisplay currentDisplay = new CurrentConditionsDisplay(weatherStation);
        StatisticsDisplay statsDisplay = new StatisticsDisplay(weatherStation);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherStation);
        
        weatherStation.setMeasurements(25.0f, 65.0f, 30.4f);
        weatherStation.setMeasurements(22.0f, 70.0f, 29.2f);
        weatherStation.setMeasurements(28.0f, 90.0f, 29.2f);
        
        // Stock Market Example
        System.out.println("\n--- Stock Market Example ---");
        Stock appleStock = new Stock("AAPL", 150.00);
        Stock googleStock = new Stock("GOOGL", 2500.00);
        
        StockTrader trader1 = new StockTrader("Alice");
        StockTrader trader2 = new StockTrader("Bob");
        StockDisplay display = new StockDisplay();
        
        appleStock.addObserver(trader1);
        appleStock.addObserver(trader2);
        appleStock.addObserver(display);
        
        googleStock.addObserver(trader1);
        googleStock.addObserver(display);
        
        System.out.println("\n--- Stock Price Changes ---");
        appleStock.setPrice(155.00);
        googleStock.setPrice(2550.00);
        
        appleStock.removeObserver(trader2);
        appleStock.setPrice(160.00);
    }
} 