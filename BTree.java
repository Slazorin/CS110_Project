import java.io.*;
import java.lang.*;

public class BTree{
	public static final long INIT_POS = 0;
	public static final long INIT_VAL = 0;
	public static final long DEF_VALUE = -1;
	public static final int ARR_LENGTH = 20;
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
		for(int i = 0; i < ARR_LENGTH; i++){
			raf.writeLong(DEF_VALUE);
		}
	}

	public void addValue(long key, long numRec) throws IOException{
		raf.seek(INIT_POS);

		if(raf.readLong()==0){
			raf.seek(INIT_POS);
			raf.writeLong(1);
		}
		raf.seek(32);
		raf.writeLong(key);
		raf.writeLong(numRec);







		// raf.seek(8 + numRec*256);
		// raf.writeShort(val.length());
		// raf.writeBytes(val);
		// raf.seek(INIT_RECORDS);
		// raf.writeLong(++numRec);
	}
	
}