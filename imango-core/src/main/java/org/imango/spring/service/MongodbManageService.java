package org.imango.spring.service;

import org.imango.spring.constant.IMangoOptions;
import org.imango.spring.core.MongoContext;
import org.imango.spring.core.MongodbManageClient;
import org.imango.spring.core.OperationBuilder;
import org.imango.spring.parse.MongoParse;

public class MongodbManageService implements MongodbManageClient {

    public MongodbManageService(IMangoOptions options) {
        switchMethod(options);
    }

    @Override
    public OperationBuilder getOperationBuilder() {
        return new OperationBuilder(MongoContext.getMongoDbFactory());
    }

    @Override
    public void switchDatabase(IMangoOptions options) {
        switchMethod(options);
    }

    private void switchMethod(IMangoOptions options) {
        // 带用户名密码登录方式
        // mongodb://username:password@host:port
        MongoContext.addMongoDBFactory(options);
        MongoContext.setMongoDBFactory(options);
        MongoParse.operationBuilder = new OperationBuilder(MongoContext.getMongoDbFactory());
    }
}
