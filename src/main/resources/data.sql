INSERT IGNORE INTO `card` (`id`, `created_by`, `created_date_time`, `last_modified_by`, `last_modified_date_time`, `color`, `description`, `name`, `status`) VALUES
    (1, 'admin@cards.com', '2023-08-28 09:43:43.000000', 'admin@cards.com', '2023-08-28 09:43:43.000000', '#75821E', 'Title 1', 'Name', 'TODO'),
    (2, 'admin@cards.com', '2023-08-28 09:44:14.000000', 'admin@cards.com', '2023-08-28 09:44:14.000000', '#75821E', 'Title 2', 'Name', 'TODO'),
    (3, 'member@cards.com', '2023-08-28 12:44:33.000000', 'member@cards.com', '2023-08-28 12:44:33.000000', '#111111', 'Title 3', 'Name', 'TODO'),
    (4, 'member@cards.com', '2023-08-28 12:44:43.000000', 'member@cards.com', '2023-08-28 12:44:43.000000', '#111111', 'Title 4', 'Name', 'TODO');

INSERT IGNORE INTO `user` (`id`, `email_address`, `password`, `role`) VALUES
    (1, 'admin@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'ADMIN'),     #password is password
    (3, 'member@cards.com', '$2a$10$AUE6Db.6s4wSgeB3T89PlucPe7.2ql2lerT3qJ46L9Zrc/0BdTN8u', 'MEMBER');   #password is password

