/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package milestone2;
import java.util.Scanner;
import java.io.*;
/**
 *
 * @author Obciana
 */
public class MileStone2 {
    static String employeeFile = "Copy of MotorPH_Employee Data.csv";
    static String attendanceFile = "Attendance Record.csv";
    static String sssFile = "SSS.txt";
    static String pagibigFile = "Pag-Ibig.txt";
    static String taxFile = "TAX.txt";
    static String PhilFile = "Philhealth.txt";

    
//    This method are part of loggin 
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Loggin start
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (!(username.equalsIgnoreCase("employee") || username.equalsIgnoreCase("payroll_staff"))
                || !password.equals("12345")) {

            System.out.println("Incorrect username and/or password.");
            return;
        }

        // If the loggin is employee
        if (username.equals("employee")) {

            System.out.println("1. Enter Employee Number");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {

                System.out.print("Enter Employee Number: ");
                String empNum = scanner.nextLine();
                processpayroll(empNum);
            }
        }

        // if loggin Payroll Staff
        else {

            System.out.println("1. Process Payroll");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();

            if (choice == 1) {

                System.out.println("1. One Employee");
                System.out.println("2. All Employees");

                int sub = scanner.nextInt();
                scanner.nextLine();

                if (sub == 1) {
                    System.out.print("Enter Employee Number: ");
                    String empNum = scanner.nextLine();
                    processpayroll(empNum);
                }

                else if (sub == 2) {
                    allemployee();
                }
            }
        }
        scanner.close();
    }

// This Method is process of payroll and the filereader of the data of logging in user like payroll staff and employee
    public static void processpayroll(String employeenum) {

        try (BufferedReader employeefilereader = new BufferedReader(new FileReader(employeeFile))) {

            String employeefile;
            employeefilereader.readLine();

            boolean employeefound = false;
            
            while ((employeefile = employeefilereader.readLine()) != null) {

                String[] employeedatadevider = employeefile.split(",");

                if (employeedatadevider[0].equals(employeenum)) {

                    employeefound = true;

                    String name = employeedatadevider[2] + " " + employeedatadevider[1];
                    String birthday = employeedatadevider[3];
                    double hourlyRate = Double.parseDouble(employeedatadevider[employeedatadevider.length - 1]);

                    double firstCutHours = 0;
                    double secondCutHours = 0;

                    BufferedReader attendance = new BufferedReader(new FileReader(attendanceFile));
                    
                    String attendancefile;
                    attendance.readLine();

                    while ((attendancefile = attendance.readLine()) != null) {

                        String[] attendancedata = attendancefile.split(",");

                        if (attendancedata[0].equals(employeenum)) {

                              String[] monthanddate = attendancedata[3].split("/");

                              int month = Integer.parseInt(monthanddate[0]);
                              int day   = Integer.parseInt(monthanddate[1]);

                              // filter of june only
                              if (month != 6) continue;
                            double hours = computeHours(attendancedata[4], attendancedata[5]);

                            if (day <= 15) {
                                firstCutHours += hours;
                            } else {
                                secondCutHours += hours;
                            }
                        }
                    }

                    // First cutoff
                    double firstGross = firstCutHours * hourlyRate;
                    
                    System.out.println("");
                    System.out.println("Employee Number: " + employeenum);
                    System.out.println("Employee Name: " + name);
                    System.out.println("Birthday: " + birthday);
                    
                     System.out.println("");
                    System.out.println("Cutoff: June 1 - June 15");
                    System.out.println("Total Hours Worked: " + firstCutHours);
                    System.out.println("Gross Salary: " + firstGross);
                    System.out.println("Net Salary: " + firstGross);

                    // Second cutoff
                    
                    double secondGross = secondCutHours * hourlyRate;
                    double totalGross = firstGross + secondGross;  
                    double sss = SSScomputarion(totalGross);
                    double philhealth = computephilhealth(totalGross);
                    double pagibig = computepagibig(totalGross);
                    double after = totalGross - sss - philhealth - pagibig;
                    double tax = computeTax(after);
                    double totalDed = sss + philhealth + pagibig + tax;
                    double net = totalGross - totalDed;
                    
                    System.out.println("");
                    System.out.println("Cutoff: June 16 - June 30");
                    System.out.println("Total Hours Worked: " + secondCutHours);
                    System.out.println("Gross Salary: " + secondGross);
                    System.out.println("SSS: " + sss);
                    System.out.println("PhilHealth: " + philhealth);
                    System.out.println("Pag-IBIG: " + pagibig);
                    System.out.println("Tax: " + tax);
                    System.out.println("Total Deductions: " + totalDed);
                    System.out.println("Net Salary: " + net);

                    break;
                }
            }
             // if employee doest exist 
            if (!employeefound) {
                System.out.println("Employee number does not exist.");
            }

        } catch (Exception error) {
            System.out.println("Error: " + error.getMessage());
    
        } 
    }
    

    // if payroll staff choose all employee this method will be show
    public static void allemployee() {

        try (BufferedReader employeereader = new BufferedReader(new FileReader(employeeFile))) {

            String employeerecord;
            employeereader.readLine();

            while ((employeerecord = employeereader.readLine()) != null) {

                String[] computationEmp = employeerecord.split(",");
                processpayroll(computationEmp[0]);
            }

        } catch (Exception error) {
            System.out.println("Error.");
        }
    }

    // This method are hours computation
    public static double computeHours(String login, String logout) {

        String[] in = login.split(":");
        String[] out = logout.split(":");

        int Inminutes = Integer.parseInt(in[0]) * 60 + Integer.parseInt(in[1]);
        int Outminutes = Integer.parseInt(out[0]) * 60 + Integer.parseInt(out[1]);

        int workstart = 8 * 60;
        int workend = 17 * 60;

        if (Inminutes < workstart) Inminutes = workstart;
        if (Outminutes > workend) Outminutes = workend;

        double workhours = (Outminutes - Inminutes) / 60.0;

        if (workhours < 0) return 0;

        return workhours;
    }

//   This method are filehandler used to calculate of deduction of SSS
    public static double SSScomputarion(double salary) {
    try (BufferedReader SSSfilereader = new BufferedReader(new FileReader(sssFile))) {

        String recordofsss;
        SSSfilereader.readLine(); // skip header

        while ((recordofsss = SSSfilereader.readLine()) != null) {
//   gamit pag debug         System.out.println("DEBUG LINE: " + line); //add kolang
            String[] sssdata = recordofsss.split(",");

            double minumsalary = Double.parseDouble(sssdata[0]);
            double maximumsalary = Double.parseDouble(sssdata[1]);
            double sssdeduction = Double.parseDouble(sssdata[2]);

            if (salary >= minumsalary && salary <= maximumsalary) {
//  gamit pang debug              System.out.println("MATCHED SSS: " + contribution);// add kolang kasama ng pang debug
                return sssdeduction;
            }
        }

    } catch (Exception error) {
        System.out.println("SSS Error: " + error.getMessage());
    }

    return 0;
} 

    //   This method are filehandler used to calculate of deduction of philhealth
      public static double computephilhealth(double salary) {
      try (BufferedReader philhealthfilereader = new BufferedReader(new FileReader(PhilFile))) {

        String philhealthrecord;
        philhealthfilereader.readLine(); // skip header

        while ((philhealthrecord = philhealthfilereader.readLine()) != null) {

            String[] philhealthdata = philhealthrecord.split(",");

            double minimunsalary = Double.parseDouble(philhealthdata[0]);
            double maximunsalary = Double.parseDouble(philhealthdata[1]);
            double percentage = Double.parseDouble(philhealthdata[2]);
            double minimumpercentage = Double.parseDouble(philhealthdata[3]);
            double maximumpercentage = Double.parseDouble(philhealthdata[4]);

            if (salary >= minimunsalary && salary <= maximunsalary) {

                double premium = salary * percentage;

                if (premium < minimumpercentage) premium = minimumpercentage;
                if (premium > maximumpercentage) premium = maximumpercentage;

                return premium / 2; // employee share
            }
        }

    } catch (Exception error) {
        System.out.println("PhilHealth Error: " + error.getMessage());
    }

    return 0;
}
// This method are filehandler used to calculate of deduction of pag-ibig
  public static double computepagibig(double salary) {
  try (BufferedReader pagibigfilereader = new BufferedReader(new FileReader(pagibigFile))) {

        String recordpagibig;
        pagibigfilereader.readLine(); // skip header

        while ((recordpagibig = pagibigfilereader.readLine()) != null) {
            String[] pagibigdata = recordpagibig.split(",");

            double minimumsalary = Double.parseDouble(pagibigdata[0]);
            double maximumsalary = Double.parseDouble(pagibigdata[1]);
            double percentage = Double.parseDouble(pagibigdata[2]);
            double maxpercentage = Double.parseDouble(pagibigdata[3]);

            if (salary >= minimumsalary && salary <= maximumsalary) {
                double computededuction = salary * percentage;

                if (computededuction > maxpercentage) {
                    computededuction = maxpercentage;
                }

                return computededuction;
            }
        }

    } catch (Exception error) {
        System.out.println("Pag-IBIG Error: " + error.getMessage());
    }

    return 0;
}
 
// This method are filehandler used to calculate of deduction of tax
  public static double computeTax(double salary) {

    try (BufferedReader taxreader = new BufferedReader(new FileReader(taxFile))) {

        String taxrecord;
        taxreader.readLine(); // skip header

        while ((taxrecord = taxreader.readLine()) != null) {

            String[] taxdata = taxrecord.split(",");

            double minimumsalary = Double.parseDouble(taxdata[0]);
            double maximumsalary = Double.parseDouble(taxdata[1]);
            double baseTax = Double.parseDouble(taxdata[2]);
            double maximumrate = Double.parseDouble(taxdata[3]);

            if (salary >= minimumsalary && salary <= maximumsalary) {

                return baseTax + (salary - minimumsalary) * maximumrate;
            }
        }

    } catch (Exception error) {
        System.out.println("Tax Error: " + error.getMessage());
    }

    return 0;
}
  
}
