CREATE TABLE profiles (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE,
  headline VARCHAR(150),
  bio VARCHAR(2000),
  location VARCHAR(150),
  skills VARCHAR(1000),
  experience_years INTEGER,
  website_url VARCHAR(500),
  linkedin_url VARCHAR(500),
  github_url VARCHAR(500),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT chk_profiles_experience_years CHECK (experience_years IS NULL OR experience_years >= 0)
);
