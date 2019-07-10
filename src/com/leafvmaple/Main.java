package com.leafvmaple;

import java.io.*;
import java.util.zip.*;
import java.util.regex.Pattern;

class Zip
{
    public boolean folderFlag;
    public Pattern pattern;
    public String outputName;
    public String inputName;
    public String extraFiles[];

    public Zip() {
        this.folderFlag = true;
        this.pattern = null;
        this.outputName = null;
        this.inputName = null;
    }

    public void Compress() throws Exception {
        ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(outputName));
        BufferedOutputStream bufferOutput = new BufferedOutputStream(outStream);

        File file = new File(inputName);
        Compress(outStream, bufferOutput, file, "");

        if (extraFiles != null) {
            for (String extraFileName : extraFiles) {
                File extraFile = new File(extraFileName);
                AddFile(outStream, extraFile, extraFile.getName());
            }
        }

        outStream.closeEntry();
        outStream.close();
    }

    void Compress(ZipOutputStream out, BufferedOutputStream buff, File file, String path) throws Exception {
        String filePath;
        if (path == "") {
            filePath = file.getName();
        }
        else {
            filePath = path + "/" + file.getName();
        }
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length != 0) {
                if (folderFlag) {
                    out.putNextEntry(new ZipEntry(filePath + "/"));
                }
                for(int i = 0; i < files.length; i++) {
                    Compress(out, buff, files[i], filePath);
                }
            }
        }
        else if (pattern == null || pattern.matcher(file.getName()).find()) {
            if (folderFlag) {
                AddFile(out, file, filePath);
            }
            else {
                AddFile(out, file, file.getName());
            }
        }
    }

    void AddFile(ZipOutputStream out, File file, String fileName) throws Exception  {
        System.out.println(fileName);
        out.putNextEntry(new ZipEntry(fileName));

        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream inputBuffer = new BufferedInputStream(inputStream);

        for (int data = inputBuffer.read(); data != -1; data = inputBuffer.read()) {
            out.write(data);
        }
        inputBuffer.close();
        inputStream.close();
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        Zip zip = new Zip();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    zip.outputName = args[i + 1];
                    break;
                case "-i":
                    zip.inputName = args[i + 1];
                    break;
                case "-f":
                    zip.folderFlag = Boolean.valueOf(args[i + 1]);
                    break;
                case "-r":
                    zip.pattern = Pattern.compile(args[i + 1]);
                    break;
                case "-a":
                    zip.extraFiles = args[i + 1].split("\\|");
            }
        }

        if (zip.outputName == null) {
            zip.outputName = args[0];
        }
        if (zip.inputName == null) {
            zip.inputName = args[1];
        }
        zip.Compress();
    }
}
