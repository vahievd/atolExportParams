package com.atolExportParams;
import ru.atol.drivers10.fptr.*;

import java.io.*;
import java.net.Inet4Address;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {


        IFptr fptr = new Fptr();
        fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_MODEL, String.valueOf(IFptr.LIBFPTR_MODEL_ATOL_AUTO));
        fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_USB));
        fptr.applySingleSettings();
        fptr.open();

        int i=-1;
        while (!fptr.isOpened() && i<10){
            i++;
            fptr.close();
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_COM));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_COM_FILE, "/dev/ttyS"+i);
            fptr.applySingleSettings();
            fptr.open();
        }
        if (i==10){
            System.out.println("Error=NoConnect");
        }else {

            int[] nums=new int[346];
            for (int j = 0; j < 346; j++){nums[j]=j;}
            fptr.setParam(IFptr.LIBFPTR_PARAM_JSON_DATA, "{\"type\": \"getDeviceParameters\", \"keys\": "+ Arrays.toString(nums)+"}");

            fptr.processJson();

            String result=fptr.getParamString(IFptr.LIBFPTR_PARAM_JSON_DATA);
            System.out.println(result);
            result=result.substring(1,result.length()-2);
            result=result.replaceAll("[{]\"errorCode\":.+?},","");

            String IP=Inet4Address.getLocalHost().getHostAddress();
            FileWriter fw=new FileWriter("Params/params_"+IP+".json");
            fw.write(result);
            fw.flush();
            fw.close();

/*            String params="";
            try {
                FileInputStream fis;
                fis=new FileInputStream("C:\\Auto\\hello.txt");
                int q = -1;
                while(( q = fis.read()) != -1){
                    params+=(char)q;
                }
                fis.close();
            } catch(IOException e){
                System.out.println(e.getMessage());
            }
            params=params.substring(1,params.length()-2);
            params=params.replaceAll("[{]\"errorCode\":.+?},","");
            System.out.println(params);

            fptr.setParam(IFptr.LIBFPTR_PARAM_JSON_DATA, "{\"type\": \"setDeviceParameters\",\n"+params+"\n}");
            fptr.processJson();
            System.out.println(fptr.getParamString(IFptr.LIBFPTR_PARAM_JSON_DATA));
*/

        }
        fptr.close();
        fptr.destroy();
    }
}