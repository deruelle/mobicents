package org.mobicents.slee.sipevent.server.subscription.eventlist;

import static org.junit.Assert.*;
import org.junit.Test;

public class MultiPartTest {

	
	/**
	 * Tests if the string resulting from building an {@link MultiPart} has the expected value.
	 */
	@Test
	public void test() {
		
		String s = 
			"--50UBfW7LSCVLtggUPe5z"
			+ "\n" + "Content-Transfer-Encoding: binary"
			+ "\n" + "Content-ID: <nXYxAE@pres.vancouver.example.com>"
			+ "\n" + "Content-Type: application/rlmi+xml;charset=\"UTF-8\""
			+ "\n" 
			+ "\n" + "body1"
			+ "\n" 
			+ "\n" + "--50UBfW7LSCVLtggUPe5z"
			+ "\n" + "Content-Transfer-Encoding: binary"
			+ "\n" + "Content-ID: <bUZBsM@pres.vancouver.example.com>"
			+ "\n" + "Content-Type: application/pidf+xml;charset=\"UTF-8\""
			+ "\n" 
			+ "\n" + "body2"
			+ "\n" 
			+ "\n" + "--50UBfW7LSCVLtggUPe5z"
			+ "\n" + "Content-Transfer-Encoding: binary"
			+ "\n" + "Content-ID: <ZvSvkz@pres.vancouver.example.com>"
			+ "\n" + "Content-Type: application/pidf+xml;charset=\"UTF-8\""
			+ "\n"
			+ "\n" + "body3"
			+ "\n"
			+ "\n" + "--50UBfW7LSCVLtggUPe5z--";
			
			MultiPart multiPart = new MultiPart("50UBfW7LSCVLtggUPe5z","type");
			BodyPart bodyPart1 = new BodyPart("binary","nXYxAE@pres.vancouver.example.com","application","rlmi+xml","UTF-8","body1");
			BodyPart bodyPart2 = new BodyPart("binary","bUZBsM@pres.vancouver.example.com","application","pidf+xml","UTF-8","body2");
			BodyPart bodyPart3 = new BodyPart("binary","ZvSvkz@pres.vancouver.example.com","application","pidf+xml","UTF-8","body3");
			multiPart.getBodyParts().add(bodyPart1);
			multiPart.getBodyParts().add(bodyPart2);
			multiPart.getBodyParts().add(bodyPart3);
			System.out.println("### Multipart object:\n"+multiPart);
			System.out.println("### Multipart string:\n"+s);		
			assertEquals("multiPart.toString() is not the expected!", s, multiPart.toString());
	}
}
