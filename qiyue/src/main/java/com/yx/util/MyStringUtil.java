package com.yx.util;
public class MyStringUtil {
	 
    /**
     * ��������ַ���
     *
     * @param stringLength:���ɵ��ַ�������
     * @return
     */
    public static String getRandomString() {
        String string = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            int index = (int) Math.floor(Math.random() * string.length());//����ȡ��0-25
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }
 
 
}