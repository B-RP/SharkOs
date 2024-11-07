public class Process {

    int ID;
    int arrival;
    int startAddress;
    int endAddress;

    int ACC, PSIAR, SAR, TMPR, CSIAR, MIR;
    int SDR;
    String[] IR;


    Process(int pid, int arrivalTime, int start, int end){

        ID = pid;
        arrival = arrivalTime;
        startAddress = start;
        endAddress = end;

        //Register states
        ACC = 0;
        PSIAR = start;
        SAR = 0;
        SDR = 0;
        TMPR = 0;
        CSIAR = 0;
        IR = new String[2];
        MIR = 0;
    }
    
    public void saveState(int acc, int psiar, int sar, int tmpr, int csiar, int mir, String[] ir, int sdr){
        ACC = acc;
        PSIAR = psiar;
        SAR = sar;
        TMPR = tmpr;
        CSIAR = csiar;
        MIR = mir;
        IR = ir;
        SDR = sdr; 
    }
}
