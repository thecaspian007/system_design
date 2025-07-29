package com.systemdesign.designpatterns.creational.singleton;

/**
 * Singleton Pattern Implementation
 * 
 * The Singleton pattern ensures that a class has only one instance and provides
 * global access to that instance.
 */
public class SingletonPattern {
    
    // Eager Initialization
    static class EagerSingleton {
        private static final EagerSingleton INSTANCE = new EagerSingleton();
        
        private EagerSingleton() {}
        
        public static EagerSingleton getInstance() {
            return INSTANCE;
        }
        
        public void doSomething() {
            System.out.println("Eager Singleton: Doing something...");
        }
    }
    
    // Lazy Initialization (Thread-Safe)
    static class LazySingleton {
        private static volatile LazySingleton instance;
        
        private LazySingleton() {}
        
        public static LazySingleton getInstance() {
            if (instance == null) {
                synchronized (LazySingleton.class) {
                    if (instance == null) {
                        instance = new LazySingleton();
                    }
                }
            }
            return instance;
        }
        
        public void doSomething() {
            System.out.println("Lazy Singleton: Doing something...");
        }
    }
    
    // Bill Pugh Solution (Recommended)
    static class BillPughSingleton {
        private BillPughSingleton() {}
        
        private static class SingletonHelper {
            private static final BillPughSingleton INSTANCE = new BillPughSingleton();
        }
        
        public static BillPughSingleton getInstance() {
            return SingletonHelper.INSTANCE;
        }
        
        public void doSomething() {
            System.out.println("Bill Pugh Singleton: Doing something...");
        }
    }
    
    // Enum Singleton (Best Practice)
    enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {
            System.out.println("Enum Singleton: Doing something...");
        }
    }
    
    public static void main(String[] args) {
        // Testing different singleton implementations
        System.out.println("=== Singleton Pattern Demo ===");
        
        // Eager Singleton
        EagerSingleton eagerSingleton1 = EagerSingleton.getInstance();
        EagerSingleton eagerSingleton2 = EagerSingleton.getInstance();
        System.out.println("Eager Singleton - Same instance: " + (eagerSingleton1 == eagerSingleton2));
        eagerSingleton1.doSomething();
        
        // Lazy Singleton
        LazySingleton lazySingleton1 = LazySingleton.getInstance();
        LazySingleton lazySingleton2 = LazySingleton.getInstance();
        System.out.println("Lazy Singleton - Same instance: " + (lazySingleton1 == lazySingleton2));
        lazySingleton1.doSomething();
        
        // Bill Pugh Singleton
        BillPughSingleton billPughSingleton1 = BillPughSingleton.getInstance();
        BillPughSingleton billPughSingleton2 = BillPughSingleton.getInstance();
        System.out.println("Bill Pugh Singleton - Same instance: " + (billPughSingleton1 == billPughSingleton2));
        billPughSingleton1.doSomething();
        
        // Enum Singleton
        EnumSingleton enumSingleton1 = EnumSingleton.INSTANCE;
        EnumSingleton enumSingleton2 = EnumSingleton.INSTANCE;
        System.out.println("Enum Singleton - Same instance: " + (enumSingleton1 == enumSingleton2));
        enumSingleton1.doSomething();
    }
} 