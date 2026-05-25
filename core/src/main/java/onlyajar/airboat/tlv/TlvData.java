package onlyajar.airboat.tlv;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.*;

import onlyajar.airboat.tlv.protocol.TlvProtocol;

/**
 * tlv data
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
        this.children = List.copyOf(children);
        this.value = null;
    }

    public byte[] getTag() {
        return tag;
    }

    public byte[] getValue() {
        return value;
    }

    public List<TlvData> getChildren() {
        return children;
    }

    public boolean isConstructed() {
        return !children.isEmpty() || (tag.length > 0 && (tag[0] & 0x20) != 0);
    }

    /**
     * 十六进制标签字符串，如 "9F26"
     */
    public String getTagHex() {
        return HexUtils.bytesToHex(tag);
    }

    /**
     * 十六进制值字符串
     */
    public String getValueHex() {
        return value != null ? HexUtils.bytesToHex(value) : "";
    }

    /**
     * 在子节点中按标签查找（递归深度优先）
     */
    public TlvData find(byte[] searchTag) {
        for (TlvData child : children) {
            if (Arrays.equals(child.tag, searchTag)) return child;
            TlvData found = child.find(searchTag);
            if (found != null) return found;
        }
        return null;
    }

    /**
     * 获取原始字节长度
     */
    public int getTotalLength(TlvProtocol tlvProtocol) {
        if (isConstructed() && value == null) {
            // tag + length + sum(children encoded)
            int contentLen = 0;
            for (TlvData c : children) contentLen += c.getTotalLength(tlvProtocol);
            return tag.length + tlvProtocol.encodeLength(contentLen).length + contentLen;
        }
        return tag.length + tlvProtocol.encodeLength(value.length).length + value.length;
    }


    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        if (isConstructed()) {
            return String.format("[%s] (constructed, %d children)", getTagHex(), children.size());
        }
        return String.format("[%s] = %s", getTagHex(), getValueHex());
    }

    /**
     *
     */
    @SuppressLint("DefaultLocale")
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

