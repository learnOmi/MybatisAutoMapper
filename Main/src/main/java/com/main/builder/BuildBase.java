package com.main.builder;

import com.main.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuildBase {
    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute () {
        List<String> headerInfoList = new ArrayList<>();

        headerInfoList.add("package " + Constants.PACKAGE_ENUM + ";");
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUM);

        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_UTILS + ";");
        build(headerInfoList, "DateUtils", Constants.PATH_UTILS);

        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_MAPPER + ";");
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPER);
    }

    private static void build (List<String> headerInfoList, String fileName, String outPath){
        File folder = new File(outPath);
        if (!folder.exists()){
            folder.mkdirs();
        }

        File javaFile = new File(outPath, fileName + ".java");

        try (OutputStream out = new FileOutputStream(javaFile);
             OutputStreamWriter outw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(outw);
             InputStream in = new FileInputStream(Objects.requireNonNull(BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt")).getPath());
             InputStreamReader inr = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(inr);
             ) {

            for (String headerInfo : headerInfoList) {
                bw.write(headerInfo);
                bw.newLine();
            }
            bw.newLine();

            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
