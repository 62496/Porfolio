-- ================================
-- DATABASE
-- ================================
CREATE DATABASE IF NOT EXISTS don5;
USE don5;

-- ================================
-- USERS
-- ================================
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE
);

ALTER TABLE users ADD CONSTRAINT unique_username UNIQUE (username);

-- ================================
-- MEETINGS
-- ================================
CREATE TABLE IF NOT EXISTS meetings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_a BIGINT NOT NULL,
  user_b BIGINT NOT NULL,
  interest VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_meeting_user_a
    FOREIGN KEY (user_a) REFERENCES users(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_meeting_user_b
    FOREIGN KEY (user_b) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_meetings_user_a ON meetings(user_a);
CREATE INDEX idx_meetings_user_b ON meetings(user_b);
CREATE INDEX idx_meetings_users ON meetings(user_a, user_b);

-- ================================
-- POINTS
-- ================================
CREATE TABLE IF NOT EXISTS points (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  meeting_id BIGINT NOT NULL,
  amount INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_points_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_points_meeting
    FOREIGN KEY (meeting_id) REFERENCES meetings(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_points_user ON points(user_id);
CREATE INDEX idx_points_meeting ON points(meeting_id);
