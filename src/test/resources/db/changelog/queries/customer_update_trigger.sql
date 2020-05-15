CREATE
OR
REPLACE FUNCTION update_customer_func() RETURNS TRIGGER AS $body$
BEGIN
    IF
    (OLD.is_current = true)
    THEN
        INSERT INTO stop_classification(id, opco_number, customer_number, customer_name, stop_class, stop_attributes, is_locked, is_current, saved_by, saved_time, version)
        VALUES (OLD.id, NEW.opco_number, NEW.customer_number, NEW.customer_name, NEW.stop_class, NEW.stop_attributes, false, true, NEW.saved_by, NEW.saved_time, (OLD.version + 1));
        OLD.is_current = false;
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$body$ LANGUAGE plpgsql;