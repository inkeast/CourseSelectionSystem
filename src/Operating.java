import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operating {
    private static final Pattern PATTERN_INSERT = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+\\s?[*]?,?)+)\\)\\s?;");  //("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
    private static final Pattern PATTERN_ALTER_TABLE_ADD = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
    private static final Pattern PATTERN_DELETE = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern PATTERN_UPDATE = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("drop\\stable\\s(\\w+);");
    private static final Pattern PATTERN_SELECT = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+\\s?;))?");
    private static final Pattern PATTERN_EXIT = Pattern.compile("exit");

    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public void init(){
        User user = User.getUser("user1", "abc");
        File userFolder = new File("dir", user.getName());
        File dbFolder = new File(userFolder, "db1");
        Table.init(user.getName(), dbFolder.getName());
    }
    public List dbms_online(String cmd){
        Matcher matcherSelect = PATTERN_SELECT.matcher(cmd);
        Matcher matcherInsert = PATTERN_INSERT.matcher(cmd);
        Matcher matcherDelete = PATTERN_DELETE.matcher(cmd);
        Matcher matcherUpdate = PATTERN_UPDATE.matcher(cmd);
        Matcher matcherDropTable = PATTERN_DROP_TABLE.matcher(cmd);
        Matcher matcherCreateTable = PATTERN_CREATE_TABLE.matcher(cmd);
        Matcher matcherExit = PATTERN_EXIT.matcher(cmd);

        List result=null;

        if(matcherSelect.find()) {
            rwl.readLock().lock();
            result = select(matcherSelect);
            rwl.readLock().unlock();
        }

        if(matcherInsert.find()) {
            rwl.writeLock().lock();
            result = insert(matcherInsert);
            rwl.writeLock().unlock();
        }
        if(matcherDelete.find()) {
            rwl.writeLock().lock();
            result = delete(matcherDelete);
            rwl.writeLock().unlock();
        }
        if(matcherUpdate.find()) {
            rwl.writeLock().lock();
            result = update(matcherUpdate);
            rwl.writeLock().unlock();
        }
        if(matcherDropTable.find()) {
            rwl.writeLock().lock();
            dropTable(matcherDropTable);
            rwl.writeLock().unlock();
        }
        if(matcherCreateTable.find()) {
            rwl.writeLock().lock();
            createTable(matcherCreateTable);
            rwl.writeLock().unlock();
        }
        if(matcherExit.find()){
            while (rwl.getQueueLength()!=0);
            result = new LinkedList();
            result.add("exit");
        }
        return result;
    }


    private void deleteIndex(Matcher matcherDeleteIndex) {
        String tableName = matcherDeleteIndex.group(1);
        Table table = Table.getTable(tableName);
        System.out.println(table.deleteIndex());
    }

    private List select(Matcher matcherSelect) {
        //将读到的所有数据放到tableDatasMap中
        Map<String, List<Map<String, String>>> tableDatasMap = new LinkedHashMap<>();
        //将投影放在Map<String,List<String>> projectionMap中
        Map<String, List<String>> projectionMap = new LinkedHashMap<>();
        List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));
        String whereStr = matcherSelect.group(3);
        //将tableName和table.fieldMap放入
        Map<String, Map<String, Field>> fieldMaps = new HashMap();
        for (String tableName : tableNames) {
            Table table = Table.getTable(tableName);
            if (null == table) {
                System.out.println("未找到表：" + tableName);
                return null;
            }
            Map<String, Field> fieldMap = table.getFieldMap();
            fieldMaps.put(tableName, fieldMap);
            //解析选择
            List<SingleFilter> singleFilters = new ArrayList<>();
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr, tableName, fieldMap);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));
                singleFilters.add(singleFilter);
            }

            //解析最终投影
            List<String> projections = StringUtil.parseProjection(matcherSelect.group(1), tableName, fieldMap);
            projectionMap.put(tableName, projections);
            //读取数据并进行选择操作
            List<Map<String, String>> srcDatas = table.read(singleFilters);
            if(srcDatas == null ){List nulllist = new LinkedList();return nulllist;}
            List<Map<String, String>> datas = associatedTableName(tableName, srcDatas);
            tableDatasMap.put(tableName, datas);
        }
        //解析连接条件，并创建连接对象jion
        List<Map<String, String>> joinConditionMapList = StringUtil.parseWhere_join(whereStr, fieldMaps);
        List<JoinCondition> joinConditionList = new LinkedList<>();
        for (Map<String, String> joinMap : joinConditionMapList) {
            String tableName1 = joinMap.get("tableName1");
            String tableName2 = joinMap.get("tableName2");
            String fieldName1 = joinMap.get("field1");
            String fieldName2 = joinMap.get("field2");
            Field field1 = fieldMaps.get(tableName1).get(fieldName1);
            Field field2 = fieldMaps.get(tableName2).get(fieldName2);
            String relationshipName = joinMap.get("relationshipName");
            JoinCondition joinCondition = new JoinCondition(tableName1, tableName2, field1, field2, relationshipName);
            joinConditionList.add(joinCondition);
            //将连接条件的字段加入投影中
            projectionMap.get(tableName1).add(fieldName1);
            projectionMap.get(tableName2).add(fieldName2);
        }
        List<Map<String, String>> resultDatas = Join.joinData(tableDatasMap, joinConditionList, projectionMap);
        return resultDatas;
    }

    private List insert(Matcher matcherInsert) {
        String tableName = matcherInsert.group(1);
        Table table = Table.getTable(tableName);
        Map<String, Field> fieldMap = table.getFieldMap();
        List<Map<String, String>> filtList = new LinkedList<>();
        List failed = new ArrayList();
        failed.add("failed");
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            failed.add("1");
            return failed;
        }
        Map dictMap = table.getFieldMap();
        Map<String, String> data = new HashMap<>();

        String[] fieldValues = matcherInsert.group(5).trim().split(",");
        //如果插入指定的字段
        if (null != matcherInsert.group(2)) {
            String[] fieldNames = matcherInsert.group(3).trim().split(",");
            //如果insert的名值数量不相等，错误
            if (fieldNames.length != fieldValues.length) {
                return failed;
            }
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i].trim();
                String fieldValue = fieldValues[i].trim();
                //如果在数据字典中未发现这个字段，返回错误
                if (!dictMap.containsKey(fieldName)) {
                    return failed;
                }
                if(fieldMap.get(fieldNames[i].trim()).isPrimaryKey()==true){
                    Map<String, String> filtMap = new LinkedHashMap<>();
                    filtMap.put("fieldName", fieldName);
                    filtMap.put("relationshipName", "=");
                    filtMap.put("condition", fieldValue);
                    filtList.add(filtMap);
                }
                data.put(fieldName, fieldValue);
            }
        } else {//否则插入全部字段
            Set<String> fieldNames = dictMap.keySet();
            int i = 0;
            for (String fieldName : fieldNames) {
                String fieldValue = fieldValues[i].trim();
                data.put(fieldName, fieldValue);
                i++;
            }
        }
        File file = new File(table.GetIndexPath());
        if(file.exists())
            if(InsertFindDuplication(filtList,fieldMap,tableName).size()!=0){
                failed.add("2");
                return failed;
            }
        table.insert(data);
        List success = new ArrayList();
        success.add("success");
        return  success;
    }

    private List update(Matcher matcherUpdate) {
        String tableName = matcherUpdate.group(1);
        String setStr = matcherUpdate.group(2);
        String whereStr = matcherUpdate.group(3);
        List failed = new ArrayList();
        failed.add("failed");
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return failed;
        }
        Map<String, Field> fieldMap = table.getFieldMap();
        Map<String, String> data = StringUtil.parseUpdateSet(setStr);


        List<SingleFilter> singleFilters = new ArrayList<>();
        if (null == whereStr) {
            table.update(data, singleFilters);
        } else {
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }
            table.update(data, singleFilters);
        }
        List success = new ArrayList();
        success.add("success");
        return  success;
    }

    private List delete(Matcher matcherDelete) {
        String tableName = matcherDelete.group(1);
        String whereStr = matcherDelete.group(2);
        List failed = new ArrayList();
        failed.add("failed");
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return failed;
        }

        Map<String, Field> fieldMap = table.getFieldMap();

        List<SingleFilter> singleFilters = new ArrayList<>();
        if (null == whereStr) {
            table.delete(singleFilters);
        } else {
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }
            table.delete(singleFilters);
        }
        List success = new ArrayList();
        success.add("success");
        return  success;
    }

    private void createTable(Matcher matcherCreateTable) {
        String tableName = matcherCreateTable.group(1);
        String propertys = matcherCreateTable.group(2);
        Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
        System.out.println(Table.createTable(tableName, fieldMap));
    }

    private void dropTable(Matcher matcherDropTable) {
        String tableName = matcherDropTable.group(1);
        System.out.println(Table.dropTable(tableName));
    }

    private void alterTableAdd(Matcher matcherAlterTable_add) {
        String tableName = matcherAlterTable_add.group(1);
        String propertys = matcherAlterTable_add.group(2);
        Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return;
        }
        System.out.println(table.addDict(fieldMap));

    }

    /**
     * 将数据整理成tableName.fieldName dataValue的型式
     *
     * @param tableName 表名
     * @param srcDatas  原数据
     * @return 添加表名后的数据
     */
    private List<Map<String, String>> associatedTableName(String tableName, List<Map<String, String>> srcDatas) {
        List<Map<String, String>> destDatas = new ArrayList<>();
        for (Map<String, String> srcData : srcDatas) {
            Map<String, String> destData = new LinkedHashMap<>();
            for (Map.Entry<String, String> data : srcData.entrySet()) {
                destData.put(tableName + "." + data.getKey(), data.getValue());
            }
            destDatas.add(destData);
        }
        return destDatas;
    }

    private List InsertFindDuplication(List<Map<String, String>> filtList,Map<String, Field> fieldMap,String tableName){
        List<SingleFilter> singleFilters = new ArrayList<>();
        Map<String, List<String>> projectionMap = new LinkedHashMap<>();
        Table table = Table.getTable(tableName);
        //将读到的所有数据放到tableDatasMap中
        Map<String, List<Map<String, String>>> tableDatasMap = new LinkedHashMap<>();
        for (Map<String, String> filtMap : filtList) {
            SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                    , filtMap.get("relationshipName"), filtMap.get("condition"));//core

            singleFilters.add(singleFilter);
        }

        //解析最终投影
        List<String> projections = StringUtil.parseProjection("*", tableName, fieldMap);
        projectionMap.put(tableName, projections);


        //读取数据并进行选择操作
        List<Map<String, String>> srcDatas = table.read(singleFilters);
        List<Map<String, String>> datas = associatedTableName(tableName, srcDatas);

        tableDatasMap.put(tableName, datas);

        List<Map<String, String>> resultDatas = Join.joinData(tableDatasMap, null, projectionMap);

        return  resultDatas;
    }

}

