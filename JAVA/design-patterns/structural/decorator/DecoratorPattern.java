package com.systemdesign.designpatterns.structural.decorator;

/**
 * Decorator Pattern Implementation
 * 
 * The Decorator pattern allows behavior to be added to objects dynamically
 * without altering their structure. It acts as a wrapper to existing class.
 */

// Base component interface
interface Coffee {
    String getDescription();
    double getCost();
}

// Concrete component
class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
    
    @Override
    public double getCost() {
        return 2.00;
    }
}

// Base Decorator
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
    
    @Override
    public double getCost() {
        return coffee.getCost();
    }
}

// Concrete Decorators
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + 0.50;
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + 0.25;
    }
}

class ChocolateDecorator extends CoffeeDecorator {
    public ChocolateDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Chocolate";
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + 0.75;
    }
}

class WhippedCreamDecorator extends CoffeeDecorator {
    public WhippedCreamDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Whipped Cream";
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + 1.00;
    }
}

// Another example - Text Formatting
interface Text {
    String format();
}

class PlainText implements Text {
    private String text;
    
    public PlainText(String text) {
        this.text = text;
    }
    
    @Override
    public String format() {
        return text;
    }
}

abstract class TextDecorator implements Text {
    protected Text text;
    
    public TextDecorator(Text text) {
        this.text = text;
    }
    
    @Override
    public String format() {
        return text.format();
    }
}

class BoldDecorator extends TextDecorator {
    public BoldDecorator(Text text) {
        super(text);
    }
    
    @Override
    public String format() {
        return "<b>" + text.format() + "</b>";
    }
}

class ItalicDecorator extends TextDecorator {
    public ItalicDecorator(Text text) {
        super(text);
    }
    
    @Override
    public String format() {
        return "<i>" + text.format() + "</i>";
    }
}

class UnderlineDecorator extends TextDecorator {
    public UnderlineDecorator(Text text) {
        super(text);
    }
    
    @Override
    public String format() {
        return "<u>" + text.format() + "</u>";
    }
}

// File encryption example
interface DataSource {
    void writeData(String data);
    String readData();
}

class FileDataSource implements DataSource {
    private String filename;
    private String data;
    
    public FileDataSource(String filename) {
        this.filename = filename;
    }
    
    @Override
    public void writeData(String data) {
        this.data = data;
        System.out.println("Writing data to file: " + filename);
    }
    
    @Override
    public String readData() {
        System.out.println("Reading data from file: " + filename);
        return data;
    }
}

class DataSourceDecorator implements DataSource {
    private DataSource wrapper;
    
    public DataSourceDecorator(DataSource source) {
        this.wrapper = source;
    }
    
    @Override
    public void writeData(String data) {
        wrapper.writeData(data);
    }
    
    @Override
    public String readData() {
        return wrapper.readData();
    }
}

class EncryptionDecorator extends DataSourceDecorator {
    public EncryptionDecorator(DataSource source) {
        super(source);
    }
    
    @Override
    public void writeData(String data) {
        System.out.println("Encrypting data...");
        super.writeData(encode(data));
    }
    
    @Override
    public String readData() {
        System.out.println("Decrypting data...");
        return decode(super.readData());
    }
    
    private String encode(String data) {
        return "encrypted(" + data + ")";
    }
    
    private String decode(String data) {
        return data.replace("encrypted(", "").replace(")", "");
    }
}

class CompressionDecorator extends DataSourceDecorator {
    public CompressionDecorator(DataSource source) {
        super(source);
    }
    
    @Override
    public void writeData(String data) {
        System.out.println("Compressing data...");
        super.writeData(compress(data));
    }
    
    @Override
    public String readData() {
        System.out.println("Decompressing data...");
        return decompress(super.readData());
    }
    
    private String compress(String data) {
        return "compressed(" + data + ")";
    }
    
    private String decompress(String data) {
        return data.replace("compressed(", "").replace(")", "");
    }
}

public class DecoratorPattern {
    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern Demo ===");
        
        // Coffee Example
        System.out.println("\n--- Coffee Shop Example ---");
        
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new ChocolateDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new WhippedCreamDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        // Text Formatting Example
        System.out.println("\n--- Text Formatting Example ---");
        
        Text text = new PlainText("Hello World");
        System.out.println("Plain: " + text.format());
        
        text = new BoldDecorator(text);
        System.out.println("Bold: " + text.format());
        
        text = new ItalicDecorator(text);
        System.out.println("Bold + Italic: " + text.format());
        
        text = new UnderlineDecorator(text);
        System.out.println("Bold + Italic + Underline: " + text.format());
        
        // File Encryption Example
        System.out.println("\n--- File Encryption Example ---");
        
        DataSource source = new FileDataSource("data.txt");
        source.writeData("Hello World");
        System.out.println("Read: " + source.readData());
        
        System.out.println("\n--- With Encryption ---");
        source = new EncryptionDecorator(new FileDataSource("encrypted.txt"));
        source.writeData("Hello World");
        System.out.println("Read: " + source.readData());
        
        System.out.println("\n--- With Compression + Encryption ---");
        source = new CompressionDecorator(
                new EncryptionDecorator(
                    new FileDataSource("secure.txt")
                )
        );
        source.writeData("Hello World");
        System.out.println("Read: " + source.readData());
    }
} 