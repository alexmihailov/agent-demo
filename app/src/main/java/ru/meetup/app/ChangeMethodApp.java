package ru.meetup.app;

public class ChangeMethodApp {

    public static void main(String[] args) {
        System.out.println("Change method app started");
        var firstName = "Ivan";
        var lastName = "Ivanov";
        System.out.println("First name: " + firstName);
        System.out.println("Last name: " + lastName);
        var person = new Person(firstName, lastName);
        System.out.println("Full name: " + person.getFullName());
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
}
