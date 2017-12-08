package junit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class updateTest {

	@Test
	public void updateTest() throws IOException{
		ValueStore vs = new ValueStore("Data.values");
		BTree bt = new BTree("Data.bt",vs);
		
		String res = bt.update(9,"hey");
		assertEquals("9 updated.",res);
	}

}
