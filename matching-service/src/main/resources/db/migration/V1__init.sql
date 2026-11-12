CREATE TABLE IF NOT EXISTS match_records (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    target_id UUID NOT NULL,
    match_type VARCHAR(50) NOT NULL,
    target_name VARCHAR(200) NOT NULL,
    target_role VARCHAR(200),
    target_bio TEXT,
    target_industry VARCHAR(100),
    match_score INT NOT NULL,
    synergy_summary TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_match_records_user_id ON match_records(user_id);
