package hospital;

public class Patient {
    public String id;
    public String name;
    public int triage;
    public String diagnosis;
    public int bedRow = -1, bedCol = -1;

    public Patient(String id, String name, int triage) {
        this.id = id;
        this.name = name;
        this.triage = triage;
    }

    @Override
    public String toString() {
        return String.format("Patient[%s - %s, triage=%d, bed=%s]",
                id, name, triage, (bedRow >= 0 ? "(" + bedRow + "," + bedCol + ")" : "none"));
    }
}
