import java.util.*;
import java.io.*;

public class btdb {
	public static final int FIRST_ARG = 0;
	public static final int SECOND_ARG = 1;
	public static final int THIRD_ARG = 2;
	public static void main(String[] args)  throws IOException {
		Scanner in = new Scanner(System.in); //process cmds
		String btName = args[FIRST_ARG];
		String valuesName = args[SECOND_ARG];

		//checks if two file names have been typed
		if(checkArgs(args)){
			ValueStore vs = new ValueStore(valuesName);
			while(in.hasNext()){
				String[] cmd = in.nextLine().split(" ");
				if(cmd[FIRST_ARG].equals("insert")){

				}
				vs.addValue(cmd[THIRD_ARG]);
			}
		}
	}

	public static boolean checkArgs(String[] args){
		return (args.length == 2);
	}
}