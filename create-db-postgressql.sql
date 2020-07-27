# Execute the create db as a separate sql as create db can not run in transaction space
CREATE DATABASE ref_price_dev
    WITH
    OWNER = postgres
    ENCODING = 'UTF-8'
    TEMPLATE template0
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    CONNECTION LIMIT = -1;
# Create the users and grant permission.

CREATE ROLE ref_price_user NOSUPERUSER NOCREATEROLE NOINHERIT LOGIN PASSWORD 'password';

GRANT ALL ON DATABASE ref_price_dev TO ref_price_user;
GRANT ALL PRIVILEGES ON DATABASE ref_price_dev TO ref_price_user;
GRANT ALL ON DATABASE ref_price_dev TO postgres;
