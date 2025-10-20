CREATE TABLE users(
    user_id UUID,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    user_status VARCHAR(50) NOT NULL,
    user_type VARCHAR(50) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY(user_id),
    CONSTRAINT uq_users_email UNIQUE(email),
    CONSTRAINT uq_users_cpf UNIQUE(cpf)
);

CREATE TABLE roles(
    role_id UUID,
    role_name VARCHAR(30) NOT NULL,
    
    CONSTRAINT pk_role PRIMARY KEY(role_id)
);

CREATE TABLE user_role(
    user_id UUID,
    role_id UUID,

    CONSTRAINT pk_user_role PRIMARY KEY(user_id, role_id),
    CONSTRAINT fk_user_role FOREIGN KEY(user_id) REFERENCES users(user_id),
    CONSTRAINT fk_role_user FOREIGN KEY(role_id) REFERENCES roles(role_id)
);