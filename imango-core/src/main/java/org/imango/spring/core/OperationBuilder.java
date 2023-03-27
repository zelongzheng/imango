package org.imango.spring.core;


import com.mongodb.client.MongoDatabase;
import org.bson.json.JsonParseException;
import org.imango.spring.dto.ParseDto;
import org.imango.spring.exception.SDKException;
import org.imango.spring.parse.MongoParse;
import org.imango.spring.parse.SQLParse;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 查询专用器  ---> 基于mongoTemplate拓展,重写doGetDatabase实现数据源查询动态切换
 *                 1.SQL语句查询
 *                 2.MongoDB原生语句查询
 */
public class OperationBuilder extends MongoTemplate {

    public Object sql(String sql){
        ParseDto parse = SQLParse.parse(sql);
        return null;
    }

    public String mongo(String mongoSql){
        String result = "";
        try {
            result = MongoParse.OperateDB(mongoSql, true);
        } catch (JsonParseException jsonParseException){
            throw new SDKException("500",SDKException.PARSE_ERROR+"\n\t"+jsonParseException.toString());
        }
        return result;
    }

    public OperationBuilder(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }

    @Override
    protected MongoDatabase doGetDatabase() {
        MongoDbFactory mongoDbFactory = MongoContext.getMongoDbFactory();
        return mongoDbFactory == null ? super.doGetDatabase() : mongoDbFactory.getDb();
    }

}
