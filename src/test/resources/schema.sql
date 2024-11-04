CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    meetingRoom VARCHAR(255) NOT NULL,
    booked_by VARCHAR(255) NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(255) NULL,
    acme_user_id BIGINT NOT NULL,
    FOREIGN KEY (acme_user_id) REFERENCES acme_users(id)
);


CREATE TABLE IF NOT EXISTS acme_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
);

