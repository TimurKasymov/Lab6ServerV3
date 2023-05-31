package src.db;

public class SeqNames {
    public final static String productSeq = "SELECT nextval('products_id_seq') as num;";
    public final static String coordSeq = "SELECT nextval('coordinates_id_seq') as num;";
    public final static String userSeq = "SELECT nextval('users_id_seq') as num;";
    public final static String orgSeq = "SELECT nextval('organizations_id_seq') as num;";
    public final static String internalSeq = "SELECT nextval('internaldatahistory_id_seq') as num;";
}
