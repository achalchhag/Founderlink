CREATE TABLE users (
  id UUID PRIMARY KEY,
  full_name VARCHAR(150) NOT NULL,
  email VARCHAR(254) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  account_status VARCHAR(20) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT uk_users_email UNIQUE (email),
  CONSTRAINT chk_users_role CHECK (role IN ('ADMIN','FOUNDER','DEVELOPER','DESIGNER','MENTOR','INVESTOR')),
  CONSTRAINT chk_users_account_status CHECK (account_status IN ('ACTIVE','INACTIVE','LOCKED'))
);
