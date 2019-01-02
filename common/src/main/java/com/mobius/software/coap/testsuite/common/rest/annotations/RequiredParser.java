package com.mobius.software.coap.testsuite.common.rest.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import com.mobius.software.coap.testsuite.common.rest.ResponseData;

public class RequiredParser
{
	public static String validate(Object obj)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			List<Field> fields = collectAnnotatedFields(obj.getClass(), Required.class);
			for (Field field : fields)
			{
				Required required = field.getAnnotation(Required.class);
				if (!validate(field.get(obj), required.type()))
					sb.append("invalid value for " + field.getName() + ":" + field.get(obj));
			}
		}
		catch (Exception e)
		{
			sb.append("error while parsing required type:" + e.getMessage());
		}

		if (sb.length() == 0)
			sb.append(ResponseData.OK);

		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	private static boolean validate(Object value, RequiredType type)
	{
		boolean result = false;
		switch (type)
		{
		case NOT_NULL:
			if (value != null)
				result = true;
			break;
		case GREATER_THEN_ZERO:
			if (value != null && value instanceof Number)
			{
				Long num = Long.valueOf(String.valueOf(value));
				result = num > 0;
			}
			break;
		case NOT_EMPTY:
			if (value != null)
			{
				if (value instanceof String)
				{
					String str = (String) value;
					result = !str.isEmpty();
				}
				else if (isCollection(value))
				{
					Collection collection = (Collection) value;
					result = !collection.isEmpty();
				}
				else if (isMap(value))
				{
					Map map = (Map) value;
					result = !map.isEmpty();
				}
			}
			break;
		case POSITIVE:
			if (value != null && value instanceof Number)
			{
				Long num = Long.valueOf(String.valueOf(value));
				result = num >= 0;
			}
			break;
		default:
			break;
		}

		return result;
	}

	private static boolean isCollection(Object obj)
	{
		if (obj instanceof Collection)
			return true;
		return false;
	}

	private static boolean isMap(Object obj)
	{
		if (obj instanceof Map)
			return true;
		return false;
	}

	private static <T extends Annotation> List<Field> collectAnnotatedFields(Class<?> clazz, Class<T> annot)
	{
		List<Field> fields = new ArrayList<>();
		for (Class<?> c : getClassesIncludeParents(clazz))
		{
			fields.addAll(Arrays.asList(c.getDeclaredFields()).stream() //
					.peek(f -> f.setAccessible(true)) //
					.filter(f -> Objects.nonNull(f.getAnnotation(annot))) //
					.collect(Collectors.toList()));//
		}
		return fields;
	}

	private static List<Class<?>> getClassesIncludeParents(Class<?> clazz)
	{
		LinkedList<Class<?>> classes = new LinkedList<>();
		Class<?> c = clazz;
		while (!c.equals(Object.class))
		{
			classes.addFirst(c);
			c = c.getSuperclass();
		}
		return classes;
	}
}
