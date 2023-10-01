CREATE TABLE IF NOT EXISTS `loan` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_by` varchar(50) NOT NULL,
    `created_date_time` datetime(6) NOT NULL,
    `last_modified_by` varchar(50) DEFAULT NULL,
    `last_modified_date_time` datetime(6) DEFAULT NULL,
    `description` varchar(100) DEFAULT NULL,
    `name` varchar(50) NOT NULL,
    `status` enum( 'APPLIED', 'TODO', 'KYC', 'PROCESSING', 'DENIED', 'APPROVED', 'DISBURSED', 'CREDITED', 'IN_PROGRESS', 'DONE') NOT NULL DEFAULT 'TODO',
    PRIMARY KEY (`id`)

    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- CREATE TABLE IF NOT EXISTS `user` (
--                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
--                         `email_address` varchar(255) NOT NULL,
--                         `password` varchar(255) NOT NULL,
--                         `role` enum('ADMIN', 'CLIENT', 'CLIENT_MANAGER', 'ACCOUNT_MANAGER', 'PAYMENT_ADMIN', 'REPORTING_AND_ANALYTICS_ADMIN', 'LOAN_AGENT_ADMIN') NOT NULL,
--     `created_by` varchar(50) NOT NULL,
--     `created_date_time` datetime(6) NOT NULL,
--     `last_modified_by` varchar(50) DEFAULT NULL,
--     `last_modified_date_time` datetime(6) DEFAULT NULL,
--                         PRIMARY KEY (`id`),
--                         UNIQUE KEY `UK_d0ar1h7wcp7ldy6qg5859sol6` (`email_address`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

create table if not exists user
(
    id bigint auto_increment primary key,
    email_address           varchar(255)                                                                                                                                  not null,
    password                varchar(255)                                                                                                                                  not null,
    role                    enum ('ACCOUNT_MANAGER', 'ADMIN', 'CLIENT', 'CLIENT_MANAGER', 'LOAN_AGENT_ADMIN', 'MEMBER', 'PAYMENT_ADMIN', 'REPORTING_AND_ANALYTICS_ADMIN') null,
    created_by              varchar(50)                                                                                                                                   not null,
    created_date_time       datetime(6)                                                                                                                                   null,
    last_modified_by        varchar(50)                                                                                                                                   null,
    last_modified_date_time datetime(6)                                                                                                                                   null,
    county                  varchar(255)                                                                                                                                  null,
    description             varchar(100)                                                                                                                                  null,
    first_name              varchar(255)                                                                                                                                  null,
    id_number               varchar(255)                                                                                                                                  null,
    last_name               varchar(255)                                                                                                                                  null,
    middle_name             varchar(255)                                                                                                                                  null,
    occupation              varchar(255)                                                                                                                                  null,
    phone_number            varchar(10)                                                                                                                                   null,
    primary_account_number  bigint                                                                                                                                        null,
    secondary_account_number  bigint                                                                                                                                      null,
    loan_account_number  bigint                                                                                                                                           null,
    profile_pic             varchar(255)                                                                                                                                  null,
    verified                bit                                                                                                                                           not null,
    birth_date              datetime(6)                                                                                                                                   null,
    constraint UK_4bgmpi98dylab6qdvf9xyaxu4
        unique (phone_number),
    constraint UK_893geq5bxk3o6gh642gh6cyic
        unique (id_number),
    constraint UK_d0ar1h7wcp7ldy6qg5859sol6
        unique (email_address)
);


CREATE TABLE IF NOT EXISTS `loan_package` (
    `id` int(20) NOT NULL AUTO_INCREMENT ,
    `duration` int(5) NOT NULL ,
    `rate` float NOT NULL ,
    `minimum` int(7) NOT NULL,
    `maximum` int(7) NOT NULL,
    `description` varchar(150),
    `created_by` varchar(50) NOT NULL,
    `created_date_time` datetime(6) NOT NULL,
    `last_modified_by` varchar(50) DEFAULT NULL,
    `last_modified_date_time` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)

)
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