package hospital;

public class Doctor {
    public String id, name, specialty;
    public boolean available = true;
    public Doctor next, prev; // Para lista doblemente enlazada

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return String.format("Dr.%s (%s) - %s", name, id, specialty);
    }
}
