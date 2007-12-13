package org.mobicents.sleetests.resource.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.slee.ActivityContextInterface;

import net.java.slee.resource.http.events.HttpServletRequestEvent;

import com.opencloud.sleetck.lib.sbbutils.BaseTCKSbb;

/**
 * Declares event handler with signature for custom SBB ACI instead of generic
 * ACI
 * 
 * @author Ivelin Ivanov
 * @author amit.bhayani
 * 
 */
public abstract class TestHttpRaSbb extends BaseTCKSbb {

	public void onPost(HttpServletRequestEvent event,
			ActivityContextInterface aci) {
		try {

			HttpServletResponse response = event.getResponse();

			PrintWriter w = response.getWriter();
			w.print("onPost OK!");

			w.flush();

			response.flushBuffer();

			System.out
					.println("TestHttpRaSBB: Request received and OK! response sent.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HttpServletRequestEvent received!");
	}

	public void onGet(HttpServletRequestEvent event,
			ActivityContextInterface aci) {
		try {

			HttpServletResponse response = event.getResponse();

			PrintWriter w = response.getWriter();
			w.print("onGet OK!");

			w.flush();

			response.flushBuffer();

			System.out
					.println("TestHttpRaSBB: Request received and OK! response sent.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HttpServletRequestEvent received!");
	}
}
