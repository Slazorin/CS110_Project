import java.util.*;
import java.io.*;

public class btdb {
	public static final int FIRST_ARG = 0;
	public static final int SECOND_ARG = 1;
	public static final int THIRD_ARG = 2;
	public static final int NUM_COMMANDS = 3;
	public static final long DEF_VALUE = -1;
	public static void main(String[] args)  throws IOException {
		Scanner in = new Scanner(System.in); //process cmds
		String btName = args[FIRST_ARG];
		String valuesName = args[SECOND_ARG];

		//checks if two file names have been typed
		if(checkArgs(args)){
			ValueStore vs = new ValueStore(valuesName);
			BTree bt = new BTree(btName, vs);
			while(in.hasNext()){
				String[] cmd = new String[NUM_COMMANDS];
				cmd[FIRST_ARG] = in.next();
				if(cmd[FIRST_ARG].equals("exit")){
					bt.printTree();
					bt.raf.close();
					vs.raf.close();
					System.exit(0);
				}
				cmd[SECOND_ARG] = Integer.toString(in.nextInt());
				cmd[THIRD_ARG] = in.nextLine().replaceFirst(" ","");
				if(cmd[FIRST_ARG].equals("insert")){
					long numRec = vs.addValue(cmd[THIRD_ARG]);
					String verdict = bt.addValue(Long.parseLong(cmd[SECOND_ARG]), numRec, DEF_VALUE);
					System.out.println(verdict);
				}else if(cmd[FIRST_ARG].equals("select")){
					String verdict = bt.select(Long.parseLong(cmd[SECOND_ARG]));
					System.out.println(verdict);
				}else if(cmd[FIRST_ARG].equals("update")){
					String verdict = bt.update(Long.parseLong(cmd[SECOND_ARG]),cmd[THIRD_ARG]);
					System.out.println(verdict);
				}else{
					System.out.println("ERROR: invalid command.");
				}
			}
		}
	}

	public static boolean checkArgs(String[] args){
		return (args.length == 2);
	}
}