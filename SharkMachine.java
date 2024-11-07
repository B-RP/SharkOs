public class SharkMachine {
    int ACC, PSIAR, SAR, TMPR, CSIAR, MIR;
    int SDR;
    String[] IR;
    String[][] memory;
    int nextFree;
    public int clock;
    SharkMachine(){
        //registers
        ACC = 0; //accumulator - used for all arithmetic operations
        
        PSIAR = 0; //Primary Storage Instruction Address Register - points to the next instruction 
        
        SAR = 0; //Storage Address Register - holds address of data being written to or read from
        SDR = 0; //Storage Data Register - holds data being written or read from SAR
        
        TMPR = 0; //Temporary Register 
        CSIAR = 0; //Control Storage Instruction Address Register - points to location of next microinstruction
        
        IR = new String[2]; //Instruction Register - holds current instruction being executed
        MIR = 0; //Micro-instruction Register - holds currect micro instruction

        memory = new String[1024][2];
        nextFree = 0;

        clock = 0;
    }

    //memory operations
    public void read(){
        SDR = Integer.parseInt(memory[SAR][1]); 
    }

    public void write(){
        memory[SAR][1] = Integer.toString(SDR);
    }

    //CPU operations
    private void fetch(){
        SAR = PSIAR;
        IR = memory[(int) SAR];
        //System.out.println("Action[fetch]: " + IR[0] + ", " + IR[1]);
    }

    private int decode(){
        if(IR[1] != null){
            SDR = Integer.parseInt(IR[1]); //SDR stores second part of the instruction, the operand
        }
        
        //CSIAR stores the first part of the instruction in memory, the operator
        switch(IR[0]){
            case "ADD":
                CSIAR = 10;
                return 1;
            case "SUB":
                CSIAR = 20;
                return 1;
            case "LDA":
                CSIAR = 30;
                return 1;
            case "STR":
                CSIAR = 40;
                return 1;
            case "BRH":
                CSIAR = 50;
                return 1;
            case "CBR":
                CSIAR = 60;
                return 1;
            case "YLD":
                CSIAR = 70;
                return 1;
            case "END":
                CSIAR = 80;
                return 1;
            default:
                System.out.println("ERROR: Could not decode instruction: (" + IR[0] + ", " + IR[1] + ")");
                CSIAR = 80;
               return 0;
        }
    }

    private int execute(){

        switch((int)CSIAR){
            case 10:
                ADD();
                return 1;
            case 20:
                SUB();
                return 1;
            case 30:
                LDA();
                return 1;
            case 40:
                STR();
                return 1;
            case 50:
                BRH();
                return 1;
            case 60:
                CBR();
                return 1;
            case 70:
                YLD();
                return 0;
            case 80:
                END();
                return -1;
            default:
                System.out.println("Error: Could not execute instruction: " + CSIAR);
                END();
                return -1;
        }
    
    }

    public int performClockCycle(){
        fetch();
        decode();
        int status = execute();
        clock++;
        return status;
    }

    //ALU micro-operations
    public void ADD(){ //Add opcode 10
        TMPR = ACC;
        ACC = PSIAR + 1;
        PSIAR = ACC;
        ACC = (int) TMPR;
        TMPR = (int) SDR;
        SAR = (int) TMPR;
        read();
        TMPR = (int) SDR;
        ACC += TMPR;
        CSIAR = 0;

    }

    public void SUB(){ //subtract, Opcode 20
        TMPR = ACC;
        ACC = PSIAR + 1;
        PSIAR = ACC;
        ACC = TMPR;
        TMPR = (int) SDR;
        SAR = TMPR;
        read();
        TMPR = (int) SDR;
        ACC = ACC - TMPR;
        CSIAR = 0;
    }

    //CPU micro-operations
    public void LDA(){ //Load, Opcode 30
        ACC = PSIAR + 1;
        PSIAR = ACC;
        TMPR = (int) SDR;
        SAR = TMPR;
        read();
        ACC = (int) SDR;
        CSIAR = 0;
    }

    public void STR(){ //Store, opcode 40
        TMPR = ACC;
        ACC = PSIAR + 1;
        PSIAR = ACC;
        ACC = TMPR;
        TMPR = (int) SDR;
        SAR = TMPR;
        SDR = ACC;
        write();
        CSIAR = 0;
    }

    public void BRH(){ //Branch, opcode 50
        PSIAR = (int) SDR;
    }

    public void CBR(){ //conditional branch, opcode 60
        if(ACC == 0){
            PSIAR = (int) SDR;
            CSIAR = 0;
        }
        else{
            TMPR = ACC;
            ACC = PSIAR +1;
            PSIAR = ACC;
            ACC = TMPR;
            CSIAR = 0;
        }

    }

    //Additional Operations 

    public void YLD(){ //Yield, opcode 70 
        PSIAR = 0; //CPU sits idle until next job is loaded
    }
    public void END(){ //end, opcode 80
        System.out.println("End of Job at clock cycle: " + clock);
        System.out.println("ACC: " + ACC);
        System.out.println("PSIAR: " + PSIAR);
        System.out.println("SAR: " + SAR);
        System.out.println("SDR: " + SDR);
        System.out.println("TMPR: " + TMPR);
        System.out.println("CSIAR: " + CSIAR);
        System.out.println("IR: [" + IR[0]+ ", " + IR[1] + "]" );
        System.out.println("MIR: " + MIR);

        System.out.println("----Memory----");
        for(int i = 0; i < memory.length; i++){
            System.out.println(i + ": [" + memory[i][0] + ", " + memory[i][1] + "]\t");
        }
    }
}