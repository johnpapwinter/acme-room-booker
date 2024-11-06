INSERT INTO bookings (meetingRoom, booked_by, booking_date, start_time, end_time, status)
VALUES
    ('ELMER_FUDD_ROOM', 'elmerfudd@acme.com', '2024-10-12', '09:00:01', '10:00:00', 'SCHEDULED'),
    ('ELMER_FUDD_ROOM', 'wilecoyote@acme.com', '2024-10-13', '10:00:01', '11:00:00', 'SCHEDULED'),
    ('WILE_E_COYOTE_ROOM', 'wilecoyote@acme.com', '2024-10-12', '09:30:01', '10:30:00', 'SCHEDULED');

INSERT INTO acme_users(name, email, role, username, password)
VALUES
    ('Elmer Fudd', 'elmer@acme.com', 'ADMIN', 'elmer', '$2a$10$djimFtNZ1Lh86afIHhE/yuJCF9HEIaIh4ZvZ7G/LlVoWHguUZk2fC'),
    ('Yoshemite Sam', 'sam@acme.com', 'USER', 'sam', '$2a$10$djimFtNZ1Lh86afIHhE/yuJCF9HEIaIh4ZvZ7G/LlVoWHguUZk2fC');

