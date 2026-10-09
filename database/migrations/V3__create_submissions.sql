-- Migration V3: Create Submissions Table

CREATE TABLE IF NOT EXISTS submissions (
    id BIGSERIAL PRIMARY KEY,
    exercise_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    submitted_code TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    feedback TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT,
    
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_submissions_exercise_id ON submissions(exercise_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student_id ON submissions(student_id);

-- Row Level Security (optional but matching pattern if previous tables have it)
ALTER TABLE submissions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow all operations on submissions" 
ON submissions 
FOR ALL 
USING (true) 
WITH CHECK (true);
