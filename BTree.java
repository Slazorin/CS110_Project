import java.io.*;
import java.lang.*;

public class BTree{
	public static final long INIT_POS = 0;
	public static final long INIT_VAL = 0;
	public static final int DEF_VALUE = -1;
	public static final int ARR_LENGTH = 20;
	public static final int ORDER = 7;
	public static final int BYTES_PER_ENTRY = 8;
	public static final int BYTES_PER_NODE = ((3*ORDER)-1)*BYTES_PER_ENTRY;
	public static final int HEADER_BYTES = 16;
	public static long btNumRec = 0;
	public static RandomAccessFile raf;

	//constructor
	public BTree(String strFile) throws IOException{
		File file = new File(strFile);
		if(!file.exists()){
			//make raf and file
			raf = new RandomAccessFile(strFile, "rwd");
			file.createNewFile();


			//when creating, putting first value as well
			raf.writeLong(INIT_VAL); //number of records
			raf.writeLong(INIT_VAL); //position of root node
			initRec();
		}else{
			raf = new RandomAccessFile(file, "rwd");
		}
	}

	public void initRec() throws IOException {
		for(int i = 0; i < BYTES_PER_NODE; i++){
			raf.writeByte(DEF_VALUE);
		}
	}

	public void addValue(long key, long vNumRec) throws IOException{
		//check if number of nodes is still 0
		//change number of nodes to 1 if yes (first value to be inserted into file)
		raf.seek(INIT_POS);
		if(raf.readLong()==0){
			raf.seek(INIT_POS);
			raf.writeLong(1);
		}

		//find correct index for the new value
		long correctInd = btNumRec; //default place to put new value is at the end of the list
		for(long ind = 0; ind < btNumRec; ind++){
			raf.seek(HEADER_BYTES+ind*BYTES_PER_NODE);
			long currKey = raf.readLong();
			if(currKey == DEF_VALUE || currKey > key){
				correctInd = ind;
				break;
			}
		}
		raf.seek(HEADER_BYTES+correctInd*BYTES_PER_NODE);
		raf.writeLong(key);
		raf.writeLong(vNumRec);
		btNumRec++;

	}
	
}