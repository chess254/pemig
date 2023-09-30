INSERT IGNORE INTO `loan` (`id`, `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`, `description`, `name`, `status`) VALUES
    (10, 'admin@cards.com', '2023-08-28 09:43:43.000000', 'admin@cards.com', '2023-08-28 09:43:43.000000', 'Title 1', 'Name', 'TODO'),
    (20, 'admin@cards.com', '2023-08-28 09:44:14.000000', 'admin@cards.com', '2023-08-28 09:44:14.000000', 'Title 2', 'Name', 'TODO'),
    (30, 'member@cards.com', '2023-08-28 12:44:33.000000', 'member@cards.com', '2023-08-28 12:44:33.000000', 'Title 3', 'Name', 'TODO'),
    (40, 'member@cards.com', '2023-08-28 12:44:43.000000', 'member@cards.com', '2023-08-28 12:44:43.000000', 'Title 4', 'Name', 'TODO');
# INSERT IGNORE INTO `card` (`id`, `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`, `color`, `description`, `name`, `status`) VALUES
#     (10, 'admin@cards.com', '2023-08-28 09:43:43.000000', 'admin@cards.com', '2023-08-28 09:43:43.000000', '#75821E', 'Title 1', 'Name', 'TODO'),
#     (20, 'admin@cards.com', '2023-08-28 09:44:14.000000', 'admin@cards.com', '2023-08-28 09:44:14.000000', '#75821E', 'Title 2', 'Name', 'TODO'),
#     (30, 'member@cards.com', '2023-08-28 12:44:33.000000', 'member@cards.com', '2023-08-28 12:44:33.000000', '#111111', 'Title 3', 'Name', 'TODO'),
#     (40, 'member@cards.com', '2023-08-28 12:44:43.000000', 'member@cards.com', '2023-08-28 12:44:43.000000', '#111111', 'Title 4', 'Name', 'TODO');
INSERT IGNORE INTO `user` (`id`, `email_address`, `password`, `role`, created_by, last_modified_by, last_modified_date_time, county, description, first_name, middle_name, last_name, id_number, occupation, phone_number, primary_account_number, profile_pic, verified, birth_date) VALUES
    (10, 'admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'ADMIN', 'admin@cards.com', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213819', 'admins occupation', 0721542746, 1234567890, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000');
#     (11, 'client@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'CLIENT'),
#     (12, 'client_manager@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'CLIENT_MANAGER'),
#     (13, 'account_manager@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'ACCOUNT_MANAGER'),
#     (14, 'payment_admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'PAYMENT_ADMIN'),
#     (15, 'reporting_admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'REPORTING_AND_ANALYTICS_ADMIN'),
#     (16, 'loan_agent@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'LOAN_AGENT_ADMIN');
#password is password

INSERT IGNORE INTO `loan_package` (`id`, `duration`,`rate`, `minimum`, `maximum`, `description`, `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`) VALUES
    (1,  30, 13.0, 2000, 5000,'basic', 'admin@cards.com', '2023-08-28 12:44:43.000000', 'admin@cards.com','2023-08-28 12:44:43.000000'),
    (2,  90, 6.0, 5001, 10000,'bronze', 'admin@cards.com', '2023-08-28 12:44:43.000000', 'admin@cards.com','2023-08-28 12:44:43.000000'),
    (3,  180, 6.0, 10001, 20000,'premium', 'admin@cards.com', '2023-08-28 12:44:43.000000', 'admin@cards.com','2023-08-28 12:44:43.000000');



