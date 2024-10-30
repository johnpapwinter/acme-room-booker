CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    room VARCHAR(255) NOT NULL,
    booked_by VARCHAR(255) NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(255) NULL
);
