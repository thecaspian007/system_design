package com.systemdesign.designpatterns.creational.abstractfactory;

/**
 * Abstract Factory Pattern Implementation
 * 
 * The Abstract Factory pattern provides an interface for creating families of related objects
 * without specifying their concrete classes.
 */

// Abstract Products
interface Button {
    void paint();
}

interface Checkbox {
    void paint();
}

interface TextBox {
    void paint();
}

// Concrete Products - Windows Family
class WindowsButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering Windows Button");
    }
}

class WindowsCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering Windows Checkbox");
    }
}

class WindowsTextBox implements TextBox {
    @Override
    public void paint() {
        System.out.println("Rendering Windows TextBox");
    }
}

// Concrete Products - MacOS Family
class MacOSButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering MacOS Button");
    }
}

class MacOSCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering MacOS Checkbox");
    }
}

class MacOSTextBox implements TextBox {
    @Override
    public void paint() {
        System.out.println("Rendering MacOS TextBox");
    }
}

// Concrete Products - Linux Family
class LinuxButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering Linux Button");
    }
}

class LinuxCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering Linux Checkbox");
    }
}

class LinuxTextBox implements TextBox {
    @Override
    public void paint() {
        System.out.println("Rendering Linux TextBox");
    }
}

// Abstract Factory
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
    TextBox createTextBox();
}

// Concrete Factories
class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
    
    @Override
    public TextBox createTextBox() {
        return new WindowsTextBox();
    }
}

class MacOSFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacOSButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new MacOSCheckbox();
    }
    
    @Override
    public TextBox createTextBox() {
        return new MacOSTextBox();
    }
}

class LinuxFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new LinuxButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new LinuxCheckbox();
    }
    
    @Override
    public TextBox createTextBox() {
        return new LinuxTextBox();
    }
}

// Client Application
class Application {
    private Button button;
    private Checkbox checkbox;
    private TextBox textBox;
    
    public Application(GUIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
        this.textBox = factory.createTextBox();
    }
    
    public void paint() {
        button.paint();
        checkbox.paint();
        textBox.paint();
    }
}

// Factory Provider
class FactoryProvider {
    public static GUIFactory getFactory(String osType) {
        switch (osType.toLowerCase()) {
            case "windows":
                return new WindowsFactory();
            case "macos":
                return new MacOSFactory();
            case "linux":
                return new LinuxFactory();
            default:
                throw new IllegalArgumentException("Unknown OS type: " + osType);
        }
    }
}

public class AbstractFactoryPattern {
    public static void main(String[] args) {
        System.out.println("=== Abstract Factory Pattern Demo ===");
        
        // Simulate different operating systems
        String[] operatingSystems = {"windows", "macos", "linux"};
        
        for (String os : operatingSystems) {
            System.out.println("\n--- Creating GUI for " + os.toUpperCase() + " ---");
            
            GUIFactory factory = FactoryProvider.getFactory(os);
            Application app = new Application(factory);
            app.paint();
        }
        
        // Alternative approach - detecting OS at runtime
        System.out.println("\n=== Auto-detecting OS ===");
        String currentOS = System.getProperty("os.name").toLowerCase();
        String osType;
        
        if (currentOS.contains("win")) {
            osType = "windows";
        } else if (currentOS.contains("mac")) {
            osType = "macos";
        } else if (currentOS.contains("nix") || currentOS.contains("nux")) {
            osType = "linux";
        } else {
            osType = "linux"; // default
        }
        
        System.out.println("Detected OS: " + osType);
        GUIFactory autoFactory = FactoryProvider.getFactory(osType);
        Application autoApp = new Application(autoFactory);
        autoApp.paint();
    }
} 