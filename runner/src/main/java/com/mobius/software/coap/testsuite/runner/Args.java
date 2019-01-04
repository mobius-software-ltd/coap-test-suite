package com.mobius.software.coap.testsuite.runner;

public class Args
{
	private static final String OK = "OK";

	private static final String REQUEST_FILEPATH_PREFIX = "scenarioFile=";

	private final String requestFilepath;
	
	private Args(String requestFilepath)
	{
		this.requestFilepath = requestFilepath;
	}

	public static Args parse(String[] args)
	{
		String requestFilepath = getPropertyValue(args, REQUEST_FILEPATH_PREFIX);

		Args arguments = new Args(requestFilepath);

		String validateResult = arguments.validate();
		if (!validateResult.equals(OK))
			throw new IllegalArgumentException(validateResult);

		return arguments;
	}

	private static String getPropertyValue(String[] args, String prefix)
	{
		String value = null;
		for (String arg : args)
		{
			if (arg.startsWith(prefix))
			{
				value = arg.substring(arg.indexOf(prefix) + prefix.length());
				break;
			}
		}
		return value;
	}

	public String validate()
	{
		StringBuilder errSb = new StringBuilder();
		if (requestFilepath == null || requestFilepath.isEmpty())
			errSb.append("missing required requestFilepath").append("\n");

		return errSb.length() == 0 ? OK : errSb.toString();
	}

	public String getRequestFilepath()
	{
		return requestFilepath;
	}

}
