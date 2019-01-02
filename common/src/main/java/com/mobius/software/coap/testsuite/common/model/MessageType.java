package com.mobius.software.coap.testsuite.common.model;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.mobius.software.coap.parser.tlv.CoapCode;
import com.mobius.software.coap.parser.tlv.CoapMessage;
import com.mobius.software.coap.parser.tlv.CoapType;

public enum MessageType
{
	SUBSCRIBE, SUBACK, UNSUBSCRIBE, UNSUBACK, PUBLISH, PUBACK, PINGREQ, PINGRESP, INVALID;

	public static MessageType translate(CoapMessage message)
	{
		if (message.getCode() == CoapCode.GET)
		{
			Integer observe = message.options().fetchObserve();
			if (observe != null)
			{
				if (message.getType() == CoapType.CONFIRMABLE)
				{
					if (observe == 0)
						return SUBSCRIBE;

					if (observe == 1)
						return UNSUBSCRIBE;
				}
				else if (message.getType() == CoapType.ACKNOWLEDGEMENT)
				{
					if (observe == 0)
						return SUBACK;

					if (observe == 1)
						return UNSUBACK;
				}
			}
		}
		else if (message.getCode() == CoapCode.PUT || message.getCode() == CoapCode.POST)
		{
			if (!StringUtils.isEmpty(message.options().fetchUriPath()))
				return PUBLISH;
			else if (message.getType() == CoapType.ACKNOWLEDGEMENT)
			{
				if (!ArrayUtils.isEmpty(message.getToken()))
					return PUBACK;
				else
					return PINGRESP;
			}
			else if (message.getType() == CoapType.CONFIRMABLE)
				return PINGREQ;
		}

		return INVALID;
	}

	public MessageType invert()
	{
		switch (this)
		{
		case PINGRESP:
			return PINGREQ;
		case PINGREQ:
			return PINGRESP;
		case PUBACK:
			return PUBLISH;
		case PUBLISH:
			return PUBACK;
		case SUBACK:
			return SUBSCRIBE;
		case SUBSCRIBE:
			return SUBACK;
		case UNSUBACK:
			return UNSUBSCRIBE;
		case UNSUBSCRIBE:
			return UNSUBACK;
		case INVALID:
		default:
			return INVALID;
		}
	}
}
