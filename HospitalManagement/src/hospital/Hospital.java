package hospital;

import java.util.*;

public class Hospital {

    public static String[] specialties = {"Urgencias","Pediatría","Cirugía","Medicina Interna","Cardiología"};
    public static int[] triageLevels = {1,2,3,4,5};

    public static BedState [][] beds;
    public static Patient[][] bedAssignments;

    public static Deque<Patient> historyStack = new ArrayDeque<>();
    public static Queue<Patient>[] waitingQueues = new LinkedList[6];

    public static HospNode hospitalizedHead = null;
    public static Doctor doctorsHead = null;
    public static ShiftNode currentShift = null;
    public static BSTNode patientRoot = null;

    // Inicializaciones
    public static void initBeds(int rows, int cols) {
        beds = new BedState[rows][cols];
        bedAssignments = new Patient[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                beds[i][j] = BedState.FREE;
    }

    public static void initWaitingQueues() {
        for (int i = 1; i <= 5; i++) waitingQueues[i] = new LinkedList<>();
    }

    public static void addDoctor(String id, String name, String specialty) {
        Doctor d = new Doctor(id, name, specialty);
        if (doctorsHead == null) doctorsHead = d;
        else {
            Doctor tail = doctorsHead;
            while (tail.next != null) tail = tail.next;
            tail.next = d;
            d.prev = tail;
        }
    }

    public static void initShifts(String... shifts) {
        if (shifts.length == 0) return;
        ShiftNode first = new ShiftNode(shifts[0]);
        ShiftNode cur = first;
        for (int i = 1; i < shifts.length; i++) {
            cur.next = new ShiftNode(shifts[i]);
            cur = cur.next;
        }
        cur.next = first;
        currentShift = first;
    }

    // --- BST ---
    public static void bstInsert(Patient p) {
        patientRoot = bstInsertRec(patientRoot, p);
    }

    private static BSTNode bstInsertRec(BSTNode node, Patient p) {
        if (node == null) return new BSTNode(p);
        int cmp = p.id.compareTo(node.p.id);
        if (cmp < 0) node.left = bstInsertRec(node.left, p);
        else if (cmp > 0) node.right = bstInsertRec(node.right, p);
        else node.p = p;
        return node;
    }

    public static Patient bstSearch(String id) {
        BSTNode n = patientRoot;
        while (n != null) {
            int c = id.compareTo(n.p.id);
            if (c == 0) return n.p;
            n = c < 0 ? n.left : n.right;
        }
        return null;
    }

    // --- Hospitalizados ---
    public static void addHospitalized(Patient p) {
        HospNode node = new HospNode(p);
        node.next = hospitalizedHead;
        hospitalizedHead = node;
    }

    public static boolean removeHospitalized(String id) {
        HospNode cur = hospitalizedHead, prev = null;
        while (cur != null) {
            if (cur.p.id.equals(id)) {
                if (prev == null) hospitalizedHead = cur.next;
                else prev.next = cur.next;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    public static List<Patient> listHospitalized() {
        List<Patient> list = new ArrayList<>();
        HospNode cur = hospitalizedHead;
        while (cur != null) {
            list.add(cur.p);
            cur = cur.next;
        }
        return list;
    }

    // --- Camas ---
    public static boolean assignBedIfAvailable(Patient p) {
        for (int i = 0; i < beds.length; i++) {
            for (int j = 0; j < beds[i].length; j++) {
                if (beds[i][j] == BedState.FREE) {
                    beds[i][j] = BedState.OCCUPIED;
                    bedAssignments[i][j] = p;
                    p.bedRow = i;
                    p.bedCol = j;
                    return true;
                }
            }
        }
        return false;
    }

    public static void releaseBed(Patient p) {
        if (p.bedRow >= 0) {
            beds[p.bedRow][p.bedCol] = BedState.FREE;
            bedAssignments[p.bedRow][p.bedCol] = null;
            p.bedRow = -1;
            p.bedCol = -1;
        }
    }

    // --- Cola de espera ---
    public static void enqueuePatient(Patient p) {
        int t = Math.min(Math.max(p.triage, 1), 5);
        waitingQueues[t].add(p);
        bstInsert(p);
    }

    public static Patient dequeueNext() {
        for (int t = 1; t <= 5; t++) {
            if (!waitingQueues[t].isEmpty()) return waitingQueues[t].poll();
        }
        return null;
    }

    // --- Doctores ---
    public static Doctor findAvailableDoctor() {
        Doctor cur = doctorsHead;
        while (cur != null) {
            if (cur.available) return cur;
            cur = cur.next;
        }
        return null;
    }

    // --- Turnos ---
    public static void advanceShift() {
        if (currentShift != null) currentShift = currentShift.next;
    }

    // --- Atención ---
    public static void attendNextPatient() {
        Patient p = dequeueNext();
        if (p == null) {
            System.out.println("No patients waiting.");
            return;
        }
        System.out.println("Attending: " + p);
        boolean gotBed = assignBedIfAvailable(p);
        if (gotBed) {
            System.out.println("Assigned bed to " + p.name + " at (" + p.bedRow + "," + p.bedCol + ")");
            addHospitalized(p);
        } else System.out.println("No beds available.");

        Doctor d = findAvailableDoctor();
        if (d != null) {
            d.available = false;
            System.out.println("Assigned doctor: " + d);
        } else System.out.println("No available doctors.");

        historyStack.push(p);
    }

    public static void undoLastAttention() {
        if (historyStack.isEmpty()) {
            System.out.println("History empty.");
            return;
        }
        Patient p = historyStack.pop();
        System.out.println("Undoing last attention for: " + p);
        releaseBed(p);
        removeHospitalized(p.id);
    }

    // --- Mostrar datos ---
    public static void showBedMatrix() {
        System.out.println("Beds matrix:");
        for (int i = 0; i < beds.length; i++) {
            for (int j = 0; j < beds[i].length; j++) {
                char c = beds[i][j] == BedState.FREE ? 'F' :
                        (beds[i][j] == BedState.OCCUPIED ? 'O' : 'M');
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public static void showHospitalized() {
        System.out.println("Hospitalized patients:");
        for (Patient p : listHospitalized()) System.out.println(p);
    }

    public static void showWaiting() {
        System.out.println("Waiting queues:");
        for (int t = 1; t <= 5; t++) {
            System.out.print("Triage " + t + ": ");
            for (Patient p : waitingQueues[t]) System.out.print(p.name + "(" + p.id + "), ");
            System.out.println();
        }
    }
}
