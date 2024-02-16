/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.driver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.util.AsciiString;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;
import org.apache.tinkerpop.gremlin.util.MessageSerializer;
import org.apache.tinkerpop.gremlin.util.Tokens;
import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseStatusCode;
import org.apache.tinkerpop.gremlin.util.ser.SerTokens;
import org.apache.tinkerpop.shaded.jackson.databind.JsonNode;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

/**
 * Converts {@code HttpResponse} to a {@link ResponseMessage}.
 */
@ChannelHandler.Sharable
public final class HttpGremlinResponseDecoder extends MessageToMessageDecoder<Http2StreamFrame> {
    private final MessageSerializer<?> serializer;
    private final ObjectMapper mapper = new ObjectMapper();

    private Http2Headers headers;

    private boolean hasError = false;

    public HttpGremlinResponseDecoder(final MessageSerializer<?> serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final Http2StreamFrame frame, final List<Object> objects) throws Exception {

        if (frame instanceof Http2HeadersFrame) {
            Http2Headers h2headers = ((Http2HeadersFrame) frame).headers();
            if (AsciiString.contentEquals(h2headers.status().toString(), "200")) {
                headers = ((Http2HeadersFrame) frame).headers();
            } else {
                hasError = true;
            }
        } else if (frame instanceof Http2DataFrame) {
            ByteBuf body = ((Http2DataFrame) frame).content();
            if (!hasError) {
                for (ResponseMessage msg = serializer.deserializeResponse(content.content());
                        msg != null;
                        msg = serializer.deserializeResponse(Unpooled.buffer(0))) {

                    objects.add(msg);
                }
            } else {
                final JsonNode root = mapper.readTree(new ByteBufInputStream(body));
                objects.add(ResponseMessage.build(UUID.fromString(root.get(Tokens.REQUEST_ID).asText()))
                        .code(ResponseStatusCode.SERVER_ERROR)
                        .statusMessage(root.get(SerTokens.TOKEN_MESSAGE).asText())
                        .create());
            }
}
