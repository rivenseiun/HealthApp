package com.hubu.fan.exception;

/**
 * Created by 47462 on 2016/10/19.
 */
public class ContextIsNullException extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public ContextIsNullException() {
            this("Context is not Null");
        }

        public ContextIsNullException(String detailMessage) {
            this(detailMessage,null);
        }

        public ContextIsNullException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ContextIsNullException(Throwable throwable) {
            this("Context is not Null",throwable);
        }
}
