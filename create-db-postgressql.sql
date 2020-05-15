# Execute the create db as a separate sql as create db can not run in transaction space
CREATE DATABASE payplus_dev
    WITH
    OWNER = postgres
    ENCODING = 'UTF-8'
    TEMPLATE template0
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    CONNECTION LIMIT = -1;
# Create the users and grant permission.

CREATE ROLE payplus NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN PASSWORD 'password';
GRANT ALL ON DATABASE payplus TO payplus;
GRANT ALL PRIVILEGES ON DATABASE payplus TO payplus;
GRANT ALL ON DATABASE payplus TO postgres;
