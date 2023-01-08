package com.zhenlong.common.constant;

public class WareConstant {
    public enum PurchaseStatusEnum {
        CREATED(0, "created"), ASSIGNED(1, "assigned"), RECEIVED(2, "received"), FINISHED(3, "finished"), ERROR(4, "error");
        private int code;
        private String msg;

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum PurchaseDetailStatusEnum {
        CREATED(0, "created"), ASSIGNED(1, "assigned"), PURCHASING(2, "purchasing"), FINISHED(3, "finished"), FAILED(4, "failed");
        private int code;
        private String msg;

        PurchaseDetailStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
