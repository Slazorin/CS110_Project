import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

public class BTreeTest {

	@Test
	public void addValueTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		String verdict = bt.addValue(9, 0, 0);
		assertEquals("9 inserted.", verdict);
		
	}
	
	
	@Test
	public void findCorrectNodeTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		long correctNode = bt.findCorrectNode(0);
		assertEquals(0, correctNode);
		
	}
	
	public void findKeyTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		long numRec = vs.addValue("hello");
		bt.addValue(10, numRec, -1);
		long record = bt.findKey(10);
		assertEquals(0, record);
		
	}
	
	@Test
	public void hasChildTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		boolean hc = bt.hasChild(0);
		assertEquals(false, hc);
		
	}
	
	@Test
	public void isAChildTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		boolean isC = bt.isAChild(2,0);
		assertEquals(false, isC);
		
	}
	
	@Test
	public void isFullTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		boolean isF = bt.isFull(0);
		assertEquals(false, isF);
		
	}
	
	@Test
	public void getParentTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		long par = bt.getParent(0);
		assertEquals(-1, par);
		
	}
	
	@Test
	public void getChildIndexTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		long childI = bt.getChildIndex(1, 0);
		assertEquals(-1, childI);
		
	}
	
	@Test
	public void simpleInsertTest() throws IOException {
		ValueStore vs = new ValueStore("1.values");
		BTree bt = new BTree("1.bt", vs);
		long numRec = vs.addValue("hey");
		long initCorrRec = bt.findCorrectNode(11);
		String verdict = bt.simpleInsert(11, numRec, initCorrRec);
		assertEquals("11 inserted.", verdict);
		
	}
	
	
	
	

}
