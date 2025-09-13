package com.main.builder;

import com.main.bean.Constants;
import com.main.bean.TableInfo;

import java.io.File;
import java.io.IOException;

public class BuildPo {
    public static void execute (TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, tableInfo.getBeanName() + ".java");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
