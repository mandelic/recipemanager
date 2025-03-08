CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username TEXT,
    password TEXT,
    role TEXT
);

INSERT INTO users(username, password, role) VALUES ('admin', '$2a$11$Xhln5JuapOW9Tz51YUL8POiFES.GgOiVpcuI2raFj6sQcAxV4GdjW', 'ROLE_ADMIN'); /* admin */