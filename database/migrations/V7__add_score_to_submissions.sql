ALTER TABLE submissions
ADD COLUMN score INTEGER;

ALTER TABLE submissions
ADD CONSTRAINT check_score_range
CHECK (score >= 0 AND score <= 100);
