package com.igalblech.school.graphicaljavascriptcompiler.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class UserTextFile {

    @Getter private String name;
    @Getter private Context context;
    File file;

    public UserTextFile(Context context, String name) throws IOException {
        this.name = name;
        this.context = context;


        file = new File(context.getFilesDir(), name);
        file.createNewFile(); // if file already exists will do nothing
        //FileOutputStream fileOutputStream = new FileOutputStream(file, true);

    }

    public void appendLine(String line) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file, true);// context.openFileOutput(name,  Context.MODE_APPEND);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        bufferedWriter.append(line);
        bufferedWriter.append("\n");

        bufferedWriter.close();
        outputStreamWriter.close();
        outputStream.close();
    }

    public String[] getLines() throws IOException {
        List<String> arr = new ArrayList<>(0);
        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        while ((receiveString = bufferedReader.readLine()) != null) {
            arr.add(receiveString);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        String[] ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public void replaceText(String[] lines) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file, false);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        for (int i = 0; i < lines.length; i++) {
            bufferedWriter.write(lines[i]);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        outputStreamWriter.close();
        outputStream.close();
    }

    public void replaceLine(int line, String newStr) throws IOException {
        String[] newContant = getLines();
        newContant[line] = newStr;
        replaceText(newContant);
    }

}
