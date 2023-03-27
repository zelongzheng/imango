package org.imango.spring.test;

import org.imango.spring.constant.IMangoOptions;
import org.imango.spring.core.MongoContext;
import org.imango.spring.core.MongodbManageClient;
import org.imango.spring.core.OperationBuilder;

public class Client {

    public static void main(String[] args) {
        String querySqlWithoutFields = "db.getCollection('app_info').find({$or:[{'appId':'1'}]})";
        MongodbManageClient client = MongodbManageClient.create(IMangoOptions.builder().uri("mongodb://10.255.70.190:27017").database("imywwl").build());
        OperationBuilder operationBuilder = client.getOperationBuilder();
        String mongo = operationBuilder.mongo(querySqlWithoutFields);
        System.out.println(mongo);
        client.switchDatabase(IMangoOptions.builder().database("imywwl").uri("mongodb://10.10.134.30:10009").build());
        mongo = operationBuilder.mongo(querySqlWithoutFields);
        String insert = "db.getCollection('app_info').save({'appId':'2','appName':'TEST-API-0000068','apiKey':333,'apiSecret':456,'merchantId':'A2','status':1})";
        String mongo1 = operationBuilder.mongo(insert);
        System.out.println(mongo1);
    }

}
