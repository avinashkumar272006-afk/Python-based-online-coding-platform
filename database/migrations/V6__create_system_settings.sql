CREATE TABLE IF NOT EXISTS system_settings (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by INTEGER,

    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_system_settings_key ON system_settings(setting_key);

-- Seed initial settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('platform_name', 'Python Coding Platform', 'The display name of the platform'),
('allow_registration', 'true', 'Whether new users can register (true/false)'),
('maintenance_mode', 'false', 'Enable maintenance mode (true/false)'),
('default_exercise_difficulty', 'EASY', 'Default difficulty for new exercises (EASY/MEDIUM/HARD)')
ON CONFLICT (setting_key) DO NOTHING;
