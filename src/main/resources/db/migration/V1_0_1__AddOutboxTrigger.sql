CREATE TRIGGER outbox1 AFTER INSERT ON users FOR EACH ROW CALL "de.cmuellerke.demo.triggers.AfterInsertTrigger";
