///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package org.apache.tinkerpop.gremlin.server.handler;
//
//import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
//import io.netty.handler.codec.http2.Http2ConnectionDecoder;
//import io.netty.handler.codec.http2.Http2ConnectionEncoder;
//import io.netty.handler.codec.http2.Http2Settings;
//import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
//import org.apache.tinkerpop.gremlin.server.GraphManager;
//import org.apache.tinkerpop.gremlin.server.Settings;
//import org.apache.tinkerpop.gremlin.util.MessageSerializer;
//
//import java.util.Map;
//
//public class GremlinHttp2HandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<GremlinHttp2Handler, GremlinHttp2HandlerBuilder> {
//
//    private Map<String, MessageSerializer<?>> serializers;
//    private GremlinExecutor gremlinExecutor;
//    private GraphManager graphManager;
//    private Settings settings;
//
//    public GremlinHttp2Handler build(final Map<String, MessageSerializer<?>> serializers,
//                                     final GremlinExecutor gremlinExecutor,
//                                     final GraphManager graphManager,
//                                     final Settings settings) {
//        this.serializers = serializers;
//        this.gremlinExecutor = gremlinExecutor;
//        this.graphManager = graphManager;
//        this.settings = settings;
//        return super.build();
//    }
//
//    public GremlinHttp2Handler build() {
//        return super.build();
//    }
//
//    @Override
//    protected GremlinHttp2Handler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) throws Exception {
//        GremlinHttp2Handler handler = new GremlinHttp2Handler(decoder, encoder, initialSettings);
//        frameListener(handler);
//        return handler;
//    }
//}
