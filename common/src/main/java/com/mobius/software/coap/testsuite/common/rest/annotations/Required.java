package com.mobius.software.coap.testsuite.common.rest.annotations;

public @interface Required
{
	RequiredType type() default RequiredType.NOT_NULL;
}
