package com.main;

import com.main.bean.TableInfo;
import com.main.builder.BuildTable;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTables();
    }
}
