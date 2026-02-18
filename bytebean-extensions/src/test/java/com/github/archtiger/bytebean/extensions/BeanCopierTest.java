package com.github.archtiger.bytebean.extensions;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BeanCopierTest {

    // ==================== bean -> bean tests ====================

    @Test
    void beanToBean_shouldCopyOnlyMatchedNonNullFields() {
        final SourceBean origin = new SourceBean();
        origin.setName("alice");
        origin.setAge(null);
        origin.setEnabled(true);

        final TargetBean target = new TargetBean();
        target.setName("old-name");
        target.setAge(18);
        target.setEnabled(false);
        target.setNote("keep-note");

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("alice", copied.getName());
        assertEquals(18, copied.getAge());
        assertEquals(true, copied.getEnabled());
        assertEquals("keep-note", copied.getNote());
    }

    @Test
    void beanToBean_shouldSkipNullSourceFields() {
        final SourceBean origin = new SourceBean();
        origin.setName(null);
        origin.setAge(null);
        origin.setEnabled(true);

        final TargetBean target = new TargetBean();
        target.setName("keep-name");
        target.setAge(20);
        target.setEnabled(false);
        target.setNote("keep-note");

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("keep-name", copied.getName());
        assertEquals(20, copied.getAge());
        assertEquals(true, copied.getEnabled());
        assertEquals("keep-note", copied.getNote());
    }

    @Test
    void beanToBean_shouldHandleAllFieldsNull() {
        final SourceBean origin = new SourceBean();
        origin.setName(null);
        origin.setAge(null);
        origin.setEnabled(null);

        final TargetBean target = new TargetBean();
        target.setName("keep-name");
        target.setAge(20);
        target.setEnabled(true);
        target.setNote("keep-note");

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("keep-name", copied.getName());
        assertEquals(20, copied.getAge());
        assertEquals(true, copied.getEnabled());
        assertEquals("keep-note", copied.getNote());
    }

    @Test
    void beanToBean_shouldReturnSameTargetInstance() {
        final SourceBean origin = new SourceBean();
        origin.setName("alice");

        final TargetBean target = new TargetBean();
        target.setName("old-name");

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
    }

    @Test
    void beanToBean_shouldCopyBooleanFieldWithIsGetter() {
        final SourceBeanWithIsGetter origin = new SourceBeanWithIsGetter();
        origin.setName("alice");
        origin.setEnabled(true);
        origin.setActive(false);

        final TargetBeanWithIsGetter target = new TargetBeanWithIsGetter();
        target.setName("old-name");
        target.setEnabled(false);
        target.setActive(true);

        final TargetBeanWithIsGetter copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("alice", copied.getName());
        assertTrue(copied.isEnabled());
        assertFalse(copied.isActive());
    }

    @Test
    void beanToBean_shouldSkipTypeMismatchFields() {
        final SourceBeanWithTypeMismatch origin = new SourceBeanWithTypeMismatch();
        origin.setName("alice");
        origin.setValue("100");  // String类型

        final TargetBeanWithTypeMismatch target = new TargetBeanWithTypeMismatch();
        target.setName("old-name");
        target.setValue(50);  // Integer类型

        final TargetBeanWithTypeMismatch copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("alice", copied.getName());
        assertEquals(50, copied.getValue());  // 类型不匹配，保持原值
    }

    @Test
    void beanToBean_shouldHandleComplexTypes() {
        final SourceBeanWithComplexTypes origin = new SourceBeanWithComplexTypes();
        origin.setName("alice");
        origin.setTags(List.of("tag1", "tag2"));
        origin.setMetadata(Map.of("key1", "value1"));

        final TargetBeanWithComplexTypes target = new TargetBeanWithComplexTypes();
        target.setName("old-name");
        target.setTags(List.of("old-tag"));
        target.setMetadata(Map.of("old-key", "old-value"));

        final TargetBeanWithComplexTypes copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("alice", copied.getName());
        assertEquals(List.of("tag1", "tag2"), copied.getTags());
        assertEquals(Map.of("key1", "value1"), copied.getMetadata());
    }

    // ==================== record -> bean tests ====================

    @Test
    void recordToBean_shouldCopyOnlyMatchedNonNullFields() {
        final SourceRecord origin = new SourceRecord("bob", null, true, "ignored");

        final TargetBean target = new TargetBean();
        target.setName("old-name");
        target.setAge(20);
        target.setEnabled(false);
        target.setNote("keep-note");

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("bob", copied.getName());
        assertEquals(20, copied.getAge());
        assertEquals(true, copied.getEnabled());
        assertEquals("keep-note", copied.getNote());
    }

    @Test
    void recordToBean_shouldCopyAllNonNullFields() {
        final SourceRecord origin = new SourceRecord("alice", 25, true, "extra");

        final TargetBean target = new TargetBean();
        target.setName("old-name");
        target.setAge(30);
        target.setEnabled(false);

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
        assertEquals("alice", copied.getName());
        assertEquals(25, copied.getAge());
        assertTrue(copied.getEnabled());
    }

    @Test
    void recordToBean_shouldReturnSameTargetInstance() {
        final SourceRecord origin = new SourceRecord("alice", 25, true, "extra");

        final TargetBean target = new TargetBean();
        target.setName("old-name");

        final TargetBean copied = BeanCopier.copy(origin, target);

        assertSame(target, copied);
    }

    // ==================== bean -> record tests ====================

    @Test
    void beanToRecord_shouldKeepTargetValueWhenSourceIsNullOrMissing() {
        final SourceBeanForRecord origin = new SourceBeanForRecord();
        origin.setName("new-name");
        origin.setAge(null);
        // city 字段在来源对象中不存在，来源 getterIndex = INVALID_INDEX，参数会被设置为 null

        final TargetRecord target = new TargetRecord("old-name", 30, "shanghai");

        final TargetRecord copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertEquals("new-name", copied.name());
        // 来源 age 为 null，目标 age 会被设置为 null
        assertNull(copied.age());
        // city 字段在来源对象中不存在，也会被设置为 null
        assertNull(copied.city());
    }

    @Test
    void beanToRecord_shouldCopyAllNonNullFieldsToNewRecord() {
        final SourceBeanForRecord origin = new SourceBeanForRecord();
        origin.setName("new-name");
        origin.setAge(25);

        final TargetRecord2 target = new TargetRecord2("old-name", 30, "shanghai");

        final TargetRecord2 copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertEquals("new-name", copied.name());
        assertEquals(25, copied.age());
        // 没有来源字段，会被设置为 null
        assertNull(copied.city());
    }

    @Test
    void beanToRecord_shouldCreateNewInstance() {
        final SourceBeanForRecord origin = new SourceBeanForRecord();
        origin.setName("new-name");
        origin.setAge(25);

        final TargetRecord3 target = new TargetRecord3("old-name", 30, "shanghai");

        final TargetRecord3 copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertNotNull(copied);
    }

    @Test
    void beanToRecord_shouldHandleAllSourceFieldsNull() {
        final SourceBeanForRecord origin = new SourceBeanForRecord();
        origin.setName(null);
        origin.setAge(null);

        final TargetRecord4 target = new TargetRecord4("old-name", 30, "shanghai");

        final TargetRecord4 copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertNull(copied.name());
        assertNull(copied.age());
        // city 字段在来源对象中不存在，会被设置为 null
        assertNull(copied.city());
    }

    // ==================== record -> record tests ====================

    @Test
    void recordToRecord_shouldKeepTargetValueWhenSourceIsNullOrMissing() {
        final SourceRecordForRecord origin = new SourceRecordForRecord("new-name", null);
        final TargetRecord5 target = new TargetRecord5("old-name", 40, "beijing");

        final TargetRecord5 copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertEquals("new-name", copied.name());
        // age 为 null，来源也是 null，所以结果也是 null
        assertNull(copied.age());
        // city 字段在来源 record 中不存在，会被设置为 null
        assertNull(copied.city());
    }

    @Test
    void recordToRecord_shouldCreateNewInstance() {
        final SourceRecordForRecord2 origin = new SourceRecordForRecord2("new-name", 30);
        final TargetRecord6 target = new TargetRecord6("old-name", 40, "beijing");

        final TargetRecord6 copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertNotSame(origin, copied);
        assertEquals("new-name", copied.name());
        assertEquals(30, copied.age());
        // city 字段在来源 record 中不存在，会被设置为 null
        assertNull(copied.city());
    }

    @Test
    void recordToRecord_shouldHandleAllSourceFieldsNull() {
        final SourceRecordForRecord3 origin = new SourceRecordForRecord3(null, null);
        final TargetRecord target = new TargetRecord("old-name", 40, "beijing");

        final TargetRecord copied = BeanCopier.copy(origin, target);

        assertNotSame(target, copied);
        assertNull(copied.name());
        assertNull(copied.age());
        // city 字段在来源 record 中不存在，会被设置为 null
        assertNull(copied.city());
    }

    // ==================== Test data classes ====================

    static class SourceBean {
        private String name;
        private Integer age;
        private Boolean enabled;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    static class SourceBeanForRecord {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    static class TargetBean {
        private String name;
        private Integer age;
        private Boolean enabled;
        private String note;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public record SourceRecord(String name, Integer age, Boolean enabled, String extra) {
    }

    public record SourceRecordForRecord(String name, Integer age) {
    }

    public record SourceRecordForRecord2(String name, Integer age) {
    }

    public record SourceRecordForRecord3(String name, Integer age) {
    }

    public record TargetRecord(String name, Integer age, String city) {
    }

    public record TargetRecord2(String name, Integer age, String city) {
    }

    public record TargetRecord3(String name, Integer age, String city) {
    }

    public record TargetRecord4(String name, Integer age, String city) {
    }

    public record TargetRecord5(String name, Integer age, String city) {
    }

    public record TargetRecord6(String name, Integer age, String city) {
    }

    static class SourceBeanWithIsGetter {
        private String name;
        private boolean enabled;
        private boolean active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    static class TargetBeanWithIsGetter {
        private String name;
        private boolean enabled;
        private boolean active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    static class SourceBeanWithTypeMismatch {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class TargetBeanWithTypeMismatch {
        private String name;
        private Integer value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    static class SourceBeanWithComplexTypes {
        private String name;
        private List<String> tags;
        private Map<String, String> metadata;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }
    }

    static class TargetBeanWithComplexTypes {
        private String name;
        private List<String> tags;
        private Map<String, String> metadata;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }
    }
}
