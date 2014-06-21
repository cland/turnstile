/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.cland.accessstats.util;

import cland.google.gdm.GMatrix;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.client.methods.HttpPost;
//import java.util.ArrayList;
//import java.util.List;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.NameValuePair;


public class QuickStart {

    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String origins = "-33.869952,18.537687";
        	String destinations = "-33.96979309231926,18.53386491408955|-33.90630759128996,18.40310809285091|-33.97761,18.46555";
        	String avoid = GMatrix.AVOID_HIGHWAYS + "|" +GMatrix.AVOID_TOLLS;
        	String units = GMatrix.UNITS_DEFAULT;
        	String language = "";
        	boolean sensor = true;
        	String url = GMatrix.getUrl(origins, destinations,  GMatrix.JSON, GMatrix.MOD_DRIVING, avoid, units, language, sensor);
        	System.out.println("Connection to... " + url);
        	//GET EXAMPLE
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
                System.out.println(response1.getStatusLine());
                HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                String result = EntityUtils.toString(entity1);                
                // and ensure it is fully consumed
                EntityUtils.consume(entity1);
                
                
                //Process the result json data in the the DataEntry object
                int num = 3; //we requested for 3 destinations
                
                for(int i=0;i<num;i++){
                	System.out.println(">>> DESTINATION " + i);
                	System.out.println(">>>Distance text: " + GMatrix.getDistanceText(result, 0, i));
                    System.out.println(">>>Duration : " + GMatrix.getDurationText(result, 0, i));
                }
                
            } finally {
                response1.close();
            }

            // POST EXAMPLE
//            HttpPost httpPost = new HttpPost("http://targethost/login");
//            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//            nvps.add(new BasicNameValuePair("username", "vip"));
//            nvps.add(new BasicNameValuePair("password", "secret"));
//            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//            CloseableHttpResponse response2 = httpclient.execute(httpPost);
//
//            try {
//                System.out.println(response2.getStatusLine());
//                HttpEntity entity2 = response2.getEntity();
//                // do something useful with the response body
//                // and ensure it is fully consumed
//                EntityUtils.consume(entity2);
//            } finally {
//                response2.close();
//            }
        } finally {
            httpclient.close();
        }
    }

}
