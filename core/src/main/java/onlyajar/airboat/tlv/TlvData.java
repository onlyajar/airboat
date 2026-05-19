package onlyajar.airboat.tlv;
import java.util.*;

/**
 * ┌──────────┬───────────────────────────────────────────┐
 * │  字段     │  编码规则                                 │
 * ├──────────┼───────────────────────────────────────────┤
 * │  Tag     │  1字节: b5~b1 ≠ 11111                     │
 * │          │  多字节: 首字节 b5~b1 = 11111,             │
 * │          │  后续字节 b7=1继续, b7=0结束              │
 * ├──────────┼───────────────────────────────────────────┤
 * │  Length  │  b7=0: 短格式, 值 = b6~b0   (0~127)      │
 * │          │  b7=1: 长格式, b6~b0 = 后续字节数         │
 * │          │   0x81+1字节 → 128~255                    │
 * │          │   0x82+2字节 → 256~65535                  │
 * │          │   0x83+3字节 → 65536~16777215             │
 * ├──────────┼───────────────────────────────────────────┤
 * │  Value   │  基本编码 (Primitive): 原始数据           │
 * │          │  构造编码 (Constructed): 嵌套的 TLV 序列  │
 * ├──────────┼───────────────────────────────────────────┤
 * │  Tag 判断│  b6 = 0 → Primitive, b6 = 1 → Constructed│
 * └──────────┴───────────────────────────────────────────┘
 * 表示一个 BER-TLV 数据对象
 */
public class TlvData {

    private final byte[] tag;
    private final byte[] value;
    private final List<TlvData> children; // 用于构造结构（Template）

    // 叶子节点构造
    public TlvData(byte[] tag, byte[] value) {
        this.tag = Objects.requireNonNull(tag);
        this.value = Objects.requireNonNull(value);
        this.children = Collections.emptyList();
    }

    // 构造节点（含子节点）
    public TlvData(byte[] tag, List<TlvData> children) {
        this.tag = Objects.requireNonNull(tag);
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
        this.value = null;
    }

    public byte[] getTag()   { return tag; }
    public byte[] getValue() { return value; }
    public List<TlvData> getChildren() { return children; }

    public boolean isConstructed() {
        return !children.isEmpty() || (tag.length > 0 && (tag[0] & 0x20) != 0);
    }

    /** 十六进制标签字符串，如 "9F26" */
    public String getTagHex() {
        return bytesToHex(tag);
    }

    /** 十六进制值字符串 */
    public String getValueHex() {
        return value != null ? bytesToHex(value) : "";
    }

    /** 在子节点中按标签查找（递归深度优先） */
    public TlvData find(byte[] searchTag) {
        for (TlvData child : children) {
            if (Arrays.equals(child.tag, searchTag)) return child;
            TlvData found = child.find(searchTag);
            if (found != null) return found;
        }
        return null;
    }

    /** 获取原始字节长度 */
    public int getTotalLength() {
        if (isConstructed() && value == null) {
            // tag + length + sum(children encoded)
            int contentLen = 0;
            for (TlvData c : children) contentLen += c.getTotalLength();
            return tag.length + BerTlvUtil.encodeLengthBytes(contentLen).length + contentLen;
        }
        return tag.length + BerTlvUtil.encodeLengthBytes(value.length).length + value.length;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    @Override
    public String toString() {
        if (isConstructed()) {
            return String.format("[%s] (constructed, %d children)", getTagHex(), children.size());
        }
        return String.format("[%s] = %s", getTagHex(), getValueHex());
    }

    /**
     * 以树形结构打印（便于调试）
     */
    public String toPrettyString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) sb.append("  ");
        if (isConstructed()) {
            sb.append(String.format("[%s] Constructed (%d children)\n", getTagHex(), children.size()));
            for (TlvData child : children) {
                sb.append(child.toPrettyString(indent + 1));
            }
        } else {
            sb.append(String.format("[%s] = %s\n", getTagHex(), getValueHex()));
        }
        return sb.toString();
    }
}

