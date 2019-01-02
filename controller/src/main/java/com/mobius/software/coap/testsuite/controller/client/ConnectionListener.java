package com.mobius.software.coap.testsuite.controller.client;

import com.mobius.software.coap.parser.tlv.CoapMessage;

public interface ConnectionListener
{
	void packetReceived(CoapMessage header);
}
