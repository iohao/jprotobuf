/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.bjf.remoting.protobuf.v3.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.simplestring.StringTypeClass.StringMessage;
import com.baidu.bjf.remoting.protobuf.simplestring.StringTypePOJOClass;
import com.baidu.bjf.remoting.protobuf.simpletypes.AllTypes.InterClassName;
import com.baidu.bjf.remoting.protobuf.v3.any.AnyProtos.AnyObject;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import junit.framework.Assert;

/**
 * The Class AnyTest.
 * @since 2.4.3
 */
public class AnyTest {
    
    /**
     * Encode origin decode jprotobuf.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void encodeOriginDecodeJprotobuf() throws IOException {
        StringTypePOJOClass pojo = new StringTypePOJOClass();
        pojo.setStr("hello world");
        com.baidu.bjf.remoting.protobuf.Any any = com.baidu.bjf.remoting.protobuf.Any.pack(pojo);

        String m = "hello xiemalin.";
        AnyPOJO anyPojo = new AnyPOJO();
        anyPojo.setMessage(m);
        
        List<com.baidu.bjf.remoting.protobuf.Any> details = new ArrayList<com.baidu.bjf.remoting.protobuf.Any>();
        details.add(any);
        anyPojo.setDetails(details);
        
        Codec<AnyPOJO> codec = ProtobufProxy.create(AnyPOJO.class);
        // do encode and decode
        byte[] bytes = codec.encode(anyPojo);
        AnyPOJO anyPojo2 = codec.decode(bytes);
        
        
        Assert.assertEquals(m, anyPojo2.getMessage());
        List<com.baidu.bjf.remoting.protobuf.Any> details2 = anyPojo2.getDetails();
        Assert.assertEquals(1, details2.size());
        
        for (com.baidu.bjf.remoting.protobuf.Any any3 : details2) {
            boolean b = any3.is(StringTypePOJOClass.class);
            Assert.assertTrue(b);
            if (b) {
                StringTypePOJOClass unpack = any3.unpack(StringTypePOJOClass.class);
                Assert.assertEquals(pojo.getStr(), unpack.getStr());
            }
        }
        
    }

    
    /**
     * Test use any.
     *
     * @throws InvalidProtocolBufferException the invalid protocol buffer exception
     */
    @Test
    public void testUseOriginAny() throws InvalidProtocolBufferException {
        
        StringMessage message = StringMessage.newBuilder().setList("hello world").build();
        
        InterClassName message2 = InterClassName.newBuilder().setInt32F(1333).build();
        
        
        AnyObject any = AnyObject.newBuilder().addDetails(Any.pack(message)).addDetails(Any.pack(message2)).build();
        
        byte[] byteArray = any.toByteArray();
        
        AnyObject anyObject = any.parseFrom(byteArray);
        System.out.println(anyObject);
        
        List<Any> detailsList = anyObject.getDetailsList();
        for (Any any2 : detailsList) {
            if (any2.is(StringMessage.class)) {
                StringMessage unpack = any2.unpack(StringMessage.class);
                
                Assert.assertEquals(message.getList(), unpack.getList());
            }
        }
    }
    
    
    /**
     * Test any.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testAny() throws IOException {
        StringTypePOJOClass pojo = new StringTypePOJOClass();
        pojo.setStr("hello world");
        
        com.baidu.bjf.remoting.protobuf.Any any = com.baidu.bjf.remoting.protobuf.Any.pack(pojo);
        
        Codec<com.baidu.bjf.remoting.protobuf.Any> codec = ProtobufProxy.create(com.baidu.bjf.remoting.protobuf.Any.class);
        byte[] bytes = codec.encode(any);
        com.baidu.bjf.remoting.protobuf.Any any2 = codec.decode(bytes);
        
        
        boolean b = any2.is(StringTypePOJOClass.class);
        Assert.assertTrue(b);
        StringTypePOJOClass unpack = any2.unpack(StringTypePOJOClass.class);
        Assert.assertEquals(pojo.getStr(), unpack.getStr());
        
    }
}