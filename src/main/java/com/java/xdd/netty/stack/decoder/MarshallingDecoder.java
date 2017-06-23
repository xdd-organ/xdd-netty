package com.java.xdd.netty.stack.decoder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

public class MarshallingDecoder {
    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException{
        this.unmarshaller = null;
    }

    protected Object decode(ByteBuf in) {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        //ChannelBufferByteInput output = new ChannelBufferByteInput(buf);

        return null;
    }
}
