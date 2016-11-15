package com.devsai.ctrl;

import java.util.List;
import java.util.Map;

/**
 * Created by huangxiangsai on 16/5/21.
 */
public class CommonResult {

    private int code ;
    private String message;
    private List<Map<String,?>> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Map<String, ?>> getData() {
        return data;
    }

    public void setData(List<Map<String, ?>> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonResult that = (CommonResult) o;

        if (code != that.code) return false;
        if (!message.equals(that.message)) return false;
        return !(data != null ? !data.equals(that.data) : that.data != null);

    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + message.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
