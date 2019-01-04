package com.mobius.software.coap.testsuite.runner.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class TemplateParser
{
	private static TemplateParser INSTANCE;

	private Map<String, String> templates = new HashMap<>();

	public static TemplateParser getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new TemplateParser();
		return INSTANCE;
	}

	public static void initLocal()
	{
		getInstance().addTemplate("controller.1.ip", "127.0.0.1");
		getInstance().addTemplate("controller.1.port", "9998");
		getInstance().addTemplate("controller.2.ip", "127.0.0.1");
		getInstance().addTemplate("controller.2.port", "9998");
		getInstance().addTemplate("controller.3.ip", "127.0.0.1");
		getInstance().addTemplate("controller.3.port", "9998");
		getInstance().addTemplate("broker.ip", "127.0.2.1");
	}

	public static void initRemote()
	{
		getInstance().addTemplate("controller.1.ip", "51.136.26.168");
		getInstance().addTemplate("controller.1.port", "9998");
		getInstance().addTemplate("controller.2.ip", "51.144.104.152");
		getInstance().addTemplate("controller.2.port", "9998");
		getInstance().addTemplate("controller.3.ip", "40.114.202.153");
		getInstance().addTemplate("controller.3.port", "9998");
		getInstance().addTemplate("broker.ip", "52.174.41.32");
	}

	public void addTemplate(String name, String value)
	{
		templates.put(name, value);
	}

	public String fileToStringProcessTemplates(File jsonFile) throws IOException
	{
		String textual = FileUtils.readFileToString(jsonFile);
		for (Entry<String, String> entry : templates.entrySet())
		{
			while (textual.contains(entry.getKey()))
				textual = textual.replace(entry.getKey(), entry.getValue());
		}
		return textual;
	}
}
