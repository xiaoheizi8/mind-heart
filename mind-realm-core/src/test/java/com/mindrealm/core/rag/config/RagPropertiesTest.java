package com.mindrealm.core.rag.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RagPropertiesTest {

    @Test
    void testDefaultValues() {
        RagProperties properties = new RagProperties();

        assertEquals(RagProperties.Mode.LOCAL_MEMORY, properties.getMode());
        assertNotNull(properties.getBailian());
        assertNotNull(properties.getLocalMemory());
        assertNotNull(properties.getMilvus());
    }

    @Test
    void testBailianConfigDefaults() {
        RagProperties.BailianConfig config = new RagProperties.BailianConfig();

        assertFalse(config.isEnabled());
        assertEquals("qwen-plus", config.getModel());
        assertEquals(0.9f, config.getTemperature());
        assertEquals(4000, config.getMaxTokens());
        assertTrue(config.isUseKnowledgeBase());
    }

    @Test
    void testLocalMemoryConfigDefaults() {
        RagProperties.LocalMemoryConfig config = new RagProperties.LocalMemoryConfig();

        assertTrue(config.isEnabled());
    }

    @Test
    void testMilvusConfigDefaults() {
        RagProperties.MilvusConfig config = new RagProperties.MilvusConfig();

        assertFalse(config.isEnabled());
        assertEquals("localhost", config.getHost());
        assertEquals(19530, config.getPort());
        assertEquals("mind_realm_knowledge", config.getCollection());
        assertEquals(1536, config.getDimension());
    }

    @Test
    void testSettersAndGetters() {
        RagProperties properties = new RagProperties();

        properties.setMode(RagProperties.Mode.BAILIAN_KNOWLEDGE);
        assertEquals(RagProperties.Mode.BAILIAN_KNOWLEDGE, properties.getMode());

        RagProperties.BailianConfig bailian = new RagProperties.BailianConfig();
        bailian.setEnabled(true);
        bailian.setApiKey("test-key");
        bailian.setKnowledgeBaseId("kb-123");
        properties.setBailian(bailian);

        assertTrue(properties.getBailian().isEnabled());
        assertEquals("test-key", properties.getBailian().getApiKey());
        assertEquals("kb-123", properties.getBailian().getKnowledgeBaseId());
    }

    @Test
    void testModeEnum() {
        assertEquals(5, RagProperties.Mode.values().length);
        
        assertEquals(RagProperties.Mode.BAILIAN_KNOWLEDGE, RagProperties.Mode.valueOf("BAILIAN_KNOWLEDGE"));
        assertEquals(RagProperties.Mode.LOCAL_MEMORY, RagProperties.Mode.valueOf("LOCAL_MEMORY"));
        assertEquals(RagProperties.Mode.MILVUS_VECTOR, RagProperties.Mode.valueOf("MILVUS_VECTOR"));
        assertEquals(RagProperties.Mode.DEEPSEEK, RagProperties.Mode.valueOf("DEEPSEEK"));
        assertEquals(RagProperties.Mode.HYBRID, RagProperties.Mode.valueOf("HYBRID"));
    }
}
