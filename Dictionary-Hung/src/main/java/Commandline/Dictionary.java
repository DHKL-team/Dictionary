package Commandline;

public class

Dictionary extends Trie {
    /* Bill Pugh Singleton
    - Khi Singleton được tải vào bộ nhớ thì SingletonHelper chưa được tải vào.
    - Nó chỉ được tải khi và chỉ khi phương thức getInstance() được gọi.
    - Với cách này tránh được lỗi cơ chế khởi tạo instance của Singleton trong Multi-Thread,
    performance cao do tách biệt được quá trình xử lý
     */
    private Dictionary() {

    }

    public static Dictionary getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final Dictionary INSTANCE = new Dictionary();
    }
}