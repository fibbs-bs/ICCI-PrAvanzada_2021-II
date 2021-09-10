/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author jhona
 */
public class Ayudantia3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.println("¿En qué comuna se harán las gestiones? :");
        String city = scan.nextLine();
        String [] hospitalsNames = hospitalsReading(city);
        while (hospitalsNames[0].equals("empty")){
            System.out.println("Ingrese una comuna correcta, ¿En qué comuna se harán las gestiones?");
            city = scan.nextLine();
            hospitalsNames = hospitalsReading(city);
            
        }
        int [] cityVars = cityReading(city);
        int localInfections = cityVars[0];
        int localPopulation = cityVars[1];
        double infectionPercentage = (localInfections/(double)localPopulation)*100;
        System.out.println("El porcentaje de infectados en "+city+" es: "+Math.round(infectionPercentage*1000.0)/1000.0+"%");
        if (infectionPercentage >= 1){
            A_Type_Requirements(localInfections);
        }
        else{
            B_Type_Requirements(hospitalsNames);
        }
        
    }

    private static String[] hospitalsReading(String city) throws FileNotFoundException {
        Scanner scan = new Scanner(new File ("hospitales.txt"));
        while(scan.hasNext()){
            String line = scan.nextLine();
            String[] fragments = line.split(",");
            if (fragments[0].equals(city)){
                return fragments[1].split("-");
            }
        }
        return new String[]{"empty"};
    }

    private static int[] cityReading(String city) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("comunas.txt"));
        while(scan.hasNext()){
            String line = scan.nextLine();
            String[] cityInfo = line.split(",");
            if (cityInfo[0].equals(city)){
                int localPopulation = Integer.parseInt(cityInfo[1]);
                int localInfections = Integer.parseInt(cityInfo[2]);
                return new int[]{localPopulation,localInfections};
            }
        }
        return new int[]{0};
    }

    private static void A_Type_Requirements(int infections) {
        String [] names = new String[infections];
        int [] infectedCounts = new int[infections];
        System.out.println("Requerimientos tipo A – Trazabilidad de casos");
        System.out.println("Ingrese persona contagiada y contagios: ");
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        int spreaderCount = 0;
        while(!line.equals("-1")){
            String[] infectionInfo = line.split(",");
            String spreaderName = infectionInfo[0];
            int infectedCount = Integer.parseInt(infectionInfo[1]);
            spreaderCount = spreadersDataStoreMachine(names,spreaderName,infectedCounts,infectedCount,spreaderCount);
            System.out.println("Ingrese persona contagiada y contagios: ");
            line = scan.nextLine();
        }
        String bestSpreader = bestSpreader(names,infectedCounts,spreaderCount);
        System.out.println("La persona que mas ha contagiado es: "+bestSpreader);
    }

    private static int spreadersDataStoreMachine(String[] names,String spreaderName, int[] infectedCounts, int infectedCount, int spreaderCount) {
        boolean founded = false;
        for (int i = 0 ; i <spreaderCount ; i++ ){
            if (names[i].equals(spreaderName)){
                infectedCounts[i]+=infectedCount;
                founded = true;
                break;
            }
        }
        if (founded){
            return spreaderCount;
        }
        else{
            names[spreaderCount] = spreaderName;
            infectedCounts[spreaderCount] = infectedCount;
            int plus = spreaderCount+1;
            return (plus);
        }
            
    }

    private static String bestSpreader(String[] names, int[] infectedCounts, int count) {
        int max = -1;
        String name = "";
        for (int i = 0 ; i <count ; i++ ){
            if (infectedCounts[i] > max){
                max = infectedCounts[i];
                name = names[i];
            }
        }
        return name;
    }

    private static void B_Type_Requirements(String[] hospitalsNames) {
        int [] beds = new int[hospitalsNames.length];
        int [] docs = new int[hospitalsNames.length];
        String [] severity = new String[]{"grave","critico","riesgo vital"};
        double [] peopleOnThisSeverity = new double[3];
        Scanner scan = new Scanner(System.in);
        for (int i = 0 ; i < hospitalsNames.length ; i++){
            System.out.println("Ingrese cantidad de camas del hospital "+hospitalsNames[i]+": ");
            int bedsCount = Integer.parseInt(scan.nextLine());
            beds[i]=bedsCount;
            System.out.println("Ingrese cantidad de doctores del hospital "+hospitalsNames[i]+": ");
            int docsCount = Integer.parseInt(scan.nextLine());
            docs[i]=docsCount;
        }
        double add = 0;
        for (int j = 0; j < 3;j++){
            System.out.println("Ingrese porcentaje de personas en estado "+severity[j]+": ");
            double severityB = Double.parseDouble(scan.nextLine());
            add+=severityB;
            if (add >= 100){
                if (add > 100){
                    peopleOnThisSeverity[j] = severityB + (100-add);
                }
                else{
                    peopleOnThisSeverity[j] = severityB;
                }
                break;
            }
            else{
                peopleOnThisSeverity[j] = severityB;
            }
        }
        if (add<100){
            double excess = 100-add;
            peopleOnThisSeverity[2] += excess;
            System.out.println("Se agregó "+excess+"% a riesgo vital ya que no se habia cumplido el 100%");
        }
        hospitalsOrder(beds,hospitalsNames,hospitalsNames.length,docs);
        System.out.println("");
        deployHospitals(hospitalsNames,beds,docs,hospitalsNames.length);
        System.out.println("");
        int hospitalsCount = deleteHospitalAndDoctorTransfers(beds,hospitalsNames,docs);
        hospitalsOrder(beds,hospitalsNames,hospitalsCount,docs);
        System.out.println("");
        deploySeverity(severity,peopleOnThisSeverity);
        System.out.println("");
        deployHospitals(hospitalsNames,beds,docs,hospitalsCount);
        
    }
    private static void hospitalsOrder(int[] beds, String[]hospitalsNames, int count, int []docs){
        int aux,aux3;
        String aux2;
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (beds[j + 1] > beds[j]) {
                    aux = beds[j + 1];
                    beds[j + 1] = beds[j];
                    beds[j] = aux;
                    aux2 = hospitalsNames[j + 1];
                    hospitalsNames[j + 1] = hospitalsNames[j];
                    hospitalsNames[j] = aux2;
                    aux3 = docs[j + 1];
                    docs[j + 1] = docs[j];
                    docs[j] = aux3;
                }
            }
        }
    }
    private static void deploySeverity(String[] severity, double[] peopleOnThisSeverity) {
        System.out.println("Los porcentajes de tipos de pacientes en la comuna son:");
        for (int i  = 0 ; i<3 ; i++ ){
            System.out.println(severity[i]+": "+peopleOnThisSeverity[i]+"%");
        }
    }

    private static void deployHospitals(String[] hospitalsNames, int[] beds, int[] docs, int count) {
        System.out.println("Los hospitales en la comuna son:");
        for(int i = 0; i<count;i++){
            System.out.println(hospitalsNames[i]+": "+beds[i]+" camas y "+docs[i]+" doctores.");
        }
    }

    private static int deleteHospitalAndDoctorTransfers(int[] beds, String[] hospitalsNames, int[] docs) {
        if (hospitalsNames.length>1){
            int minDocAvailableHospitalIndex = minDocAvailableHospital(docs);
            String hospitalName = hospitalsNames[minDocAvailableHospitalIndex];
            int freeDoctors = docs[minDocAvailableHospitalIndex];
            int newHospitalCount = deleteProcess(beds, hospitalsNames, docs, minDocAvailableHospitalIndex);
            int maxBedsUsedHospitalIndex = maxBedsUsedHospital(beds,newHospitalCount);
            docs[maxBedsUsedHospitalIndex] += freeDoctors;
            System.out.println("El hospital eliminado fue "+hospitalName+", la cantidad de doctores desplazados fue "+freeDoctors+" y fueron transferidos a "+hospitalsNames[maxBedsUsedHospitalIndex]);
            return newHospitalCount;
        }
        else{
            System.out.println("No se pueden transferir doctores ya que sólo existe un hospital");
            return hospitalsNames.length;
        }
        
    }

    private static int minDocAvailableHospital(int[] docs) {
        int minus = 99999;
        int dex = -1;
        for (int i = 0;i<docs.length; i++){
            if (docs[i]<minus){
                minus=docs[i];
                dex = i;
            }
        }
        return dex;
    }

    private static int deleteProcess(int[] beds, String[] hospitalsNames, int[] docs, int pos) {
        for (int k = pos; k < hospitalsNames.length -1 ;k++){
            hospitalsNames[k]=hospitalsNames[k+1];
            beds[k]=beds[k+1];
            docs[k]=docs[k+1];
        }
        return (hospitalsNames.length-1);
    }

    private static int maxBedsUsedHospital(int[] beds, int cant) {
        int max = -1;
        int dex = -1;
        for (int i = 0;i<cant; i++){
            if (beds[i]>max){
                max=beds[i];
                dex = i;
            }
        }
        return dex;
    }
}