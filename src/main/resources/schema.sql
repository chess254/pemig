CREATE TABLE IF NOT EXISTS `loan` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_by` varchar(50) NOT NULL,
    `created_date_time` datetime(6) NOT NULL,
    `last_modified_by` varchar(50) DEFAULT NULL,
    `last_modified_date_time` datetime(6) DEFAULT NULL,
    `color` varchar(7) NOT NULL,
    `description` varchar(100) DEFAULT NULL,
    `name` varchar(50) NOT NULL,
    `status` enum('DONE','IN_PROGRESS','TODO') NOT NULL DEFAULT 'TODO',
    PRIMARY KEY (`id`)

    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `email_address` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `role` enum('ADMIN','MEMBER') NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_d0ar1h7wcp7ldy6qg5859sol6` (`email_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
-- --
-- -- Indexes for table `user`
-- --
-- ALTER TABLE `user`
--     ADD PRIMARY KEY (`id`),
--   ADD UNIQUE KEY `UK_d0ar1h7wcp7ldy6qg5859sol6` (`email_address`);
--
-- --
-- -- AUTO_INCREMENT for dumped tables
-- --
--
-- --
-- -- AUTO_INCREMENT for table `user`
-- --
-- ALTER TABLE `user`
--     MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
-- COMMIT;