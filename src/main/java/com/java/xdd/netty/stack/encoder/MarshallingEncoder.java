package com.java.xdd.netty.stack.encoder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private Marshaller marshaller;

    public MarshallingEncoder() {
        this.marshaller = null;
    }


    protected void encode(Object msg, ByteBuf out) throws Exception {
        int lengthPos = out.writerIndex();
        out.writeBytes(LENGTH_PLACEHOLDER);
        //ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
    }
}