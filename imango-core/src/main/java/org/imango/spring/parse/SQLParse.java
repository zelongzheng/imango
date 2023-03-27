package org.imango.spring.parse;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.imango.spring.dto.LimitDto;
import org.imango.spring.dto.ParseDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLParse {

    public static ParseDto parse(String sql){
        System.out.println("要解析的SQL为 ： " + sql);
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        // 单语句解析，只有一条数据
        if (!sql.isEmpty()) {
            SQLStatement sqlStatement = statementList.get(0);
            // 插入语句解析
            if (sqlStatement instanceof SQLServerInsertStatement) {
                // 转换
                SQLServerInsertStatement insertStatement = (SQLServerInsertStatement) sqlStatement;
                // 获取列名
                List<SQLExpr> columns = insertStatement.getColumns();
                List<String> columnsName = new ArrayList<>(columns.size());
                for (SQLExpr column : columns) {
                    columnsName.add(((SQLIdentifierExpr) column).getName());
                }
                System.out.println(columnsName);
                // 获取值
                List<SQLInsertStatement.ValuesClause> valuesList = insertStatement.getValuesList();
                List<List<Object>> dataList = new ArrayList<>();
                for (SQLInsertStatement.ValuesClause valuesClause : valuesList) {
                    List<SQLExpr> values = valuesClause.getValues();
                    List<Object> data = new ArrayList<>(columnsName.size());
                    for (SQLExpr value : values) {
                        data.add(getValue(value));
                    }
                    dataList.add(data);
                }
                System.out.println(dataList);
                // 获取表名
                System.out.println(insertStatement.getTableName().getSimpleName());
            } else if (sqlStatement instanceof SQLServerUpdateStatement) {
                // 更新语句解析
                SQLServerUpdateStatement updateStatement = (SQLServerUpdateStatement) sqlStatement;
                // 获取更新的值和内容
                List<SQLUpdateSetItem> items = updateStatement.getItems();
                Map<String, Object> updateMap = new HashMap<>(items.size());
                for (SQLUpdateSetItem item : items) {
                    updateMap.put(((SQLIdentifierExpr) item.getColumn()).getName(), getValue(item.getValue()));
                }
                System.out.println(updateMap);
                // 获取条件，条件比较复杂，需根据实际情况自行提取
                SQLBinaryOpExpr where = (SQLBinaryOpExpr) updateStatement.getWhere();
                System.out.println(where);
                // 获取表名
                System.out.println(updateStatement.getTableName().getSimpleName());
            }  else if (sqlStatement instanceof SQLSelectStatement) {
                // 查询语句解析
                SQLSelectStatement selectStatement = (SQLSelectStatement) sqlStatement;
                SQLSelect select = selectStatement.getSelect();
                SQLSelectQueryBlock query = (SQLSelectQueryBlock) select.getQuery();
                SQLExprTableSource tableSource = (SQLExprTableSource) query.getFrom();
                String tableName = tableSource.getExpr().toString();
                System.out.println("表名:"+tableName);
                SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr)query.getWhere();
                List<LimitDto> s = new ArrayList<>();
                List<LimitDto> lefts = parseLimit(sqlBinaryOpExpr, s);
                for (LimitDto left : lefts) {
                    System.out.println(left.toString());
                }
                if (sqlBinaryOpExpr!=null){
                    System.out.println("条件参数:"+sqlBinaryOpExpr);
                    String m = sqlBinaryOpExpr.toString();
                    m = m.replace("\n\t"," ");
                    //第一个条件是and
                    List<LimitDto> limitDtos = new ArrayList<>();
                    System.out.println(m);
                }

                ParseDto parseDto = new ParseDto();
                parseDto.setCollection(tableName);

                // 这里新增的条件，如果语法不正确会报错。如果条件不正确，需要执行了sql后才会报错。
                query.addCondition("name like 'admin%'");

            }
        }
        return null;
    }

    private static Object getValue(SQLExpr value) {
        // TODO 判断更多的种类
        if (value instanceof SQLIntegerExpr) {
            // 值是数字
            return ((SQLIntegerExpr) value).getNumber();
        } else if (value instanceof SQLCharExpr) {
            // 值是字符串
            return ((SQLCharExpr) value).getText();
        }
        return null;
    }

    public static List<LimitDto> parseLimit(SQLBinaryOpExpr sqlBinaryOpExpr, List<LimitDto> limitDtos){
        LimitDto limitDto = new LimitDto();
        //遍历左部
        if (sqlBinaryOpExpr.getLeft() instanceof SQLBinaryOpExpr){
            parseLimit((SQLBinaryOpExpr) sqlBinaryOpExpr.getLeft(),limitDtos);
        } else if (sqlBinaryOpExpr.getLeft() instanceof SQLInListExpr){
            limitDto.setValue(sqlBinaryOpExpr.toString());
            limitDtos.add(limitDto);
        } else if (sqlBinaryOpExpr.getLeft() instanceof SQLIdentifierExpr){
            limitDto.setValue(sqlBinaryOpExpr.toString());
            limitDtos.add(limitDto);
        }

        //遍历右部
        if (sqlBinaryOpExpr.getRight() instanceof SQLBinaryOpExpr){
            parseLimit((SQLBinaryOpExpr) sqlBinaryOpExpr.getRight(),limitDtos);
        } else if (sqlBinaryOpExpr.getRight() instanceof SQLInListExpr){
            limitDto.setValue(sqlBinaryOpExpr.toString());
            limitDtos.add(limitDto);
        } else if (sqlBinaryOpExpr.getRight() instanceof SQLIdentifierExpr){
            limitDto.setValue(sqlBinaryOpExpr.toString());
            limitDtos.add(limitDto);
        }
        return limitDtos;
    }

}
