import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;



public class SharkOS {

    static boolean power = true;
    static int numOfProcesses = 0;
    static int quantum = 4;
    static int jobClockTracker = 0;
    static SharkMachine machineInterface = new SharkMachine();
    static Queue<Process> programQueue = new LinkedList<>();
    static Queue<Process> readyQueue = new LinkedList<>();

    
    public static void writeToMemory(String fileName, int arrival) throws FileNotFoundException{

        File pg = new File(fileName);
        Scanner scanner = new Scanner(pg);

        int startAddress = machineInterface.nextFree;

        while(scanner.hasNextLine()){

            String line = scanner.nextLine();
            //index 0-3 are always operators
            machineInterface.memory[machineInterface.nextFree][0] = line.substring(0,3);
            //if there are more than 3 characters, an operand exists and can take up any number of chars on the line
            if(line.length()>3){
                machineInterface.memory[machineInterface.nextFree][1] = line.substring(4, line.length());
            }
            machineInterface.nextFree++;
        }
        int endAddress = machineInterface.nextFree - 1;

        Process p = new Process(numOfProcesses, arrival, startAddress, endAddress);

        numOfProcesses++;
        programQueue.add(p);
        scanner.close();
    }

    public static void loadToCPU(Process p){
        machineInterface.ACC = p.ACC;
        machineInterface.PSIAR = p.PSIAR;
        machineInterface.SAR = p.SAR;
        machineInterface.SDR = p.SDR;
        machineInterface.TMPR = p.TMPR;
        machineInterface.CSIAR = p.CSIAR;
        machineInterface.IR = p.IR;
        machineInterface.MIR = p.MIR;
    }

    public static void interrupt(){
        if(readyQueue.size() > 1){

            //removes the current program from ready queue, save state, and place to back of the queue
            Process currentProcess = readyQueue.poll();
            currentProcess.saveState(machineInterface.ACC, machineInterface.PSIAR, 
                machineInterface.SAR, machineInterface.TMPR, machineInterface.CSIAR, 
                machineInterface.MIR, machineInterface.IR, machineInterface.SDR);

            readyQueue.add(currentProcess);
            machineInterface.PSIAR = 0; //Point to yield instruction in memory
        }
        //if there are no other jobs in the queue
        else{
            jobClockTracker = 0; //counter is reset and current job maintains CPU
        }
    }

    public static void roundRobinScheduler(int clockCycle, int status){
        //Check to see if it is time for a job's arrival
        if(!programQueue.isEmpty()){
            while(!programQueue.isEmpty() && clockCycle == programQueue.peek().arrival){
                readyQueue.add(programQueue.poll());
            }
        }

        //Check the ending status of the last instruction ran. 
        if(status == -1){ //last instruction ran failed or ended the job, job is removed from ready queue
            readyQueue.remove();

            if(readyQueue.isEmpty()){

                machineInterface.PSIAR = 0; //point to yield instruction to enter idle until next job
                if(programQueue.isEmpty()){
                    //No more programs to run
                    power = false;
                }
            }
            else{
                Process currentProcess = readyQueue.peek();
                loadToCPU(currentProcess);
            }
        }
        
        else if(status == 0){//Yield instruction ran or system just booted
            if(!readyQueue.isEmpty()){
                //reset counter
                jobClockTracker = 0;

                //load the next job's state
                Process currentJob = readyQueue.peek();
                loadToCPU(currentJob);
            }
            
        }
        
        else{ //regular instruction ran, the job does not end or yield CPU. 
            jobClockTracker += status; 

            if(jobClockTracker == quantum){
                //System.out.println("Quantum reached: interrupting");
                interrupt();
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException{

        PrintStream o = new PrintStream(new File("SystemLog.txt"));
        System.setOut(o);

        //Write yield instruction to memory so we can run it when needed
        machineInterface.memory[machineInterface.nextFree][0] = "YLD";
        machineInterface.nextFree++;

        //write Shark Machine Language programs to memory
        writeToMemory("CalcTotal.txt", 0);
        writeToMemory("SubToZero.txt", 5);
        writeToMemory("AddTo30.txt", 7);
        writeToMemory("Multiplier.txt", 10);
        writeToMemory("Doubler.txt", 12);
        writeToMemory("IsEven.txt", 12);

        int status = 0;

        do {
            roundRobinScheduler(machineInterface.clock, status);
            status = machineInterface.performClockCycle();
        
        } while(power);
    }
}