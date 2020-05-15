CREATE schema audit;
REVOKE CREATE ON schema audit FROM public;

CREATE TABLE audit.logged_actions
(
    schema_name   varchar(45) NOT NULL,
    TABLE_NAME    varchar(45) NOT NULL,
    user_name     varchar(20),
    action_tstamp TIMESTAMP WITH TIME zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    action        char        NOT NULL CHECK (action IN ('I', 'D', 'U')),
    original_data text,
    new_data      text,
    query         text
)
WITH (fillfactor=100);

REVOKE ALL ON audit.logged_actions FROM public;

GRANT SELECT ON audit.logged_actions TO public;
CREATE INDEX logged_actions_schema_table_idx
    ON audit.logged_actions (((schema_name || '.' || TABLE_NAME):: TEXT) );
CREATE INDEX logged_actions_action_tstamp_idx
    ON audit.logged_actions (action_tstamp);

CREATE INDEX logged_actions_action_idx
    ON audit.logged_actions (action);

CREATE
OR
REPLACE FUNCTION audit.if_modified_func() RETURNS TRIGGER AS $body$
    DECLARE
    v_old_data json;
v_new_data json;
BEGIN
    IF
    (TG_OP = 'UPDATE')
    THEN
        v_old_data := row_to_json(OLD);
v_new_data := row_to_json(NEW);
INSERT INTO audit.logged_actions (schema_name, table_name, user_name, action, original_data, new_data, query)
VALUES (TG_TABLE_SCHEMA::TEXT, TG_TABLE_NAME::TEXT, session_user::TEXT, substring(TG_OP, 1, 1), v_old_data, v_new_data, current_query());
RETURN NEW;
ELSIF
    (TG_OP = 'DELETE')
    THEN
        v_old_data := row_to_json(OLD);
INSERT INTO audit.logged_actions (schema_name, table_name, user_name, action, original_data, query)
VALUES (TG_TABLE_SCHEMA::TEXT, TG_TABLE_NAME::TEXT, session_user::TEXT, substring(TG_OP, 1, 1), v_old_data, current_query());
RETURN OLD;
ELSIF
    (TG_OP = 'INSERT')
    THEN
        v_new_data := row_to_json(NEW);
INSERT INTO audit.logged_actions (schema_name, table_name, user_name, action, new_data, query)
VALUES (TG_TABLE_SCHEMA::TEXT, TG_TABLE_NAME::TEXT, session_user::TEXT, substring(TG_OP, 1, 1), v_new_data, current_query());
RETURN NEW;
ELSE
        RAISE WARNING '[AUDIT.IF_MODIFIED_FUNC] - Other action occurred: %, at %',TG_OP,now();
RETURN NULL;
END IF;

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING '[AUDIT.IF_MODIFIED_FUNC] - UDF ERROR [DATA EXCEPTION] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
RETURN NULL;
WHEN unique_violation THEN
        RAISE WARNING '[AUDIT.IF_MODIFIED_FUNC] - UDF ERROR [UNIQUE] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
RETURN NULL;
WHEN OTHERS THEN
        RAISE WARNING '[AUDIT.IF_MODIFIED_FUNC] - UDF ERROR [OTHER] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
RETURN NULL;
END;
$body$
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = pg_catalog, audit;