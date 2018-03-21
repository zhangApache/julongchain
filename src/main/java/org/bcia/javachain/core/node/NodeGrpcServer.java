/**
 * Copyright DingXuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.node;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.endorser.IEndorserServer;
import org.bcia.javachain.core.events.IEventHubServer;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.gossip.GossipGrpc;
import org.bcia.javachain.protos.gossip.Message;
import org.bcia.javachain.protos.node.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static org.bcia.javachain.protos.gossip.GossipGrpc.getPingMethod;

/**
 * 节点GRPC服务
 *
 * @author zhouhui
 * @Date: 2018/3/13
 * @company Dingxuan
 */
@Component
public class NodeGrpcServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeGrpcServer.class);
    /**
     * 监听的端口
     */
    private int port = 7051;
    /**
     * grpc框架定义的服务器抽象
     */
    private Server server;
    /**
     * 业务服务1:背书服务
     */
    private IEndorserServer endorserServer;
    /**
     * 业务服务2:事件处理服务
     */
    private IEventHubServer eventHubServer;

    public NodeGrpcServer(int port) {
        this.port = port;
    }

    /**
     * 绑定背书服务
     *
     * @param endorserServer
     */
    public void bindEndorserServer(IEndorserServer endorserServer) {
        this.endorserServer = endorserServer;
    }

    /**
     * 绑定事件处理服务
     *
     * @param eventHubServer
     */
    public void bindEventHubServer(IEventHubServer eventHubServer) {
        this.eventHubServer = eventHubServer;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new EndorserServerImpl())
                .build()
                .start();
        log.info("NodeGrpcServer start, port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("shutting down NodeGrpcServer since JVM is shutting down");
                NodeGrpcServer.this.stop();
                log.error("NodeGrpcServer shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // block 一直到退出程序
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        NodeGrpcServer server = new NodeGrpcServer(7051);
        server.start();
        server.blockUntilShutdown();
    }

    private class EndorserServerImpl extends EndorserGrpc.EndorserImplBase {
        @Override
        public void processProposal(ProposalPackage.SignedProposal request, StreamObserver<ProposalResponsePackage.ProposalResponse> responseObserver) {
            if (endorserServer != null) {
                ProposalResponsePackage.ProposalResponse proposalResponse = endorserServer.processProposal(request);
                responseObserver.onNext(proposalResponse);
                responseObserver.onCompleted();
            } else {
                log.error("endorserServer is not ready, but client sent some message: " + request);
                responseObserver.onError(new NodeException("endorserServer is not ready"));
            }
        }
    }

    private class EventServerImpl extends EventsGrpc.EventsImplBase {
        @Override
        public StreamObserver<EventsPackage.SignedEvent> chat(StreamObserver<EventsPackage.Event> responseObserver) {
            return new StreamObserver<EventsPackage.SignedEvent>() {
                @Override
                public void onNext(EventsPackage.SignedEvent value) {
                    if (eventHubServer != null) {
                        EventsPackage.Event resultEvent = eventHubServer.chat(value);
                        responseObserver.onNext(resultEvent);
                    } else {
                        log.error("eventHubServer is not ready, but client sent some message: " + value);
                        responseObserver.onError(new NodeException("eventHubServer is not ready"));
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }
    }

    private class DeliverServerImpl extends DeliverGrpc.DeliverImplBase {
        @Override
        public StreamObserver<Common.Envelope> deliver(StreamObserver<EventsPackage.DeliverResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }

        @Override
        public StreamObserver<Common.Envelope> deliverFiltered(StreamObserver<EventsPackage.DeliverResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }
    }

    private class GossipServerImpl extends GossipGrpc.GossipImplBase {
        @Override
        public StreamObserver<Message.Envelope> gossipStream(StreamObserver<Message.Envelope> responseObserver) {
            return new StreamObserver<Message.Envelope>() {
                @Override
                public void onNext(Message.Envelope value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }

        @Override
        public void ping(Message.Empty request, StreamObserver<Message.Empty> responseObserver) {

        }

    }

    private class AdminServerImpl extends AdminGrpc.AdminImplBase {
        @Override
        public void getStatus(Empty request, StreamObserver<AdminPackage.ServerStatus>
                responseObserver) {
        }

        @Override
        public void startServer(Empty request, StreamObserver<AdminPackage.ServerStatus> responseObserver) {

        }

        @Override
        public void getModuleLogLevel(AdminPackage.LogLevelRequest request, StreamObserver<AdminPackage.LogLevelResponse> responseObserver) {

        }

        @Override
        public void setModuleLogLevel(AdminPackage.LogLevelRequest request, StreamObserver<AdminPackage.LogLevelResponse> responseObserver) {

        }

        @Override
        public void revertLogLevels(Empty request, StreamObserver<Empty> responseObserver) {

        }

    }


}
