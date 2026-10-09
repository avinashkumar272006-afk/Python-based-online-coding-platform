-- Migration V8: Create Challenges and Challenge Participants Tables

CREATE TABLE IF NOT EXISTS challenges (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    created_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_challenge_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_challenge_difficulty CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    CONSTRAINT chk_challenge_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT chk_challenge_dates CHECK (start_date IS NULL OR end_date IS NULL OR start_date <= end_date)
);

CREATE INDEX IF NOT EXISTS idx_challenges_status ON challenges(status);
CREATE INDEX IF NOT EXISTS idx_challenges_created_by ON challenges(created_by);

CREATE TABLE IF NOT EXISTS challenge_participants (
    id BIGSERIAL PRIMARY KEY,
    challenge_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'JOINED',
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_cp_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_challenge_student UNIQUE (challenge_id, student_id),
    CONSTRAINT chk_cp_status CHECK (status IN ('JOINED', 'COMPLETED', 'DROPPED'))
);

CREATE INDEX IF NOT EXISTS idx_cp_challenge_id ON challenge_participants(challenge_id);
CREATE INDEX IF NOT EXISTS idx_cp_student_id ON challenge_participants(student_id);

-- Optional Row Level Security (if used in previous tables)
ALTER TABLE challenges ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Allow all operations on challenges" ON challenges;

ALTER TABLE challenge_participants ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Allow all operations on challenge_participants" ON challenge_participants;
