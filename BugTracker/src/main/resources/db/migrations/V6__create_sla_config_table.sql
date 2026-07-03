CREATE TABLE sla_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    priority ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL UNIQUE,
    resolution_hours INT NOT NULL
);

INSERT INTO sla_config (priority, resolution_hours) VALUES
('CRITICAL', 2),
('HIGH', 6),
('MEDIUM', 24),
('LOW', 48);