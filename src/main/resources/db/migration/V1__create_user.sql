CREATE TABLE IF NOT EXISTS users (
    user_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    password text NOT NULL
);

CREATE TABLE IF NOT EXISTS role (
    role_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    role varchar(30) NOT NULL
);

INSERT INTO role (role) VALUES ('ROLE_ADMIN'), ('ROLE_MODER'), ('ROLE_USER');

CREATE TABLE IF NOT EXISTS role_user (
  role_id int REFERENCES role(role_id) ON DELETE CASCADE,
  user_id int REFERENCES users(user_id) ON DELETE CASCADE,
  PRIMARY KEY (role_id, user_id)
);

CREATE INDEX IF NOT EXISTS user_id_ix ON users(user_id);
CREATE INDEX IF NOT EXISTS user_email_ix ON users(email);

