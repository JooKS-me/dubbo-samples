/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.dubbo.sample.tri;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

class ApiWrapperConsumer {
    private final IWrapperGreeter delegate;

    public ApiWrapperConsumer() {
        ReferenceConfig<IWrapperGreeter> ref = new ReferenceConfig<>();
        ref.setInterface(IWrapperGreeter.class);
        ref.setCheck(false);
        ref.setTimeout(3000);
        ref.setProtocol("tri");
        ref.setLazy(true);

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(new ApplicationConfig("demo-consumer"))
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(ref)
                .start();
        this.delegate = ref.get();
    }

    public static void main(String[] args) {
        final ApiWrapperConsumer consumer = new ApiWrapperConsumer();
        System.out.println("dubbo triple wrapper consuemr started");
        consumer.sayHelloUnary();
        consumer.sayHelloStream();
        consumer.sayHelloServerStream();
    }

    public void sayHelloUnary() {
        System.out.println(delegate.sayHello("unary"));
    }

    public void sayHelloServerStream() {
        delegate.sayHelloServerStream("server stream", new StdoutStreamObserver<>("sayHelloServerStream"));

    }

    public void sayHelloStream() {
        final StreamObserver<String> request = delegate.sayHelloStream(new StdoutStreamObserver<>("sayHelloStream"));
        for (int i = 0; i < 10; i++) {
            request.onNext("stream request");
        }
        request.onCompleted();
    }

}
