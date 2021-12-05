package ru.meetup.app;

public class ChangeMethodAppAnnotation {

    public static void main(String[] args) {
        System.out.println("Change method app started");
        var firstName = "Ivan";
        var lastName = "Ivanov";
        System.out.println("First name: " + firstName);
        System.out.println("Last name: " + lastName);
        var person = new Person(firstName, lastName);
        System.out.println("Full name: " + person.getFullName());

        var carBrand = "KIA";
        var carModel = "Cerato";
        System.out.println("Car brand: " + carBrand);
        System.out.println("Car model: " + carModel);
        var car = new Car(carBrand, carModel);
        System.out.println("Car full name: " + car.getFullName());
    }

    public static class Person {
        private final String firstName;
        private final String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    public static class Car {
        private final String brand;
        private final String model;

        public Car(String brand, String model) {
            this.brand = brand;
            this.model = model;
        }

        public String getFullName() {
            return brand + " " + model;
        }
    }
}
