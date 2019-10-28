package com.efl.server.agreement;

public class EflMessage {
    /**
     * 消息头标志
     */
    private int header = ConstantValue.HEAD_DATA;

    /**
     * 消息长度
     */
    private int contentLength;

    /**
     * 消息类型
     */
    private int type;

    /**
     * 消息内容
     */
    private byte[] content;

    public EflMessage(int contentLength, int type,byte[] content) {
        this.contentLength = contentLength+4;
        this.type=type;
        this.content = content;
    }

    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
