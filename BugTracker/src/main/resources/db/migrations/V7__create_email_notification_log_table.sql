CREATE TABLE email_notification_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT,
    recipient_email VARCHAR(150) NOT NULL,
    subject VARCHAR(300) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_sent BOOLEAN DEFAULT FALSE,
    error_message TEXT,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);