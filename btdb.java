import java.util.*;
import java.io.*;

public class btdb {
	public static final int FIRST_ARG = 0;
	public static final int SECOND_ARG = 1;
	public static final int THIRD_ARG = 2;
	public static final int NUM_COMMANDS = 3;
	public static void main(String[] args)  throws IOException {
		Scanner in = new Scanner(System.in); //process cmds
		String btName = args[FIRST_ARG];
		String valuesName = args[SECOND_ARG];

		//checks if two file names have been typed
		if(checkArgs(args)){
			ValueStore vs = new ValueStore(valuesName);
			BTree bt = new BTree(btName);
			while(in.hasNext()){
				String[] cmd = new String[NUM_COMMANDS];
				cmd[FIRST_ARG] = in.next();
				cmd[SECOND_ARG] = Integer.toString(in.nextInt());
				cmd[THIRD_ARG] = in.nextLine().replaceFirst(" ","");
				if(cmd[FIRST_ARG].equals("insert")){
					long numRec = vs.addValue(cmd[THIRD_ARG]);
					bt.addValue(Long.parseLong(cmd[SECOND_ARG]), numRec);
				}
				
			}
		}
	}

	public static boolean checkArgs(String[] args){
		return (args.length == 2);
	}
}