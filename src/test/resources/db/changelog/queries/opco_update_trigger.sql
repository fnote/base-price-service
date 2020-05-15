CREATE
OR
REPLACE FUNCTION update_opco_func() RETURNS TRIGGER AS $body$
BEGIN
    IF
    (OLD.is_current = true)
    THEN
INSERT INTO opco(id, opco_number, workday_name, country_code, market, sap_entity_id, sus_entity_id, adp_pay_group, adp_location_id, currency,
                   timezone, target_pieces_per_trip, active_status, is_locked, is_current, saved_by,
                   saved_time, version)
VALUES (OLD.id, NEW.opco_number, NEW.workday_name, NEW.country_code, NEW.market, NEW.sap_entity_id, NEW.sus_entity_id, NEW.adp_pay_group, NEW.adp_location_id,
        NEW.currency, NEW.timezone, NEW.target_pieces_per_trip, NEW.active_status, false, true,
        NEW.saved_by, NEW.saved_time, (OLD.version + 1));
OLD.is_current = false;
RETURN OLD;
ELSE
        RETURN NEW;
END IF;
END;
$body$ LANGUAGE plpgsql;