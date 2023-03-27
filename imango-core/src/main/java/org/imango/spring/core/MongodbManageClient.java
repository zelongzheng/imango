package org.imango.spring.core;

import org.imango.spring.constant.IMangoOptions;
import org.imango.spring.service.MongodbManageService;

public interface MongodbManageClient {

    OperationBuilder getOperationBuilder();

    void switchDatabase(IMangoOptions options);

    static MongodbManageClient create(IMangoOptions options) {
        return new MongodbManageService(options);
    }

}
