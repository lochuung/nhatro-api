package vn.huuloc.boardinghouse.enums.sort.filter;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static vn.huuloc.boardinghouse.constant.ValueConstants.DEFAULT_DATE_TIME_FORMAT;

@Slf4j
public enum FieldType {

    BOOLEAN {
        public Object parse(String value) {
            return Boolean.valueOf(value);
        }
    },

    CHAR {
        public Object parse(String value) {
            return value.charAt(0);
        }
    },

    DATE {
        public Object parse(String value) {
            Object date = null;
            try {
                date = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
            } catch (Exception e) {
                log.info("Failed parse field type DATE {}", e.getMessage());
            }

            return date;
        }
    },

    DOUBLE {
        public Object parse(String value) {
            return Double.valueOf(value);
        }
    },

    INTEGER {
        public Object parse(String value) {
            return Integer.valueOf(value);
        }
    },

    LONG {
        public Object parse(String value) {
            return Long.valueOf(value);
        }
    },

    BIG_DECIMAL {
        public Object parse(String value) {
            return new BigDecimal(value);
        }
    },

    STRING {
        public Object parse(String value) {
            return value;
        }
    };

    public abstract Object parse(String value);

}