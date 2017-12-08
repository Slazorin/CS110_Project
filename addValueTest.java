package junit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class addValueTest {

	@Test
	public void addValueTest() throws IOException{
		ValueStore vs = new ValueStore("Data.values");
		BTree bt = new BTree("Data.bt",vs);
		
		String res = bt.addValue(9, 0, 0);
		assertEquals("9 inserted.",res);
		
	}

}
