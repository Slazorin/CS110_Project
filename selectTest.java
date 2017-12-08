package junit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class selectTest {

	@Test
	public void selectTest() throws IOException{
		ValueStore vs = new ValueStore("Data.values");
		BTree bt = new BTree("Data.bt",vs);
		
		String res = bt.select(9);
		assertEquals("9 ==> hello",res);
	}

}
