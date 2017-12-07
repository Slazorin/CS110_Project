import java.io.*;
import java.lang.*;

public class ValueStore{
	public static final int INIT_RECORDS = 0;
	public static final int INIT_POS = 0;
	public static final long BYTES_PER_ENTRY = 8;
	public static final long BYTES_PER_STRING = 258;
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

	public long addValue(String val) throws IOException{
		raf.seek(BYTES_PER_ENTRY + numRec*BYTES_PER_STRING);
		byte[] byteArray = val.getBytes("UTF8");
		raf.writeShort(val.length());
		raf.write(byteArray);
		raf.seek(INIT_RECORDS);
		raf.writeLong(++numRec);

		return (numRec-1);
	}
	
	//fix format
	public String getValue(long index) throws IOException{
		raf.seek(BYTES_PER_ENTRY+index*BYTES_PER_STRING);
		return raf.readUTF();
	}

	public void updateVal(long index, String newVal) throws IOException{
		raf.seek(BYTES_PER_ENTRY+index*BYTES_PER_STRING);
		raf.writeUTF(newVal);
	}
}