package Entity;

public class Person {
    protected String name;
    protected String dateOfBirth;
    protected String gender;
    protected String phoneNumber;

    public Person(String name, String dateOfBirth, String gender, String phoneNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public String getName() { return name; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }
}