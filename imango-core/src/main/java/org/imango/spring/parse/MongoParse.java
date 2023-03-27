package org.imango.spring.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.imango.spring.core.OperationBuilder;
import org.imango.spring.util.FastJsonUtil;
import org.imango.spring.util.RegExpUtil;

import java.util.Iterator;
import java.util.List;

public class MongoParse {

    public static OperationBuilder operationBuilder = null;

    public static String OperateDB(String sql, Boolean hasColumnName) {
        String result = "";
        if (sql.toUpperCase().contains(").FIND(")) {
            result = Query(sql, hasColumnName);
        }
        else if(sql.toUpperCase().contains(").REMOVE("))
        {
            result=String.valueOf(Delete(sql));
        }
        else if(sql.toUpperCase().contains(").UPDATE("))
        {
            result=String.valueOf(Update(sql));
        }
        else {
            result= String.valueOf(Create(sql));
        }
        return result;
    }

    private static String Query(String sql, Boolean hasColumnName) {
        String tableName = RegExpUtil.getTableName(sql);
        String query = RegExpUtil.getQuery(sql);
        List<String> columnNames = getColumnNames(query);
        BasicDBObject queryObject = BasicDBObject.parse(query);
        FindIterable<Document> documents = operationBuilder.getCollection(tableName).find(queryObject);
        JSONArray lists = new JSONArray();
        MongoCursor<Document> cursor = documents.cursor();
        while (cursor.hasNext()) {
            JSONObject jsonObj = new JSONObject();
            JSONArray subLists = new JSONArray();
            Document next = cursor.next();
            if (!columnNames.isEmpty()) {
                if(!hasColumnName)
                {
                    for (String columnName : columnNames)
                        subLists.add(next.get(columnName));
                    lists.add(subLists);
                }
                else {
                    for (String columnName : columnNames)
                        jsonObj.put(columnName, next.get(columnName));
                    lists.add(jsonObj);
                }
            } else {
                if(!hasColumnName)
                {
                    Iterator<String> it = next.keySet().iterator();
                    while(it.hasNext())
                        subLists.add(next.get(it.next()));
                    lists.add(subLists);
                }
                else {
                    lists.add(next);
                }
            }

        }
        return FastJsonUtil.ObjectToString(lists);
    }

    private static int Create(String sql)
    {
        String tableName = RegExpUtil.getTableName(sql);
        String create = RegExpUtil.getCreate(sql);
        Document parse = Document.parse(create);
        operationBuilder.getCollection(tableName).insertOne(parse);
        return 0;
    }

    private static long Update(String sql)
    {
        String tableName = RegExpUtil.getTableName(sql);
        String update = RegExpUtil.getUpdate(sql);
        BasicDBObject updateQueryObject = BasicDBObject.parse(update);
        String upset = getUpset(update);
        BasicDBObject upsetObject = BasicDBObject.parse(upset);
        UpdateResult updateResult = operationBuilder.getCollection(tableName).updateOne(updateQueryObject, upsetObject);
        return updateResult.getModifiedCount();
    }

    private static long Delete(String sql)
    {
        String tableName = RegExpUtil.getTableName(sql);
        String delete = RegExpUtil.getRemove(sql);
        BasicDBObject deleteObject = BasicDBObject.parse(delete);
        DeleteResult deleteResult = operationBuilder.getCollection(tableName).deleteOne(deleteObject);
        return deleteResult.getDeletedCount();
    }

    private static List<String> getColumnNames(String query) {
        String tmp = RegExpUtil.getObject(query,1);
        return RegExpUtil.getColumnNames(tmp);
    }

    private static String getUpset(String query) {
        String tmp  = RegExpUtil.getObject(query, 1);
        return tmp;
    }

}
