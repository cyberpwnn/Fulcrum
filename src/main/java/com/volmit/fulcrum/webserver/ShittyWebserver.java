package com.volmit.fulcrum.webserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.volmit.fulcrum.Fulcrum;

public class ShittyWebserver
{
	private int port;
	private File root;
	private Server server;

	public ShittyWebserver(int port, File root)
	{
		this.port = port;
		this.root = root;
		root.mkdirs();
	}

	public void start() throws Exception
	{
		System.out.println("Spinning up shitty webserver on *:" + port + " hosting root " + root.getAbsolutePath());
		server = new Server(port);
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] {"index.html"});
		resource_handler.setResourceBase(root.getAbsolutePath());

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {resource_handler, new DefaultHandler()});
		server.setHandler(handlers);
		server.start();

		try
		{
			writeResource(Fulcrum.class.getResource("/index.html"), new File(root, "index.html"));
		}

		catch(Exception e)
		{

		}
	}

	public void stop()
	{
		System.out.println("Shutting down shitty webserver.");
		try
		{
			server.stop();
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void writeResource(URL url, File f) throws IOException
	{
		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		InputStream in = url.openStream();
		byte[] buffer = new byte[1024];
		int read = 0;

		while((read = in.read(buffer)) != -1)
		{
			fos.write(buffer, 0, read);
		}

		fos.close();
		in.close();
	}
}
