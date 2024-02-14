package org.apache.tinkerpop.gremlin.driver;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class StreamChannelizer extends ChannelInboundHandlerAdapter {

    private ChannelHandler requestEncoder;
    private ChannelHandler responseDecoder;
    private ChannelHandler responseHandler;

    public StreamChannelizer(ChannelHandler gremlinRequestEncoder, ChannelHandler gremlinResponseDecoder, ChannelHandler gremlinResponseHandler) {
        requestEncoder = gremlinRequestEncoder;
        responseDecoder = gremlinResponseDecoder;
        responseHandler = gremlinResponseHandler;
    }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            ctx.pipeline().addLast("gremlin-encoder", requestEncoder);
            ctx.pipeline().addLast("gremlin-decoder", responseDecoder);
            ctx.pipeline().addLast("gremlin-handler", responseHandler);
        }
}
