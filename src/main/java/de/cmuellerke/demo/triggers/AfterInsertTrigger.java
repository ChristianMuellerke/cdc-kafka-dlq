package de.cmuellerke.demo.triggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.h2.api.Trigger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AfterInsertTrigger implements Trigger {

    @Override
    public void close() throws SQLException {
        // TODO Auto-generated method stub
        Trigger.super.close();
    }

    /**
     * This method is called for each triggered action.
     *
     * @param conn a connection to the database
     * @param oldRow the old row, or null if no old row is available (for
     *            INSERT)
     * @param newRow the new row, or null if no new row is available (for
     *            DELETE)
     * @throws SQLException if the operation must be undone
     */
    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        System.out.println("TRIGGER FIRED?");
        log.info("triggered?");
         UUID insertedId = null;
        if (newRow != null) {
            Object o = newRow[0];
            log.info("O:{} V={}", o.getClass(), o.toString());
            insertedId = UUID.fromString(o.toString());
        }
        if (oldRow != null) {
            log.info("OldRow provided...what have you done?");
        }
        PreparedStatement prep = conn.prepareStatement(
                "INSERT INTO users_outbox (id, tspInserted) VALUES (?,?)");
        prep.setObject(1, insertedId);
        prep.setTimestamp(2, Timestamp.from(Instant.now()));
        prep.execute();
     }
// INSERT INTO USERS (id, vorname, nachname) VALUES ('b8bdcf00-4d39-4591-9413-63e98d66128e', 'ERNST', 'MEI')    
    @Override
    public void init(Connection arg0, String arg1, String arg2, String arg3, boolean arg4, int arg5)
            throws SQLException {
        // TODO Auto-generated method stub
        Trigger.super.init(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void remove() throws SQLException {
        // TODO Auto-generated method stub
        Trigger.super.remove();
    }

}
