package com.main.builder;

import com.main.bean.Constants;
import com.main.utils.DateUtils;
import com.main.utils.PropertiesUtils;

import java.io.BufferedWriter;
import java.util.Date;

public class BuildComment {
    public static void createClassComment (BufferedWriter bw, String classComment) throws Exception {
        bw.write("/**\n");
        bw.write(" * " + classComment + "\n");
        bw.write(" * @author " + Constants.AUTHOR_COMMENT + "\n");
        bw.write(" * @since " + DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT_SLASH) + "\n");
        bw.write(" */\n");
    }

    public static void createMethodComment (BufferedWriter bw, String methodComment) throws Exception {
    }

    public static void createFieldComment (BufferedWriter bw, String fieldComment) throws Exception {
        bw.write("\t/**\n");
        bw.write("\t * " + (fieldComment == null ? "" : fieldComment) + "\n");
        bw.write("\t */\n");
    }
}
