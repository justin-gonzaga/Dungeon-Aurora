package dungeonmania.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Single event manager yikes
 * 
 * Usage:
 * <pre>
 * class MySubject {
 *   private Semy<String> myEventSemy = new Semy<>();
 * 
 *   public void myFunctionThatDoesSomething() {
 *     this.myEventSemy.emit("event happens!");
 *   }
 * 
 *   public onMyEvent(Observer<String> o) {
 *     this.myEventSemy.bind(o);
 *   }
 * }
 * 
 * class MyObserver {
 *   public MyObserver(Subject s) {
 *     s.onMyEvent(this::onMyEvent);
 *   }
 *   public onMyEvent(String message) {
 *     System.out.println("Event was triggered! Message=" + message);
 *   }
 * }
 * </pre>
 */
public class Semy<T> {
    private List<Observer<T>> observers = new ArrayList<>();
    
    public interface Observer<U> {
        public void onEvent(U data);
    }

    /**
     * adds an observer (an object whoes onEvent method will be called when
     * semy.emit is called)
     * @param observer
     */
    public void bind(Observer<T> observer) {
        this.observers.add(observer);
    }

    /**
     * notifies the observers
     * @param data
     */
    public void emit(T data) {
        for (Observer<T> o : this.observers) {
            o.onEvent(data);
        }
    }
}
