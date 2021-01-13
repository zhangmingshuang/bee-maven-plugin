package bee.com.nascent.maven.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通用构建器
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings("MethodWithTooManyParameters")
public class PojoBuilder<T> {

  private final Supplier<T> instantiator;

  private final List<Consumer<T>> modifiers = new ArrayList<>(8);

  public PojoBuilder(Supplier<T> instant) {
    this.instantiator = instant;
  }

  public static <T> PojoBuilder<T> of(Supplier<T> instant) {
    return new PojoBuilder<>(instant);
  }

  public PojoBuilder<T> with(Consumer<T> consumer) {
    consumer.accept(this.instantiator.get());
    return this;
  }

  public <P1> PojoBuilder<T> with(Consumer1<T, P1> consumer, P1 p1) {
    Consumer<T> c = instance -> consumer.accept(instance, p1);
    this.modifiers.add(c);
    return this;
  }

  public <P1, P2> PojoBuilder<T> with(Consumer2<T, P1, P2> consumer, P1 p1, P2 p2) {
    Consumer<T> c = instance -> consumer.accept(instance, p1, p2);
    this.modifiers.add(c);
    return this;
  }

  public <P1, P2, P3> PojoBuilder<T> with(Consumer3<T, P1, P2, P3> consumer, P1 p1, P2 p2, P3 p3) {
    Consumer<T> c = instance -> consumer.accept(instance, p1, p2, p3);
    this.modifiers.add(c);
    return this;
  }

  public <P1, P2, P3, P4> PojoBuilder<T> with(
      Consumer4<T, P1, P2, P3, P4> consumer, P1 p1, P2 p2, P3 p3, P4 p4) {
    Consumer<T> c = instance -> consumer.accept(instance, p1, p2, p3, p4);
    this.modifiers.add(c);
    return this;
  }

  public <P1, P2, P3, P4, P5> PojoBuilder<T> with(
      Consumer5<T, P1, P2, P3, P4, P5> consumer, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
    Consumer<T> c = instance -> consumer.accept(instance, p1, p2, p3, p4, p5);
    this.modifiers.add(c);
    return this;
  }

  public T build() {
    T value = this.instantiator.get();
    this.modifiers.forEach(modifier -> modifier.accept(value));
    this.modifiers.clear();
    return value;
  }

  /** 1 参数 Consumer */
  @FunctionalInterface
  public interface Consumer1<T, P1> {

    /**
     * 接收参数方法
     *
     * @param t 对象
     * @param p1 参数二
     */
    void accept(T t, P1 p1);
  }

  /** 2 参数 Consumer */
  @FunctionalInterface
  public interface Consumer2<T, P1, P2> {

    /**
     * 接收参数方法
     *
     * @param t 对象
     * @param p1 参数一
     * @param p2 参数二
     */
    void accept(T t, P1 p1, P2 p2);
  }

  /** 3 参数 Consumer */
  @FunctionalInterface
  public interface Consumer3<T, P1, P2, P3> {

    /**
     * 接收参数方法
     *
     * @param t 对象
     * @param p1 参数一
     * @param p2 参数二
     * @param p3 参数三
     */
    void accept(T t, P1 p1, P2 p2, P3 p3);
  }

  @FunctionalInterface
  public interface Consumer4<T, P1, P2, P3, P4> {

    /**
     * 接收参数方法
     *
     * @param t 对象
     * @param p1 参数一
     * @param p2 参数二
     * @param p3 参数三
     * @param p4 参数四
     */
    void accept(T t, P1 p1, P2 p2, P3 p3, P4 p4);
  }

  @FunctionalInterface
  public interface Consumer5<T, P1, P2, P3, P4, P5> {

    /**
     * 接收参数方法
     *
     * @param t 对象
     * @param p1 参数一
     * @param p2 参数二
     * @param p3 参数三
     * @param p4 参数四
     * @param p5 参数五
     */
    void accept(T t, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
  }
}
