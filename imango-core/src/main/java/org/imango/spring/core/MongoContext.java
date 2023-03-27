package org.imango.spring.core;


import com.mongodb.ConnectionString;
import org.imango.spring.constant.IMangoOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class MongoContext {

    private static final Map<String, MongoDbFactory> MONGO_CLIENT_DB_FACTORY_MAP = new HashMap<>();
    private static final ThreadLocal<MongoDbFactory> MONGO_DB_FACTORY_THREAD_LOCAL = new ThreadLocal<>();

    @Bean(name = "mongoTemplate")
    public OperationBuilder dynamicMongoTemplate() {
        Iterator<MongoDbFactory> iterator = MONGO_CLIENT_DB_FACTORY_MAP.values().iterator();
        return new OperationBuilder(iterator.next());
    }

    @Bean(name = "mongoDbFactory")
    public MongoDbFactory mongoDbFactory() {
        Iterator<MongoDbFactory> iterator = MONGO_CLIENT_DB_FACTORY_MAP.values().iterator();
        return iterator.next();
    }

    public static void addMongoDBFactory(IMangoOptions iMangoOptions){
        //mongodb://username:password@host:port
        if(!MONGO_CLIENT_DB_FACTORY_MAP.containsKey(iMangoOptions.getUri()+iMangoOptions.getDatabase())){
            final ConnectionString connectionString = new ConnectionString(iMangoOptions.getUri());
            setDatabaseName(connectionString,iMangoOptions.getDatabase());
            MONGO_CLIENT_DB_FACTORY_MAP.put(iMangoOptions.getUri()+iMangoOptions.getDatabase(), new SimpleMongoClientDbFactory(connectionString));
        }
    }

    public static void setMongoDBFactory(IMangoOptions iMangoOptions) {
        MONGO_DB_FACTORY_THREAD_LOCAL.set(MONGO_CLIENT_DB_FACTORY_MAP.get(iMangoOptions.getUri()+iMangoOptions.getDatabase()));
    }

    public static MongoDbFactory getMongoDbFactory() {
        return MONGO_DB_FACTORY_THREAD_LOCAL.get();
    }

    public static void removeAll() {
        MONGO_DB_FACTORY_THREAD_LOCAL.remove();
    }

    public static void setDatabaseName(final ConnectionString p, String database) {
        final Class<?> clz = p.getClass();
        try {
            final Field nameField = clz.getDeclaredField("database");
            nameField.setAccessible(true);
            nameField.set(p, String.valueOf(database));
        } catch (final NoSuchFieldException e) {
            e.printStackTrace();
        } catch (final SecurityException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}