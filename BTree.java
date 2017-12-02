import java.io.*;
import java.lang.*;
import java.util.*;

public class BTree{
	public static long btNumRec = 0;
	public static final long INIT_POS = 0;
	public static final long INIT_VAL = 0;
	public static final long DEF_VALUE = -1;
	public static final long REC_LENGTH = 20;
	public static final long IND_FIRST_CHILD = 1;
	public static final int ORDER = 7;
	public static final long BYTES_PER_ENTRY = 8;
	public static final long NOT_PARENT = -1;
	public static final long BYTES_PER_NODE = ((3*ORDER)-1)*BYTES_PER_ENTRY;
	public static final int HEADER_BYTES = 16;
	public static final int LAST_OFFSET = 18;
	public static final int NUM_POINTERS = 3*ORDER-1;
	public static final long MAX_KEYS = ORDER - 1;
	public static final long END_OF_FILE = HEADER_BYTES + BYTES_PER_NODE*(btNumRec+1); 
	public static ValueStore vs;
	public static currVsNum cvs;
	public static ArrayList<ArrayList<Long>> pairMap;
	public static ArrayList<Long> nodesMap;
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
			initRec(INIT_POS);
		}else{
			raf = new RandomAccessFile(file, "rwd");
		}
		vs = valueS;
	}

	public void initRec(long index) throws IOException {
		//Fills up a node with -1s
		raf.seek(index);
		for(int i = 0; i < NUM_POINTERS; i++){
			raf.seek(BYTES_PER_ENTRY*i+HEADER_BYTES);
			raf.writeLong(DEF_VALUE);
		}
	}

	public String addValue(long key, long vNumRec) throws IOException{
		String verdict = "";
		currVsNum = vNumRec; //vsNumRec of key to be inserted
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

		//if key is valid find its correct place
		//find place on bottom most-level (default place to insert without splitting)
		long initCorrRec = 0;
		boolean indFound = false;
		long availInd = MAX_KEYS; //3*btNumRec+2; //first available index
		long corrInd = availInd;// first correct is last of node

		//finds correct node/bt record number
		raf.seek(HEADER_BYTES+(initCorrRec*BYTES_PER_ENTRY*REC_LENGTH)+BYTES_PER_ENTRY);
		long child1ID = raf.readLong();
		while(initCorrRec <= btNumRec || child1ID == NOT_PARENT ){
			//finds correct index
			for(long ind = 2; ind < availInd*3; ind+=3){
				raf.seek(HEADER_BYTES+(initCorrRec*BYTES_PER_ENTRY*REC_LENGTH)+ind*BYTES_PER_ENTRY);
				long currKey = raf.readLong();
				if(currKey == DEF_VALUE || currKey > key){
					corrInd = ind;
					indFound = true;
					System.out.println(corrInd + " " + currKey); //tester
					break;

				}
			}
			if(indFound){
				break;
			}
			System.out.println("HI");//tester
			initCorrRec++;
			if(initCorrRec > btNumRec){
				break;
			}
			System.out.println("FAIL");//tester
			raf.seek(HEADER_BYTES+(initCorrRec*BYTES_PER_ENTRY*REC_LENGTH)+IND_FIRST_CHILD);
			child1ID = raf.readLong();

		}

		if(!isFull(initCorrRec)){
			//btNumRec++;
			return simpleInsert(key, vNumRec, initCorrRec, corrInd);
		}else{
			//insert all keys in corrNode into key array
			promMap = new HashMap<HashMap<Long,Long>,Long>();
			getPromoteList(key,initCorrRec);
			verdict = key + " inserted.";
		}

		return verdict;

	}

	public void getPromoteList(long key, long node){
		ArrayList<Long> keyVSMap = new ArrayList<Long>();
		keyVSMap.add(key);
		keyVSMap.add(cvs);
		pairMap.add(keyVSMap);
		nodesMap.add(node);

		long toInsert = key;
		boolean freeFnd = false;

		while(!freeFnd){
			ArrayList<Long> pMap = new ArrayList<Long>();
			if(isFull(node)){
				toInsert = getMedian(toInsert,node);
				long parent = getParent(node);
				if(toInsert == key){
					nodesMap.set(0,parent);
				}else{
					long vsNumRec = findVSRec(toInsert);
					pMap.add(toInsert);
					pMap.add(vsNumRec);
					pairMap.add(pMap);
					nodesMap.add(parent);
				}
				node = parent;
			}else{
				freeFnd = true;
			}

		}

	}

	public String simpleInsert(long key, long vNumRec, long initCorrRec, long corrInd) throws IOException{
		String verdict = "";
		shiftValues(initCorrRec, corrInd);
		raf.seek(HEADER_BYTES+(initCorrRec*BYTES_PER_ENTRY*REC_LENGTH)+corrInd*BYTES_PER_ENTRY);
		raf.writeLong(key);
		raf.writeLong(vNumRec);
		verdict = key + " inserted.";
		return verdict;

	}



	public long childCount(long node) throws IOException{
		long count = 0;
		long ind = HEADER_BYTES + node*BYTES_PER_NODE + BYTES_PER_ENTRY;
		boolean isChild = true;
		while(isChild){
			long curr = raf.readLong();
			if( curr != DEF_VALUE ) {
				count++;
			}else {
				isChild = false;
			}
			ind += BYTES_PER_ENTRY*2;
		}
		return count;
	}

	public long keyCount(long node) throws IOException{
		long count = 0;
		long ind = HEADER_BYTES + node*BYTES_PER_NODE + BYTES_PER_ENTRY*2;
		boolean isKey = true;
		while(isKey){
			raf.seek(ind);
			long curr = raf.readLong();
			if( curr != DEF_VALUE ) {
				count++;
			}else {
				isKey = false;
			}
			ind += BYTES_PER_ENTRY*3;
		}
		return count;
	}
	public void makeParentNode(long key, long vNumRec) throws IOException{
		//Goes to EOF and creates node full of -1s
		initRec(END_OF_FILE);
		raf.seek(HEADER_BYTES + btNumRec*BYTES_PER_NODE + BYTES_PER_ENTRY);
		raf.writeLong(key);
		raf.writeLong(vNumRec);
	}

	public void split(long promotedKey, long node) throws IOException{
		for(promMap.Entry<HashMap<Long,Long>, Long> entry : promMap.entrySet()) {
		    HashMap<Long,Long> pair = entry.getKey();
		    long node = entry.getValue();
		    for (Long num : promMap.keySet()) {
			    simpleInsert(num,pair.get(num))
			}
		    
		}
	}

	public long getParent(long node) throws IOException{
		//Grabs the parent node record num
		raf.seek(HEADER_BYTES + node*BYTES_PER_NODE);
		long parentID = raf.readLong();
		return parentID;
	}

	public void setParent(long node, long newParent) throws IOException {
		//Sets the parent of a node
		raf.seek(HEADER_BYTES + node*REC_LENGTH*BYTES_PER_ENTRY);
		raf.writeLong(newParent);
	}

	public void shiftRecord(long copyFromIndex, long copyToIndex) throws IOException{
		//Reads each long data from current record and writes to the target one
		for( long i = 0; i < BYTES_PER_NODE; i += BYTES_PER_ENTRY ){
			raf.seek(copyFromIndex);
			long curr = raf.readLong();
			raf.seek(copyToIndex);
			raf.writeLong(curr);
		}
	}

	public void shiftValues(long node, long correctInd) throws IOException{
		long keyCount = keyCount(node);
		System.out.println(keyCount);
		for(long ind = 3*keyCount+2; ind > correctInd; ind-=3){
			//read value from previous position
			raf.seek(HEADER_BYTES + (node*BYTES_PER_NODE) + (ind-3)*BYTES_PER_ENTRY);
			long prevKey = raf.readLong();
			long prevVNumRec = raf.readLong();
			//put values into next position
			raf.seek(HEADER_BYTES+ (node*BYTES_PER_NODE) + ind*BYTES_PER_ENTRY);
			raf.writeLong(prevKey);
			raf.writeLong(prevVNumRec);

		}
	}

	public long isCopy(long k, long node) throws IOException{
		long isCopy = DEF_VALUE;
		for(long ind = 2; ind <= NUM_POINTERS-3; ind+=3){
			raf.seek(HEADER_BYTES+ node*BYTES_PER_NODE + ind*BYTES_PER_ENTRY);
			long currKey = raf.readLong();
			if(currKey == k){
				isCopy = ind;
				break;
			}
		}
		return isCopy;
	}

	public boolean isFull(long node) throws IOException{
		raf.seek(node*BYTES_PER_NODE + HEADER_BYTES + LAST_OFFSET*BYTES_PER_ENTRY);
		long curr = raf.readLong();
		if( curr != DEF_VALUE ){
			return true;
		}
		return false;
	}

	public long getMedian(long node, long key){
		long[] keys = new long[ORDER];
		int arrInd = 0;
		for(int ind = 2; ind < REC_LENGTH; ind+=3){
			raf.seek(HEADER_BYTES+(node*BYTES_PER_NODE)+ind*BYTES_PER_ENTRY);
			keys[arrInd++] = raf.readLong(); 

		}
		keys[arrInd] = key;
		Arrays.sort(keys);
		return keys[ORDER/2];
	}

	public long findVSRec(long key) throws IOException{
		for(long ind = 2; ind < END_OF_FILE; ind += 3){
			raf.seek(HEADER_BYTES+ node*BYTES_PER_NODE + ind*BYTES_PER_ENTRY);
			long currKey = raf.readLong();
			if(currKey == k){
				return raf.readLong();
			}
		}
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