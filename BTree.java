import java.io.*;
import java.lang.*;
import java.util.*;

public class BTree{
	public static final long INIT_POS = 0;
	public static final long INIT_VAL = 0;
	public static final long DEF_VALUE = -1;
	public static final long ARR_LENGTH = 20;
	public static final int ORDER = 7;
	public static final long BYTES_PER_ENTRY = 8;
	public static final long BYTES_PER_NODE = ((3*ORDER)-1)*BYTES_PER_ENTRY;
	public static final int HEADER_BYTES = 16;
	public static final int LAST_OFFSET = 18;
	public static final int NUM_POINTERS = 3*ORDER-1;
	public static long btNumRec = 0;
	public static ValueStore vs;
	public static RandomAccessFile raf;

	//constructor
	public BTree(String strFile, ValueStore valueS) throws IOException{
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
		vs = valueS;
	}

	public void initRec() throws IOException {
		for(int i = 0; i < NUM_POINTERS; i++){
			raf.seek(BYTES_PER_ENTRY*i+HEADER_BYTES);
			raf.writeLong(DEF_VALUE);
		}
	}

	public String addValue(long key, long vNumRec) throws IOException{
		String verdict = "";
		//check if number of nodes is still 0
		//change number of nodes to 1 if yes (first value to be inserted into file)
		raf.seek(INIT_POS);
		if(raf.readLong()==0){
			raf.seek(INIT_POS);
			raf.writeLong(1);
		}


		if(isCopy(key) != DEF_VALUE){
			verdict = "ERROR: " + key + " already exists.";
			return verdict;
		}

	}

	public void shiftRecord(long copyFromIndex, long copyToIndex) throws IOException{
		//Reads each long data from current record and writes to the target one
		for( long i = 0; i < ARR_LENGTH*BYTES_PER_ENTRY; i += BYTES_PER_ENTRY ){
			raf.seek(copyFromIndex);
			long curr = raf.readLong();
			raf.seek(copyToIndex);
			raf.writeLong(curr);
		}
	}

	public void shiftValues(long correctInd) throws IOException{
		for(long ind = 3*btNumRec+2; ind > correctInd; ind-=3){
			//read value from previous position
			raf.seek(HEADER_BYTES+(ind-3)*BYTES_PER_ENTRY);
			long prevKey = raf.readLong();
			long prevVNumRec = raf.readLong();
			//put values into next position
			raf.seek(HEADER_BYTES+ind*BYTES_PER_ENTRY);
			raf.writeLong(prevKey);
			raf.writeLong(prevVNumRec);

		}
	}

	public long isCopy(long k) throws IOException{
		long isCopy = DEF_VALUE;
		for(long ind = 2; ind <= NUM_POINTERS-3; ind+=3){
			raf.seek(HEADER_BYTES+ind*BYTES_PER_ENTRY);
			long currKey = raf.readLong();
			if(currKey == k){
				isCopy = ind;
				break;
			}
		}
		return isCopy;
	}

	public boolean isFull(long node){
		raf.seek(node*BYTES_PER_ENTRY*ARR_LENGTH + BYTES_PER_ENTRY*LAST_OFFSET);
		long curr = raf.readLong();
		if( curr == DEF_VALUE ){
			return true;
		}
		return false;
	}

	public long getMedian(long[] keys, long i){
		Arrays.sort(keys);
		return keys[ORDER/2];
	}


	public String select(long k) throws IOException{
		String verdict = "";
		long index = isCopy(k);
		if(index == DEF_VALUE){
			verdict = "ERROR: " + k + " does not exist.";
		}else{
			raf.seek(HEADER_BYTES+(index+1)*BYTES_PER_ENTRY);
			long numOnVS = raf.readLong();
			String val = vs.getValue(numOnVS);
			verdict = k + " ==> " + val;
		}

		return verdict;

	}

	public String update(long k, String newVal) throws IOException{
		String verdict = "";
		long index = isCopy(k);
		if(index == DEF_VALUE){
			verdict = "ERROR: " + k + " does not exist.";
		}else{
			raf.seek(HEADER_BYTES+(index+1)*BYTES_PER_ENTRY);
			long numOnVS = raf.readLong();
			vs.updateVal(numOnVS,newVal);
			verdict = k + " updated" + ".";
		}
		return verdict;
	}
	
}