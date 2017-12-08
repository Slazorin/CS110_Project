package junit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class addValueVSTest {

	@Test
	public void addValueVSTest() throws IOException{
		ValueStore vs = new ValueStore("Data.values");
		
		long res = vs.addValue("hello");
		assertEquals(2,res);
		
	}

}
