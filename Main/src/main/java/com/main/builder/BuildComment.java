package com.main.builder;

import java.io.BufferedWriter;

public class BuildComment {
    public static void createClassComment (BufferedWriter bw, String classComment) throws Exception {
        bw.write("/**\n");
        bw.write(" * " + classComment + "\n");
        bw.write(" * @Date:" + "\n");
        bw.write(" */\n");
    }

    public static void createMethodComment (BufferedWriter bw, String methodComment) throws Exception {

    }

    public static void createFieldComment (BufferedWriter bw, String fieldComment) throws Exception {

    }
}
