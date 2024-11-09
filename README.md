# SharkOs
<h3>The Shark Machine</h3>
<p>A simulated machine with 8 micro instructions. We'll call these SML (shark machine language)</p>
<p>When the simulation starts, the machine runs microinstructions stored in it's RAM using clock cycles of fetch, decode, and execute.</p>
<p>Note that PSIAR points to the next instruction in memory to be run. After each clock cycle, PSIAR increments</p>
<img src="https://github.com/user-attachments/assets/6ba8b53e-7ab8-46bf-bc0f-832640356347">

<h3>PCBs and Booting the OS</h3>
<p>Process states and info are saved in process control blocks (PCSs) to achieve multitasking in the CPU.</p>
<p>
  To boot the operating system, a "yield" instruction is stored in the 0th address of the machine's memory (more on this later). Then, for
  each SML program in storage, a PCB is created, placed in the program queue, and all its instructions are loaded into the machine's memory.
</p>
<p>In reality, the OS itself should also be loaded into memory here, but this level of accuracy is beyond the scope of this simulation.</p>
<img src="https://github.com/user-attachments/assets/9958ba52-b525-4ec4-8d62-b463c5175957">

<h3>Multitasking with a Round Robin Scheduler</h3>
<p>
  After all micro instructions are loaded into memory, the machine starts and the Round Robin Scheduler begins as a do-while loop. To 
  simulate different arrival times of processes, each process has a set arrival time that represents which clock cycle it will be ready on. 
  The scheduler first checks if any processes in the program queue are ready to enter the ready queue. 
</p>
  The first instruction that the machine will run is "yield", since we placed it in address 0. Yield returns 0, so the first status code for the scheduler to process is 0.
  At this state, if there is a program ready to run in the ready queue, its PCB is used to load its registers states to the machine and the JobClockTracker is reset to 0. The PSIAR field
  will point to the first instruction of the process in memory (if this is the first clock cycle where it's running) or to the next instruction to
  be run (if the process was previously running but was interrupted/yielded the CPU). Each instruction of this process will return a status code of 1
  except for when it ends.
<p>
<p>
  For each instruction that returns a 1, the job clock tracker is incremented, this keeps track of how many clock cycles the process has run for. When the
  job clock tracker is equal to the quantum, the scheduler calls an interrupt, which saves the state of the registers to the process' PCB and instructs the 
  machine to yield by pointing PSIAR to address 0. Since yield returns 0, the loop continues as previously stated. 
</p>
<p>
  Each SML program ends with an "end" instruction, which returns a status of -1. When this happens, the process is removed from the ready queue. If the ready queue is empty
  at this state or after a status code of 0, the OS will continue pointing PSIAR to address 0 to enter an idle loop until a process enters the ready queue. If the program queue 
  is empty after a status of 0 or -1, the OS powers off the machine and the simulation ends. 
</p>
  
<img src="https://github.com/user-attachments/assets/34a93c8f-f300-43d6-af33-911f1763e6b8">


