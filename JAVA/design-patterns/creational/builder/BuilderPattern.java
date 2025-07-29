package com.systemdesign.designpatterns.creational.builder;

/**
 * Builder Pattern Implementation
 * 
 * The Builder pattern constructs complex objects step by step.
 * It allows you to produce different types and representations of an object
 * using the same construction code.
 */

// Product class
class Computer {
    private String cpu;
    private String memory;
    private String storage;
    private String gpu;
    private String motherboard;
    private String powerSupply;
    private boolean hasWifi;
    private boolean hasBluetooth;
    
    private Computer(ComputerBuilder builder) {
        this.cpu = builder.cpu;
        this.memory = builder.memory;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
        this.motherboard = builder.motherboard;
        this.powerSupply = builder.powerSupply;
        this.hasWifi = builder.hasWifi;
        this.hasBluetooth = builder.hasBluetooth;
    }
    
    // Getters
    public String getCpu() { return cpu; }
    public String getMemory() { return memory; }
    public String getStorage() { return storage; }
    public String getGpu() { return gpu; }
    public String getMotherboard() { return motherboard; }
    public String getPowerSupply() { return powerSupply; }
    public boolean hasWifi() { return hasWifi; }
    public boolean hasBluetooth() { return hasBluetooth; }
    
    @Override
    public String toString() {
        return "Computer{" +
                "cpu='" + cpu + '\'' +
                ", memory='" + memory + '\'' +
                ", storage='" + storage + '\'' +
                ", gpu='" + gpu + '\'' +
                ", motherboard='" + motherboard + '\'' +
                ", powerSupply='" + powerSupply + '\'' +
                ", hasWifi=" + hasWifi +
                ", hasBluetooth=" + hasBluetooth +
                '}';
    }
    
    // Builder class
    public static class ComputerBuilder {
        private String cpu;
        private String memory;
        private String storage;
        private String gpu;
        private String motherboard;
        private String powerSupply;
        private boolean hasWifi;
        private boolean hasBluetooth;
        
        public ComputerBuilder(String cpu, String memory) {
            this.cpu = cpu;
            this.memory = memory;
        }
        
        public ComputerBuilder setStorage(String storage) {
            this.storage = storage;
            return this;
        }
        
        public ComputerBuilder setGpu(String gpu) {
            this.gpu = gpu;
            return this;
        }
        
        public ComputerBuilder setMotherboard(String motherboard) {
            this.motherboard = motherboard;
            return this;
        }
        
        public ComputerBuilder setPowerSupply(String powerSupply) {
            this.powerSupply = powerSupply;
            return this;
        }
        
        public ComputerBuilder setWifi(boolean hasWifi) {
            this.hasWifi = hasWifi;
            return this;
        }
        
        public ComputerBuilder setBluetooth(boolean hasBluetooth) {
            this.hasBluetooth = hasBluetooth;
            return this;
        }
        
        public Computer build() {
            return new Computer(this);
        }
    }
}

// Alternative Builder using interface
interface HouseBuilder {
    HouseBuilder setFoundation(String foundation);
    HouseBuilder setStructure(String structure);
    HouseBuilder setRoof(String roof);
    HouseBuilder setInterior(String interior);
    House build();
}

class House {
    private String foundation;
    private String structure;
    private String roof;
    private String interior;
    
    public House(String foundation, String structure, String roof, String interior) {
        this.foundation = foundation;
        this.structure = structure;
        this.roof = roof;
        this.interior = interior;
    }
    
    @Override
    public String toString() {
        return "House{" +
                "foundation='" + foundation + '\'' +
                ", structure='" + structure + '\'' +
                ", roof='" + roof + '\'' +
                ", interior='" + interior + '\'' +
                '}';
    }
}

class ConcreteHouseBuilder implements HouseBuilder {
    private String foundation;
    private String structure;
    private String roof;
    private String interior;
    
    @Override
    public HouseBuilder setFoundation(String foundation) {
        this.foundation = foundation;
        return this;
    }
    
    @Override
    public HouseBuilder setStructure(String structure) {
        this.structure = structure;
        return this;
    }
    
    @Override
    public HouseBuilder setRoof(String roof) {
        this.roof = roof;
        return this;
    }
    
    @Override
    public HouseBuilder setInterior(String interior) {
        this.interior = interior;
        return this;
    }
    
    @Override
    public House build() {
        return new House(foundation, structure, roof, interior);
    }
}

// Director class
class HouseDirector {
    private HouseBuilder builder;
    
    public HouseDirector(HouseBuilder builder) {
        this.builder = builder;
    }
    
    public House constructSimpleHouse() {
        return builder
                .setFoundation("Concrete Foundation")
                .setStructure("Wood Frame")
                .setRoof("Asphalt Shingles")
                .setInterior("Basic Interior")
                .build();
    }
    
    public House constructLuxuryHouse() {
        return builder
                .setFoundation("Reinforced Concrete Foundation")
                .setStructure("Steel Frame")
                .setRoof("Tile Roof")
                .setInterior("Luxury Interior")
                .build();
    }
}

public class BuilderPattern {
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demo ===");
        
        // Using Computer Builder
        Computer gamingComputer = new Computer.ComputerBuilder("Intel i9", "32GB DDR4")
                .setStorage("1TB SSD")
                .setGpu("RTX 4090")
                .setMotherboard("ASUS ROG")
                .setPowerSupply("850W")
                .setWifi(true)
                .setBluetooth(true)
                .build();
        
        Computer officeComputer = new Computer.ComputerBuilder("Intel i5", "16GB DDR4")
                .setStorage("512GB SSD")
                .setMotherboard("Basic Motherboard")
                .setPowerSupply("500W")
                .setWifi(true)
                .build();
        
        System.out.println("Gaming Computer: " + gamingComputer);
        System.out.println("Office Computer: " + officeComputer);
        
        // Using House Builder with Director
        System.out.println("\n=== House Builder with Director ===");
        HouseBuilder houseBuilder = new ConcreteHouseBuilder();
        HouseDirector director = new HouseDirector(houseBuilder);
        
        House simpleHouse = director.constructSimpleHouse();
        House luxuryHouse = director.constructLuxuryHouse();
        
        System.out.println("Simple House: " + simpleHouse);
        System.out.println("Luxury House: " + luxuryHouse);
    }
} 