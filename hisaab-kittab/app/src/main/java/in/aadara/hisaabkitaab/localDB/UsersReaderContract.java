package in.aadara.hisaabkitaab.localDB;

import android.provider.BaseColumns;

/**
 * Created by umashankarpathak on 15/01/18.
 */

public class UsersReaderContract {
    public UsersReaderContract(){}
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "customers";
        public static final String COLUMN_NAME= "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_MOBILE = "mobile";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_AMOUNT = "amount";
    }
}

