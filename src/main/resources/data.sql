# DELETE FROM user;
# DELETE FROM loan;
INSERT IGNORE INTO `user` (`id`, `email_address`, `password`, `role`, `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`, `county`, `description`, `first_name`, `middle_name`, `last_name`, `id_number`, `occupation`, `phone_number`, `primary_account_number`, `profile_pic`, `verified`, `birth_date`, `secondary_account_number`, `loan_account_number`)
VALUES
    ('1','admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'ADMIN', 'admin@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213819', 'admins occupation', 0721542746, 1234567890, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678'),
    ('2','client@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'CLIENT', 'client@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213818', 'admins occupation', 0721542747, 1234567891, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678'),
    ('3','client_manager@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'CLIENT_MANAGER', 'clientmanager@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213817', 'admins occupation', 0721542748, 1234567892, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678'),
    ('4','account_manager@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'ACCOUNT_MANAGER', 'accountmanager@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213816', 'admins occupation', 0721542749, 1234567893, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678'),
    ('5','payment_admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'PAYMENT_ADMIN', 'paymentadmin@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213815', 'admins occupation', 0721542740, 1234567894, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678'),
    ('6','reporting_admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'REPORTING_AND_ANALYTICS_ADMIN', 'reportingadmin@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213814', 'admins occupation', 0721542741, 1234567895, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678'),
    ('7','loan_agent@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'LOAN_AGENT_ADMIN', 'loanagent@cards.com','2023-08-28 09:43:43.000000', 'admin@cards.com','2023-08-28 09:43:43.000000', 'Nairobi', 'test description', 'admin', 'admid', 'adlast', '25213813', 'admins occupation', 0721542742, 1234567896, 'http://prof.pic', 1, '2023-08-28 09:43:43.000000', '12345678', '12345678');
INSERT IGNORE INTO `loan` ( `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`, `description`, `name`, `status`, `customer_id`, `time`, `rate`) VALUES
    ('admin@cards.com', '2023-08-28 09:43:43.000000', 'admin@cards.com', '2023-08-28 09:43:43.000000', 'Title 1', 'Name', 'TODO', 1, 3, 13),
    ('admin@cards.com', '2023-08-28 09:44:14.000000', 'admin@cards.com', '2023-08-28 09:44:14.000000', 'Title 2', 'Name', 'TODO', 2, 3, 13),
    ('member@cards.com', '2023-08-28 12:44:33.000000', 'member@cards.com', '2023-08-28 12:44:33.000000', 'Title 3', 'Name', 'TODO', 3, 3, 13),
    ('member@cards.com', '2023-08-28 12:44:43.000000', 'member@cards.com', '2023-08-28 12:44:43.000000', 'Title 4', 'Name', 'TODO', 4, 3, 13);

#password is password

INSERT IGNORE INTO `loan_package` (`id`, `duration`,`rate`, `minimum`, `maximum`, `description`, `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`) VALUES
    (1,  30, 13.0, 2000, 5000,'basic', 'admin@cards.com', '2023-08-28 12:44:43.000000', 'admin@cards.com','2023-08-28 12:44:43.000000'),
    (2,  90, 6.0, 5001, 10000,'bronze', 'admin@cards.com', '2023-08-28 12:44:43.000000', 'admin@cards.com','2023-08-28 12:44:43.000000'),
    (3,  180, 6.0, 10001, 20000,'premium', 'admin@cards.com', '2023-08-28 12:44:43.000000', 'admin@cards.com','2023-08-28 12:44:43.000000');



