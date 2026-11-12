CREATE TABLE startups (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(3000),
    industry VARCHAR(100),
    stage VARCHAR(50),
    looking_for VARCHAR(500),
    website_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_startups_stage CHECK (stage IS NULL OR stage IN ('IDEA','MVP','EARLY_STAGE','GROWTH','SCALING'))
);
