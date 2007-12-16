package org.csapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

/**
 * 
 * Class Description for SerializationTest
 */
public class SerializationTest extends TestCase {

    public void testEnumSerialization() {
        TpAddressPresentation orig = TpAddressPresentation.P_ADDRESS_PRESENTATION_ADDRESS_NOT_AVAILABLE;

        TpAddressPresentation serialized = (TpAddressPresentation) serialize(orig);

        assertNotNull(serialized);

        assertEquals(orig.value(), serialized.value());
    }

    public void testStructSerialization() {
        TpAddress orig = new TpAddress(
                TpAddressPlan.P_ADDRESS_PLAN_AESA,
                "AddrString",
                "Name",
                TpAddressPresentation.P_ADDRESS_PRESENTATION_ADDRESS_NOT_AVAILABLE,
                TpAddressScreening.P_ADDRESS_SCREENING_NETWORK, "SubStr");

        TpAddress serialized = (TpAddress) serialize(orig);

        assertNotNull(serialized);

        assertEquals(orig.AddrString, serialized.AddrString);

        assertEquals(orig.Name, serialized.Name);

        assertEquals(orig.SubAddressString, serialized.SubAddressString);

        assertEquals(orig.Screening.value(), serialized.Screening.value());

        assertEquals(orig.Presentation.value(), serialized.Presentation.value());

        assertEquals(orig.Plan.value(), serialized.Plan.value());
    }

    public void testExceptionSerialization() {
        P_INVALID_NETWORK_STATE orig = new P_INVALID_NETWORK_STATE("A", "B");

        P_INVALID_NETWORK_STATE serialized = (P_INVALID_NETWORK_STATE) serialize(orig);

        assertNotNull(serialized);

        assertEquals(orig.ExtraInformation, serialized.ExtraInformation);

        assertEquals(orig.getMessage(), serialized.getMessage());
    }

    public void testUnionSerialization() {
        
        TpAoCOrder orig = new TpAoCOrder();

        orig.ChargeAdviceInfo(new TpChargeAdviceInfo(new TpCAIElements(1, 2, 3,
                4, 5, 6, 7), new TpCAIElements(8, 9, 10, 11, 12, 13, 14)));

        TpAoCOrder serialized = (TpAoCOrder) serialize(orig);

        assertNotNull(serialized);

        assertEquals(
                orig.ChargeAdviceInfo().CurrentCAI.InitialSecsPerTimeInterval,
                serialized.ChargeAdviceInfo().CurrentCAI.InitialSecsPerTimeInterval);

        assertEquals(
                orig.ChargeAdviceInfo().NextCAI.InitialSecsPerTimeInterval,
                serialized.ChargeAdviceInfo().NextCAI.InitialSecsPerTimeInterval);
        
        orig = new TpAoCOrder();

        orig.ChargePerTime(new TpChargePerTime(1,2,3));

        serialized = (TpAoCOrder) serialize(orig);

        assertNotNull(serialized);

        assertEquals(
                orig.ChargePerTime().InitialCharge,
                serialized.ChargePerTime().InitialCharge);

        assertEquals(
                orig.ChargePerTime().NextChargePerMinute,
                serialized.ChargePerTime().NextChargePerMinute);

        assertEquals(
                orig.ChargePerTime().CurrentChargePerMinute,
                serialized.ChargePerTime().CurrentChargePerMinute);
    }

    private Object serialize(Object o) {
        Object result = null;

        try {
            FileOutputStream outputStream = new FileOutputStream("Test.bin");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    outputStream);

            objectOutputStream.writeObject(o);

            objectOutputStream.flush();
            
            objectOutputStream.close();

            FileInputStream fileInputStream = new FileInputStream("Test.bin");

            ObjectInputStream objectInputStream = new ObjectInputStream(
                    fileInputStream);

            result = objectInputStream.readObject();
            
            fileInputStream.close();
        }
        catch (IOException e) {
            fail();
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            fail();
            e.printStackTrace();
        } finally {

            File file = new File("Test.bin");

            file.delete();
        }

        return result;
    }
}