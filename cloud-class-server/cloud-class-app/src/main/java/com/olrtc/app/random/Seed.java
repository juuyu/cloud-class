package com.olrtc.app.random;

/**
 * @author njy
 * @since 2023/3/1 09:57
 */
public class Seed {

    /*

    1、定义一个起始值，例如 "000"。
    2、每次生成字符串时，将当前字符串解析为一个整数，并将其加一。
    3、将加一后的整数转换为指定进制的字符串，例如 36 进制，其中数字和字母组合在一起。
    4、将转换后的字符串补齐至指定长度，例如三位数，不足三位时在左侧补充 0。
    5、将生成的字符串返回，并将其作为下一个字符串的起始值。

     */


    private final int    base;
    private final int    length;
    private       String currentString;


    public Seed(int base, int length) {
        this.base = base;
        this.length = length;
        this.currentString = "0".repeat(length);
    }


    public String getNextString() {
        int intValue = Integer.parseInt(currentString, base);
        intValue = (intValue + 1) % (int) Math.pow(base, length);
        String newString = Integer.toString(intValue, base);
        newString = "0".repeat(length - newString.length()) + newString;
        currentString = newString;
        return currentString;
    }

    public static void main(String[] args) {
        Seed generator = new Seed(36, 3);
        for (int i = 0; i < 1000; i++) {
            String str = generator.getNextString();
            System.out.println(str);
        }
    }


}
