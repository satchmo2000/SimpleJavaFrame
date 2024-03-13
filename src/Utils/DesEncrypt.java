package Utils;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Base64;
import java.util.Scanner;

public class DesEncrypt {
    static byte[] bytekey;

    private DesEncrypt(String strKey) {
        this.bytekey = strKey.getBytes();
    }

    private static DesEncrypt instance;
    private static byte[][] storecry;

    //单利模式，可以直接访问该类，不需要实例化该类的对象。保证一个类仅有一个实例，并提供一个访问它的全局访问点。
    //懒汉式，线程安全，synchronized实现多线程安全
    public static synchronized DesEncrypt getInstance(String strKey) {
        if (instance == null) {
            instance = new DesEncrypt(strKey);
        }
        return instance;
    }

    public static String newFileName;
    //  初始置换IP 64位
    private static final int[] INIT_REP_IP = {58, 50, 42, 34, 26, 18, 10, 2, 60, 52,
            44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48,
            40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35,
            27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31,
            23, 15, 7};
    // 初始逆置换IP 64位
    private static final int[] INIT_INVER_REP_IP = {40, 8, 48, 16, 56, 24, 64, 32, 39, 7,
            47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45,
            13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11,
            51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49,
            17, 57, 25};
    // 置换选择1，56位
    private static final int[] PC_1 = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50,
            42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44,
            36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6,
            61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
    //  置换选择2即压缩置换 48位
    private static final int[] PC_2 = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21,
            10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47,
            55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36,
            29, 32};
    //  扩展置换E  48位
    private static final int[] Ext_Per_E = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9,
            10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20,
            21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};
    //  P盒  32位
    private static final int[] P = {16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23,
            26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22,
            11, 4, 25};
    // S_Box
    private static final int[][][] S_Box = {//S-盒
            {// S_Box[1]
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}},
            { // S_Box[2]
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}},
            { // S_Box[3]
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}},
            { // S_Box[4]
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}},
            { // S_Box[5]
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}},
            { // S_Box[6]
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}},
            { // S_Box[7]
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}},
            { // S_Box[8]
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}}
    };
    public static final int[] LeftMove = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    //循环左移的轮数
    private static final int LEFT_MOVE_COUNT = 16;
    //加密的轮数
    private static final int ENCRY_COUNT = 16;
    //解密的论数
    private static final int DECIP_COUNT = 15;
    //cbc模式中的初始向量I CBCMODE_I[64]
    private static final byte[] CBCMODE_I = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] keydata = new int[64];// 二进制加密数据
    static int[] encryptdata = new int[64]; // 加密操作完成后的字节数组
    static byte[] EncryptCode = new byte[8];// 密钥初试化成二维数组
    static int[][] keyArray;
    // 循环移位操作函数
    static int[] c0 = new int[28];
    static int[] d0 = new int[28];
    static int[] c1 = new int[28];
    static int[] d1 = new int[28];

    static int[] L0 = new int[32];
    static int[] R0 = new int[32];
    static int[] L1 = new int[32];
    static int[] R1 = new int[32];
    static int[] RE = new int[48];
    static int[][] S = new int[8][6];
    static int[] sBoxData = new int[8];
    static int[] sValue = new int[32];
    static int[] RP = new int[32];

    /**
     * des 中加解密的核心算法
     *
     * @param des_data 加密的数据，也就是明文
     * @param flag     1就是加密，0就是解密
     * @return
     */
    public static byte[] DesEncrypt(byte[] des_data, int flag) {
        byte[] format_key = DataFormat(bytekey);
        byte[] format_data = DataFormat(des_data);
        int datalen = format_data.length;
        byte[] result_data = new byte[datalen];
        result_data = EcbModel(flag, format_key, format_data, datalen, result_data);
        // 当前为解密过程，去掉加密时产生的填充位
        byte[] decryptbytearray = null;
        if (flag == 0) {
            int delete_len = result_data[datalen - 8 - 1];
            delete_len = ((delete_len >= 1) && (delete_len <= 8)) ? delete_len : 0;
            decryptbytearray = new byte[datalen - delete_len - 8];
            System.arraycopy(result_data, 0, decryptbytearray, 0, datalen - delete_len - 8);
        }
        return (flag == 1) ? result_data : decryptbytearray;
    }

    /**
     * 如若需要则对秘钥进行扩展
     *
     * @param data
     * @return
     */
    public static byte[] DataFormat(byte[] data) {
        //0~7字节拓展为8字节，8~15字节拓展为16字节
        int len = data.length;
        int padlen = 8 - (len % 8);
        int newlen = len + padlen;
        byte[] newdata = new byte[newlen];
        System.arraycopy(data, 0, newdata, 0, len);//将data完整复制给newdata
        for (int i = len; i < newlen; i++)//拓展
            newdata[i] = (byte) padlen;
        return newdata;
    }

    /**
     * 使用ECB模式对明文进行加/解密
     *
     * @param flag
     * @param format_key
     * @param format_data
     * @param datalen
     * @param result_data
     */
    public static byte[] EcbModel(int flag, byte[] format_key, byte[] format_data, int datalen, byte[] result_data) {
        // 使用了电码本模式ECB,8个字节8个字节一加密
        int unitcount = datalen / 8;
        byte[] tmpkey = new byte[8];
        byte[] tmpdata = new byte[8];
        // 取格式化话后秘钥的前八个字节
        System.arraycopy(format_key, 0, tmpkey, 0, 8);
        for (int i = 0; i < unitcount; i++) {
            //每次取格式化后数据的8个字节
            System.arraycopy(format_data, i * 8, tmpdata, 0, 8);
            byte[] tmpresult = UnitDes(tmpkey, tmpdata, flag);
            System.arraycopy(tmpresult, 0, result_data, i * 8, 8);
        }

        return result_data;
    }

    /**
     * 每8个字节加解密
     *
     * @param des_key  秘钥
     * @param des_data 数据
     * @param flag     1加密，0解密
     * @return byte数组
     */
    public static byte[] UnitDes(byte[] des_key, byte[] des_data, int flag) {
        keydata = ReadDataToBirnaryIntArray(des_key);// 初试化密钥为二维密钥数组
        encryptdata = ReadDataToBirnaryIntArray(des_data);// 将加密数据字节数组转换成二进制字节数组
        if (keyArray == null) {
            keyArray = new int[16][48];
            keyArray = KeyInitialize(keydata, keyArray); // 生成16个48位的子秘钥
        }
        EncryptCode = Encrypt(encryptdata, flag, keyArray);
        return EncryptCode;
    }

    /**
     * 将数据转换成二进制的整形数组
     *
     * @param intdata
     * @return
     */
    public static int[] ReadDataToBirnaryIntArray(byte[] intdata) {
        int i;
        int j;
        int[] IntDa = new int[8];
        int[] IntVa = new int[64];
        // 将数据转换为二进制数，存储到数组
        for (i = 0; i < 8; i++) {
            IntDa[i] = intdata[i];
            if (IntDa[i] < 0) {
                IntDa[i] += 256;
                IntDa[i] %= 256;
            }
        }
        //除2的方式
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                IntVa[((i * 8) + 7) - j] = IntDa[i] % 2;
                IntDa[i] = IntDa[i] / 2;
            }
        }
        return IntVa;
    }

    static int[] K0 = new int[56];// 初始密钥记作Y0，它由64位01序列组成，但其中8位用于奇偶校验，所以实际用于加密的只有56位，记为K0

    /**
     * 通过此方法生成16个子秘钥
     *
     * @param key      初始64位二进制的秘钥
     * @param keyarray 存放每一轮的子秘钥
     */
    public static int[][] KeyInitialize(int[] key, int[][] keyarray) {
        int i;
        int j;
        for (i = 0; i < 56; i++) {
            K0[i] = key[PC_1[i] - 1]; // 密钥进行PC-1变换。置换表PC_1的第i位为n，就把数据表key的第n位（key[n-1]）放到这K0表的第i位
        }
        for (i = 0; i < LEFT_MOVE_COUNT; i++) {
            LeftBitMove(K0, LeftMove[i]); //Ci,Di循环左移得到Ci+1,Di+1，Ci+1Di+1合并做PC_2置换
            for (j = 0; j < 48; j++) {
                keyarray[i][j] = K0[PC_2[j] - 1]; // 生成子密钥keyarray[i][j]
            }
        }
        return keyarray;
    }

    /**
     * 循环左移
     *
     * @param k      通过置换函数PC-1置换后生成的56二进制位
     * @param offset 左移的位数
     */
    public static void LeftBitMove(int[] k, int offset) {
        int i;
        for (i = 0; i < 28; i++) {
            c0[i] = k[i];
            d0[i] = k[i + 28];
        }
        if (offset == 1) {
            for (i = 0; i < 27; i++) { // 循环左移一位
                c1[i] = c0[i + 1];
                d1[i] = d0[i + 1];
            }
            c1[27] = c0[0];
            d1[27] = d0[0];
        } else if (offset == 2) {
            for (i = 0; i < 26; i++) { // 循环左移两位
                c1[i] = c0[i + 2];
                d1[i] = d0[i + 2];
            }
            c1[26] = c0[0];
            d1[26] = d0[0];
            c1[27] = c0[1];
            d1[27] = d0[1];
        }
        for (i = 0; i < 28; i++) {
            k[i] = c1[i];
            k[i + 28] = d1[i];
        }
    }

    /**
     * 执行加密解密操作
     *
     * @param encryptdata  64位的明文
     * @param flag  1即为加密操作
     * @param keyarray  16轮子秘钥
     * @return
     */
    static byte[] encrypt = new byte[8];
    static int[] MIP_1 = new int[64];
    static int[] M = new int[64];

    public static byte[] Encrypt(int[] encryptdata, int flag, int[][] keyarray) {
        int i;
        // 通过初始置换函数对明文进行初始置换
        for (i = 0; i < 64; i++) {
            M[i] = encryptdata[INIT_REP_IP[i] - 1]; // 明文IP变换
        }
        if (flag == 1) { // 加密
            for (i = 0; i < ENCRY_COUNT; i++) {
                M = LoopF(M, i, flag, keyarray);
            }
        } else if (flag == 0) { // 解密
            for (i = DECIP_COUNT; i >= 0; i--) {
                M = LoopF(M, i, flag, keyarray);
            }
        }
        for (i = 0; i < 64; i++) {
            MIP_1[i] = M[INIT_INVER_REP_IP[i] - 1]; // 进行IP_1逆运算
        }
        encrypt = BirnaryIntArrayToInt(MIP_1);// 返回加密数据
        return encrypt;
    }

    /**
     * 循环迭代16轮
     *
     * @param M        明文
     * @param times    次数
     * @param flag     1为加密，0为解密
     * @param keyarray 每一轮的子秘钥
     */
    public static int[] LoopF(int[] M, int times, int flag, int[][] keyarray) {
        int i;
        int j;
        for (i = 0; i < 32; i++) {
            L0[i] = M[i]; // 明文左侧的初始化
            R0[i] = M[i + 32]; // 明文右侧的初始化
        }
        // R0经过扩展置换表E，由32位变为48位的RE，再由子秘钥Ki加密
        for (i = 0; i < 48; i++) {
            RE[i] = R0[Ext_Per_E[i] - 1];
            RE[i] = RE[i] + keyarray[times][i]; // 子秘钥Ki加密，与KeyArray[times][i]按位作不进位加法运算(异或运算)
            if (RE[i] == 2) {
                RE[i] = 0;
            }
        }
        //RE经过S盒压缩，变为32位的sValue
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 6; j++) {
                S[i][j] = RE[(i * 6) + j]; // 48位分成8组，每组6位
            }
            // 下面经过S盒，得到一个十进制数
            sBoxData[i] = S_Box[i][(S[i][0] << 1) + S[i][5]][(S[i][1] << 3) + (S[i][2] << 2) + (S[i][3] << 1) + S[i][4]];
            // S盒中取得的十进制数变为4位二进制
            for (j = 0; j < 4; j++) {
                sValue[((i * 4) + 3) - j] = sBoxData[i] % 2;
                sBoxData[i] = sBoxData[i] / 2;
            }
        }
        //sValue经过P变换变成32位的RP
        for (i = 0; i < 32; i++) {
            RP[i] = sValue[P[i] - 1]; // 经过P变换
            L1[i] = R0[i]; // 右边移到左边
            R1[i] = L0[i] + RP[i];//与L0与RP按位作不进位加法运算(异或运算)，得R1
            if (R1[i] == 2) {
                R1[i] = 0;
            }
            // 重新合成M，返回数组M
            // 最后一次变换时，左右不进行互换。此处采用两次变换实现不变
            if (((flag == 0) && (times == 0)) || ((flag == 1) && (times == 15))) {
                M[i] = R1[i];
                M[i + 32] = L1[i];
            } else {
                M[i] = L1[i];
                M[i + 32] = R1[i];
            }
        }
        return M;
    }

    /**
     * 将二进制转为十进制的byte类型
     *
     * @param data
     * @return
     */
    public static byte[] BirnaryIntArrayToInt(int[] data) {
        int i;
        int j;
        byte[] value = new byte[8];
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                value[i] += (data[(i << 3) + j] << (7 - j));
            }
        }
        for (i = 0; i < 8; i++) {
            value[i] %= 256;
            if (value[i] > 128) {
                value[i] -= 255;
            }
        }
        return value;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void generateFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据路径获取文件的byte数组
     *
     * @param path
     * @return
     */
    public static byte[] getFileByte(String path) {
        FileChannel fc = null;
        byte[] result = null;
        try {
            fc = new RandomAccessFile(path, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0,
                    fc.size()).load();
            result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void readfile(String filepath) throws IOException {
        Scanner in = new Scanner(System.in);
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                byte[] fileByte = getFileByte(file.getAbsolutePath());
                int[] fileByteToBirnary = ReadDataToBirnaryIntArray(fileByte);//字节流转换成二进制数组
                // 加密
                byte[] result = DesEncrypt(fileByte, 1);
                generateFile(result, "F:\\Desktop\\物联网安全\\密文", file.getName() + "的密文");
                // 解密
                byte[] tempresult = DesEncrypt(result, 0);
                generateFile(tempresult, "F:\\Desktop\\物联网安全\\解密", file.getName());
            } else if (file.isDirectory()) {
                String path1;//密文路径
                String path2;//解密路径
                path1 = filepath.replace("F:\\Desktop\\物联网安全\\明文", "F:\\Desktop\\物联网安全\\密文");
                path2 = filepath.replace("F:\\Desktop\\物联网安全\\明文", "F:\\Desktop\\物联网安全\\解密");
                File dir = new File(path1);
                if (!dir.exists()) {//判断文件目录是否存在
                    dir.mkdirs();
                }
                File dir2 = new File(path2);
                if (!dir2.exists()) {//判断文件目录是否存在
                    dir2.mkdirs();
                }
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        byte[] fileByte = getFileByte(readfile.getAbsolutePath());
                        // 加密
                        byte[] result = DesEncrypt(fileByte, 1);
                        generateFile(result, path1, "密文_" + readfile.getName());
                        // 解密
                        byte[] tempresult = DesEncrypt(result, 0);
                        generateFile(tempresult, path2, readfile.getName());
                    } else if (readfile.isDirectory()) {
                        readfile(filepath + "\\" + filelist[i]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("readfile() Exception:" + e.getMessage());
        }
    }

    public static String Encrypt(String strText , String strKey, int flag){
        getInstance(strKey);
        if(flag == 1){
            byte byteIn[] = strText.getBytes();
            byte byteOut[] = DesEncrypt(byteIn, flag);

            return Base64.getEncoder().encodeToString(byteOut);
        }else{
            byte byteIn[] = Base64.getDecoder().decode(strText);
            byte byteOut[] = DesEncrypt(byteIn, flag);

            return new String(byteOut);
        }

    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("请输入秘钥:");
        String key = in.nextLine();
        DesEncrypt desUtil = DesEncrypt.getInstance(key);//秘钥变成二进制
        try {
            readfile("F:\\Desktop\\物联网安全\\明文");
        } catch (FileNotFoundException e) {
            System.out.println("readfile() Exception:" + e.getMessage());
        }
        System.out.println("加解密成功，请到相应目录下查看");
    }
}