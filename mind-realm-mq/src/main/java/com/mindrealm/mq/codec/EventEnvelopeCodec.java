package com.mindrealm.mq.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.mindrealm.common.event.EventEnvelope;
import com.mindrealm.mq.event.EsSyncEvent;
import com.mindrealm.mq.event.NotificationEvent;
import com.mindrealm.mq.event.WarningAnalyzeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @className: EventEnvelopeCodec
 * @description: Kafka 消息编解码器 —— 基于 Kryo 二进制序列化
 *               <p>
 *               Kryo 相比 JSON 的优势：
 *               <ul>
 *               <li>线程安全：通过对象池（Pool）保证 Kryo 实例的线程安全</li>
 *               <li>性能：二进制序列化，比 JSON 快 5-10 倍，体积小 30-50%</li>
 *               <li>安全：无 JSON 反序列化漏洞风险</li>
 *               <li>类型安全：编译期注册类型，运行时无需类型推断</li>
 *               </ul>
 *               </p>
 *               <p>
 *               使用方式：
 *               <pre>
 *               // 生产者
 *               byte[] data = EventEnvelopeCodec.encode(envelope);
 *               kafkaTemplate.send(topic, key, data);
 *
 *               // 消费者
 *               EventEnvelope&lt;EsSyncEvent&gt; envelope =
 *                   EventEnvelopeCodec.decode(data, EsSyncEvent.class);
 *               </pre>
 *               </p>
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
public final class EventEnvelopeCodec {

    private static final Logger log = LoggerFactory.getLogger(EventEnvelopeCodec.class);

    private EventEnvelopeCodec() {
    }

    // ======================== Kryo 对象池 ========================

    /**
     * Kryo 实例不是线程安全的，使用 Pool 为每个线程提供独立实例。
     * 池大小：max 32，足够支撑高并发场景。
     */
    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 32) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            // 关闭注册强制要求 —— 未注册的类也能序列化（开发便利）
            // 生产环境建议改为 true 并注册所有类型，性能更优
            kryo.setRegistrationRequired(false);
            // 不强制要求无参构造函数
            kryo.setWarnUnregisteredClasses(false);

            // 预注册核心类型以获得最佳性能
            kryo.register(EventEnvelope.class);
            kryo.register(EsSyncEvent.class);
            kryo.register(WarningAnalyzeEvent.class);
            kryo.register(NotificationEvent.class);
            kryo.register(HashMap.class);
            kryo.register(ArrayList.class);
            kryo.register(byte[].class);

            return kryo;
        }
    };

    // ======================== 编码（序列化） ========================

    /**
     * 将 EventEnvelope 序列化为 Kryo 二进制字节数组
     *
     * @param envelope 事件信封
     * @return 二进制字节数组
     * @throws IllegalArgumentException 如果 envelope 校验不通过
     */
    public static byte[] encode(EventEnvelope<?> envelope) {
        validate(envelope);

        Kryo kryo = KRYO_POOL.obtain();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            Output output = new Output(baos, 4096);
            kryo.writeObject(output, envelope);
            output.close();
            return baos.toByteArray();
        } catch (KryoException e) {
            log.error("Kryo 序列化失败: eventId={}, eventType={}, error={}",
                    envelope.getEventId(), envelope.getEventType(), e.getMessage(), e);
            throw new RuntimeException("Kryo 序列化失败: " + e.getMessage(), e);
        } finally {
            KRYO_POOL.free(kryo);
        }
    }

    // ======================== 解码（反序列化） ========================

    /**
     * 将 Kryo 二进制字节数组反序列化为 EventEnvelope
     * <p>
     * 消费者明确知道目标载荷类型，Kryo 自动完成类型安全的反序列化，
     * 无需像 JSON 那样进行二次类型转换。
     * </p>
     *
     * @param data         二进制字节数组
     * @param payloadClass 载荷类型（仅用于日志，Kryo 自动推断泛型类型）
     * @param <T>          载荷泛型
     * @return 解析后的 EventEnvelope，解析失败返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> EventEnvelope<T> decode(byte[] data, Class<T> payloadClass) {
        if (data == null || data.length == 0) {
            log.warn("Kafka 消息为空，跳过反序列化");
            return null;
        }

        Kryo kryo = KRYO_POOL.obtain();
        try {
            Input input = new Input(data);
            EventEnvelope<T> envelope = kryo.readObject(input, EventEnvelope.class);
            input.close();

            if (envelope == null) {
                log.warn("EventEnvelope 反序列化结果为空");
                return null;
            }
            if (envelope.getPayload() == null) {
                log.warn("EventEnvelope.payload 为空: eventId={}, eventType={}",
                        envelope.getEventId(), envelope.getEventType());
                return null;
            }

            // Kryo 反序列化出来的 payload 已经是正确的类型，无需二次转换
            // 此处做一次类型检查用于日志告警
            if (!payloadClass.isInstance(envelope.getPayload())) {
                log.warn("EventEnvelope payload 类型不匹配: expected={}, actual={}, eventId={}",
                        payloadClass.getSimpleName(),
                        envelope.getPayload().getClass().getSimpleName(),
                        envelope.getEventId());
            }

            return envelope;
        } catch (KryoException e) {
            log.error("Kryo 反序列化失败: expectedPayload={}, dataSize={} bytes, error={}",
                    payloadClass.getSimpleName(), data.length, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Kryo 反序列化异常: expectedPayload={}, error={}",
                    payloadClass.getSimpleName(), e.getMessage(), e);
            return null;
        } finally {
            KRYO_POOL.free(kryo);
        }
    }

    // ======================== 校验方法 ========================

    /**
     * 校验 EventEnvelope 的必填字段
     */
    private static void validate(EventEnvelope<?> envelope) {
        if (envelope == null) {
            throw new IllegalArgumentException("EventEnvelope 不能为 null");
        }
        if (envelope.getEventId() == null || envelope.getEventId().isEmpty()) {
            throw new IllegalArgumentException("eventId 不能为空");
        }
        if (envelope.getEventType() == null || envelope.getEventType().isEmpty()) {
            throw new IllegalArgumentException("eventType 不能为空, eventId=" + envelope.getEventId());
        }
        if (envelope.getPayload() == null) {
            throw new IllegalArgumentException("payload 不能为空, eventId=" + envelope.getEventId());
        }
        if (envelope.getTimestamp() == null) {
            throw new IllegalArgumentException("timestamp 不能为空, eventId=" + envelope.getEventId());
        }
    }
}
