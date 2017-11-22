import java.io.*;
import java.lang.*;

public class ValueStore{
	public static final int INIT_RECORDS = 0;
	public static final int INIT_POS = 0;
	public static long numRec;
	public static RandomAccessFile raf;

	//constructor
	public ValueStore(String strFile) throws IOException{
		File file = new File(strFile);
		if(!file.exists()){
			//make raf and file
			raf = new RandomAccessFile(strFile, "rwd");
			file.createNewFile();

			//write 0 as # of records
			raf.writeLong(INIT_RECORDS);
			numRec = INIT_RECORDS;
		}else{
			raf = new RandomAccessFile(file, "rwd");

			raf.seek(INIT_POS);
			//read first long, which is # of records
			numRec = raf.readLong();
		}
	}

	public void addValue(String val) throws IOException{
		raf.seek(8 + numRec*256);
		raf.writeShort(val.length());
		raf.writeBytes(val);
		raf.seek(INIT_RECORDS);
		raf.writeLong(++numRec);
	}

	public void test() throws IOException{
		raf.seek(1);
		raf.writeBytes("Matt");
	}
	
}