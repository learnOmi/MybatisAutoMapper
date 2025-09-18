package com.main;

import com.main.bean.TableInfo;
import com.main.builder.*;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        BuildBase.execute();
        List<TableInfo> tableInfoList = BuildTable.getTables();
        for (TableInfo tableInfo : tableInfoList) {
            BuildPo.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXml.execute(tableInfo);
        }
    }
}
