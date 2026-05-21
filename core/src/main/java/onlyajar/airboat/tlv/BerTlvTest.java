package onlyajar.airboat.tlv;

import java.util.Arrays;
import java.util.List;

public class BerTlvTest {

    public static void main(String[] args) {
        System.out.println("========== 1. 组包测试 ==========\n");

        // 构造一段典型 EMV 数据
        byte[] data = new BerTlvBuilder()
                .addPrimitive("9F26", "AABBCCDD11223344")   // Application Cryptogram
                .addPrimitive("9F27", "80")                   // CID
                .addPrimitive("9F10", "0102030405060708")     // Issuer Application Data
                .addPrimitive("9F37", "11223344")             // Unpredictable Number
                .addPrimitive("82",   "1980")                 // AIP
                .addPrimitive("94",   "08010100")             // AFL (多字节 tag 测试)
                .addConstructed("70", b -> b                  // EMV Template
                        .addPrimitive("57", "4761739001010010D25122011234567890")
                        .addPrimitive("5A", "4761739001010010")
                        .addConstructed("70", b2 -> b2            // 嵌套 Template
                                .addPrimitive("9F6C", "00")
                        )
                )
                .build();

        System.out.println("编码结果: " + TlvData.bytesToHex(data));

        System.out.println("\n========== 2. 解析测试 ==========\n");

        List<TlvData> tlvs = BerTlvParser.parse(data);
        for (TlvData tlv : tlvs) {
            System.out.print(tlv.toPrettyString(0));
        }

        System.out.println("========== 3. 查找测试 ==========\n");

        // 按标签查找
        TlvData found = BerTlvParser.findByTagHex(data, "9F26");
        if (found != null) {
            System.out.println("找到 9F26 = " + found.getValueHex());
        }

        // 嵌套查找
        TlvData nested = BerTlvParser.findByTagHex(data, "9F6C");
        if (nested != null) {
            System.out.println("找到 9F6C (嵌套) = " + nested.getValueHex());
        }

        System.out.println("\n========== 4. 长度编码边界测试 ==========\n");

        // 测试不同长度的编码
        int[] testLengths = {0, 1, 127, 128, 255, 256, 65535, 65536};
        for (int len : testLengths) {
            byte[] encoded = BerTlvUtil.encodeLengthBytes(len);
            System.out.printf("长度 %6d → %s%n", len, TlvData.bytesToHex(encoded));
        }

        System.out.println("\n========== 5. 往返一致性测试 ==========\n");

        // 编码 → 解析 → 重新编码，结果应完全一致
        byte[] roundTrip = new BerTlvBuilder()
                .addPrimitive("5F2A", "0156")                 // Transaction Currency Code
                .addPrimitive("9A",   "230519")               // Transaction Date
                .addPrimitive("9C",   "00")                   // Transaction Type
                .addConstructed("77", b -> b                  // Response Template
                        .addPrimitive("82", "1980")
                        .addPrimitive("94", "08010100100101011801020020010201")
                )
                .build();

        List<TlvData> parsed = BerTlvParser.parse(roundTrip);
        BerTlvBuilder reBuilder = new BerTlvBuilder();
        for (TlvData tlv : parsed) {
            reBuilder.add(tlv);
        }
        byte[] rebuilt = reBuilder.build();

        System.out.println("原始编码: " + TlvData.bytesToHex(roundTrip));
        System.out.println("重建编码: " + TlvData.bytesToHex(rebuilt));
        System.out.println("一致性:   " + Arrays.equals(roundTrip, rebuilt));
    }
}

