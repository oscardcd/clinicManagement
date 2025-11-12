package hospital;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Hospital.initBeds(3, 4);
        Hospital.initWaitingQueues();

        Hospital.addDoctor("D001", "Ana", "Urgencias");
        Hospital.addDoctor("D002", "Bruno", "Pediatría");
        Hospital.addDoctor("D003", "Carla", "Cirugía");

        Hospital.initShifts("Day", "Night");

        Hospital.enqueuePatient(new Patient("P100", "Juan Perez", 2));
        Hospital.enqueuePatient(new Patient("P101", "Luisa Gomez", 1));
        Hospital.enqueuePatient(new Patient("P102", "Andres Ruiz", 4));

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Sistema de Gestion Hospitalaria ---");
            System.out.println("1) Mostrar camas");
            System.out.println("2) Mostrar pacientes en espera");
            System.out.println("3) Atender siguiente paciente");
            System.out.println("4) Deshacer ultima atencion");
            System.out.println("5) Mostrar pacientes hospitalizados");
            System.out.println("6) Buscar paciente por ID");
            System.out.println("7) Cambiar turno");
            System.out.println("0) Salir");
            System.out.print("Seleccione una opcion: ");

            int c = -1;
            try {
                c = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                continue;
            }

            switch (c) {
                case 1 ->
                    Hospital.showBedMatrix();
                case 2 ->
                    Hospital.showWaiting();
                case 3 ->
                    Hospital.attendNextPatient();
                case 4 ->
                    Hospital.undoLastAttention();
                case 5 ->
                    Hospital.showHospitalized();
                case 6 -> {
                    System.out.print("Ingrese el ID del paciente: ");
                    String id = sc.nextLine();
                    Patient found = Hospital.bstSearch(id);
                    System.out.println(found == null ? "No encontrado" : found);
                }
                case 7 -> {
                    Hospital.advanceShift();
                    System.out.println("Turno actual: "
                            + (Hospital.currentShift != null ? Hospital.currentShift.shiftName : "ninguno"));
                }
                case 0 ->
                    running = false;
                default ->
                    System.out.println("Opcion invalida. Intente de nuevo.");
            }
        }

        System.out.println("Hasta luego.");
    }
}
