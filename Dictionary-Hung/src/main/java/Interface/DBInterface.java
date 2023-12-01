package Interface;

public interface DBInterface {
    String db_url = "jdbc:mysql://127.0.0.1:3306/dictionary";
    String db_pass = "Hung05042004@moc";
    String db_user = "root";

    public abstract void connectdataBase();
    public abstract String search(String word);
}
